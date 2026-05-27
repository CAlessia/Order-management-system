package dataAccess;

import model.Address;
import model.Client;
import Connection.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * DAO class for managing Client records and their associated Address records.
 */
public class ClientDAO extends AbstractDAO<Client> {
    /**
     * Inserts a new Client into the database.
     *
     * @param client The Client object to insert.
     * @throws SQLException if a database error occurs.
     */
    public void insertClient(Client client) throws SQLException {
        String sql = "INSERT INTO client (name, email, addressId, telephone) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getEmail());
            ps.setInt(3, client.getAddressId());
            ps.setString(4, client.getTelephone());
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setClientId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Deletes a Client from the database by their ID.
     *
     * @param clientId The ID of the Client to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteClient(int clientId) throws SQLException {
        String sql = "DELETE FROM client WHERE clientId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves a Client along with their Address from the database by client ID.
     *
     * @param clientId The ID of the Client.
     * @return The Client object with the specified ID, or null if not found.
     * @throws SQLException if a database error occurs.
     */
    public Client getClientById(int clientId) throws SQLException {
        String sql = "SELECT client.*, address.street, address.number, address.city, address.judet " +
                "FROM client " +
                "JOIN address ON client.addressId = address.addressId " +
                "WHERE clientId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Address address = new Address(
                            rs.getInt("addressId"),
                            rs.getString("street"),
                            rs.getInt("number"),
                            rs.getString("city"),
                            rs.getString("judet")
                    );
                    return new Client(
                            rs.getInt("clientId"),
                            rs.getString("name"),
                            rs.getString("email"),
                            address,
                            rs.getString("telephone")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all Clients along with their Addresses from the database.
     *
     * @return A list of all Clients.
     * @throws SQLException if a database error occurs.
     */
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT client.*, address.street, address.number, address.city, address.judet " +
                "FROM client " +
                "JOIN address ON client.addressId = address.addressId";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Address address = new Address(
                        rs.getInt("addressId"),
                        rs.getString("street"),
                        rs.getInt("number"),
                        rs.getString("city"),
                        rs.getString("judet")
                );
                clients.add(new Client(
                        rs.getInt("clientId"),
                        rs.getString("name"),
                        rs.getString("email"),
                        address,
                        rs.getString("telephone")
                ));
            }
        }
        return clients;
    }

    /**
     * Updates an existing Client in the database.
     *
     * @param client The Client object with updated data.
     */
    public void update(Client client) {
        String sql = "UPDATE client SET name = ?, email = ?, telephone = ?, addressId = ? WHERE clientId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getTelephone());
            ps.setInt(4, client.getAddressId());
            ps.setInt(5, client.getClientId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating client", e);
        }
    }

    /**
     * Updates only the telephone and/or email of a Client.
     *
     * @param clientId The ID of the Client to update.
     * @param newTelephone The new telephone number (nullable).
     * @param newEmail The new email (nullable).
     * @throws SQLException if a database error occurs.
     */
    public void updateTelephoneOrEmail(int clientId, String newTelephone, String newEmail) throws SQLException {
        StringBuilder updateQuery = new StringBuilder("UPDATE client SET ");
        List<Object> params = new ArrayList<>();
        if (newTelephone != null) {
            updateQuery.append("telephone = ?, ");
            params.add(newTelephone);
        }
        if (newEmail != null) {
            updateQuery.append("email = ?, ");
            params.add(newEmail);
        }
        updateQuery.setLength(updateQuery.length() - 2);
        updateQuery.append(" WHERE clientId = ?");
        params.add(clientId);
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.executeUpdate();
        }
    }

    /**
     * Updates both a Client and their associated Address in a single transaction.
     * Inserts the Address if it's new.
     *
     * @param client The Client with updated Address.
     * @return true if the update succeeded, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    public boolean updateClientWithAddress(Client client) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            AddressDAO addressDAO = new AddressDAO();
            if (client.getAddress().getId() == 0) {
                int addressId = addressDAO.insertAddress(client.getAddress());
                client.getAddress().setId(addressId);
            } else {
                addressDAO.update(client.getAddress());
            }
            String sql = "UPDATE client SET name=?, email=?, telephone=?, addressId=? WHERE clientId=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, client.getName());
                ps.setString(2, client.getEmail());
                ps.setString(3, client.getTelephone());
                ps.setInt(4, client.getAddress().getId());
                ps.setInt(5, client.getClientId());
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    /**
     * Updates the Address of a Client by address ID.
     *
     * @param clientId The ID of the Client.
     * @param newAddress The new Address.
     * @throws SQLException if a database error occurs.
     */
    public void updateAddress(int clientId, Address newAddress) throws SQLException {
        AddressDAO addressDAO = new AddressDAO();
        addressDAO.update(newAddress);
        String sql = "UPDATE client SET addressId = ? WHERE clientId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAddress.getId());
            ps.setInt(2, clientId);
            ps.executeUpdate();
        }
    }

    /**
     * Updates a Client's details including telephone, email, and optionally address.
     *
     * @param clientId The ID of the Client.
     * @param newTelephone The new telephone (nullable).
     * @param newEmail The new email (nullable).
     * @param newAddress The new Address (nullable).
     * @throws SQLException if a database error occurs.
     */
    public void updateClientDetails(int clientId, String newTelephone, String newEmail, Address newAddress) throws SQLException {
        if (newAddress != null) {
            updateAddress(clientId, newAddress);
        }
        updateTelephoneOrEmail(clientId, newTelephone, newEmail);
    }
}
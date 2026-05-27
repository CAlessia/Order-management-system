package businessLayer;

import dataAccess.AddressDAO;
import model.Address;
import model.Client;
import dataAccess.ClientDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Business Logic Layer for handling client-related operations.
 * Contains validation and calls to data access methods.
 */
public class ClientBLL {
    private final ClientDAO clientDAO;

    public ClientBLL() {
        this.clientDAO = new ClientDAO();
    }

    /**
     * Adds a new client after validating required fields.
     *
     * @param client The client to add.
     * @throws IllegalArgumentException If required fields are missing.
     * @throws SQLException If a database access error occurs.
     */
    public void addClient(Client client) throws IllegalArgumentException, SQLException {
        if (client.getName() == null || client.getEmail() == null) {
            throw new IllegalArgumentException("Name and Email are required.");
        }
        clientDAO.insertClient(client);
    }

    /**
     * Deletes a client from the database by ID.
     *
     * @param id The ID of the client to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteClient(int id) throws SQLException {
        clientDAO.deleteClient(id);
    }

    /**
     * Retrieves all clients from the database.
     *
     * @return A list of all clients.
     * @throws SQLException If a database access error occurs.
     */
    public List<Client> getAllClients() throws SQLException {
        return clientDAO.getAllClients();
    }

    /**
     * Retrieves a specific client by ID.
     *
     * @param id The ID of the client to retrieve.
     * @return The client with the specified ID.
     * @throws SQLException If a database access error occurs.
     */
    public Client getClientById(int id) throws SQLException {
        return clientDAO.getClientById(id);
    }

    /**
     * Updates an existing client's information.
     *
     * @param client The client with updated data.
     * @throws SQLException If a database access error occurs.
     */
    public void updateClient(Client client) throws SQLException {
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.update(client);
    }

    /**
     * Updates the address of a client.
     *
     * @param address The address to update.
     * @throws SQLException If a database access error occurs.
     */
    public void updateAddress(Address address) throws SQLException {
        AddressDAO addressDAO = new AddressDAO();
        addressDAO.update(address);  // Call the AddressDAO's update method
    }
}

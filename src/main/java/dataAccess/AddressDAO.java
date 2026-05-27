package dataAccess;

import model.Address;
import Connection.ConnectionFactory;

import java.sql.*;
/**
 * DAO class for managing Address records in the database.
 */
public class AddressDAO {
    /**
     * Inserts an Address into the database.
     *
     * @param address The Address object to insert.
     * @return The generated ID of the inserted address.
     * @throws SQLException if a database error occurs.
     */
    public int insertAddress(Address address) throws SQLException {
        String sql = "INSERT INTO address (street, number, city, judet) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, address.getStreet());
            ps.setInt(2, address.getNumber());
            ps.setString(3, address.getCity());
            ps.setString(4, address.getJudet());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Updates an existing Address record in the database.
     *
     * @param address The Address object with updated values.
     * @throws SQLException if a database error occurs.
     */
    public void update(Address address) throws SQLException {
        String sql = "UPDATE address SET street = ?, number = ?, city = ?, judet = ? WHERE addressId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address.getStreet());
            ps.setInt(2, address.getNumber());
            ps.setString(3, address.getCity());
            ps.setString(4, address.getJudet());
            ps.setInt(5, address.getId());
            ps.executeUpdate();
        }
    }
}

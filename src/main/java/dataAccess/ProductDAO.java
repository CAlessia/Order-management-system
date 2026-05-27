package dataAccess;

import model.Product;
import Connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling operations related to Products.
 * Extends AbstractDAO for generic CRUD operations.
 */
public class ProductDAO extends AbstractDAO<Product> {
    /**
     * Inserts a new product into the database.
     *
     * @param product The product to insert.
     * @throws SQLException If a database access error occurs.
     */
    public void insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Product (name, price, stock) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Deletes a product from the database using its ID.
     *
     * @param productId The ID of the product to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM Product WHERE productId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    /**
     * Finds a product in the database by its ID.
     *
     * @param productId The ID of the product to find.
     * @return The found product or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Product findProductById(int productId) throws SQLException {
        String sql = "SELECT * FROM Product WHERE productId = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("productId"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all products.
     * @throws SQLException If a database access error occurs.
     */
    public List<Product> findAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("productId"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        }
        return products;
    }

    /**
     * Updates the details of an existing product in the database.
     *
     * @param product The product to update.
     * @throws SQLException If a database access error occurs.
     */
    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Product SET name = ?, price = ?, stock = ? WHERE productId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStock());
            ps.setInt(4, product.getProductId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating product failed, no rows affected.");
            }
        }
    }

    /**
     * Retrieves a list of products whose stock is below a specified threshold.
     *
     * @param threshold The stock threshold.
     * @return A list of products with stock less than the threshold.
     * @throws SQLException If a database access error occurs.
     */
    public List<Product> findProductsBelowStock(int threshold) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE stock < ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("productId"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    ));
                }
            }
        }
        return products;
    }

    /**
     * Updates the stock level of a specific product.
     *
     * @param productId The ID of the product.
     * @param newStock The new stock quantity to set.
     * @throws SQLException If a database access error occurs.
     */
    public void updateProductStock(int productId, int newStock) throws SQLException {
        String sql = "UPDATE Product SET stock = ? WHERE productId = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }
}
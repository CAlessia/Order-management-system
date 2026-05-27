package dataAccess;

import model.Order;

import Connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling operations related to Orders.
 * Extends AbstractDAO for generic CRUD operations.
 */
public class OrderDAO extends AbstractDAO<Order>{
    /**
     * Inserts a new order into the database if there is enough stock for the product.
     * Also updates the stock for the ordered product.
     *
     * @param order The order to be inserted.
     * @return true if the order was successfully placed, false if not enough stock.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insertOrder(Order order) throws SQLException {
        String getStockSQL = "SELECT stock FROM Product WHERE productId = ?";
        String insertOrderSQL = "INSERT INTO orders (clientId, productId, quantity) VALUES (?, ?, ?)";
        String updateStockSQL = "UPDATE Product SET stock = stock - ? WHERE productId = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stockStmt = conn.prepareStatement(getStockSQL)) {
                stockStmt.setInt(1, order.getProductId());
                ResultSet rs = stockStmt.executeQuery();

                if (rs.next()) {
                    int stock = rs.getInt("stock");
                    if (stock < order.getQuantity()) {
                        conn.rollback();
                        return false;
                    }
                } else {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertOrderSQL);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStockSQL)) {

                insertStmt.setInt(1, order.getClientId());
                insertStmt.setInt(2, order.getProductId());
                insertStmt.setInt(3, order.getQuantity());
                insertStmt.executeUpdate();

                updateStmt.setInt(1, order.getQuantity());
                updateStmt.setInt(2, order.getProductId());
                updateStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return A list of all orders.
     * @throws SQLException If a database access error occurs.
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("orderId"),
                        rs.getInt("clientId"),
                        rs.getInt("productId"),
                        rs.getInt("quantity")
                ));
            }
        }
        return orders;
    }
}

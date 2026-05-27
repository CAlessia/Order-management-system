package businessLayer;

import dataAccess.OrderDAO;
import model.Order;

import java.sql.SQLException;
import java.util.List;


/**
 * Business Logic Layer for handling order-related operations.
 * Contains business validation and communicates with the OrderDAO for database operations.
 */
public class OrderBLL {
    private final OrderDAO orderDAO;

    /**
     * Constructs a new OrderBLL instance and initializes the OrderDAO.
     */
    public OrderBLL() {
        this.orderDAO = new OrderDAO();
    }

    /**
     * Places a new order after validating the order quantity.
     *
     * @param order The order to be placed.
     * @return true if the order was successfully placed, false if there is insufficient stock.
     * @throws BusinessException If the order quantity is invalid or an error occurs during the operation.
     */
    public boolean placeOrder(Order order) throws BusinessException {
        if (order.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be greater than zero.");
        }

        try {
            return orderDAO.insertOrder(order);
        } catch (SQLException e) {
            throw new BusinessException("Error placing order: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return A list of all orders.
     * @throws BusinessException If an error occurs while fetching the orders from the database.
     */
    public List<Order> getAllOrders() throws BusinessException {
        try {
            return orderDAO.getAllOrders();
        } catch (SQLException e) {
            throw new BusinessException("Error retrieving orders: " + e.getMessage(), e);
        }
    }
}

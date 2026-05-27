package presentation;

import javax.swing.*;
import businessLayer.OrderBLL;
import dataAccess.OrderDAO;
import model.Client;
import model.Order;
import model.Product;
import businessLayer.ClientBLL;
import businessLayer.ProductBLL;
import util.TableUtil;
import businessLayer.BusinessException;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * A graphical user interface for managing orders.
 * Allows users to view, add, and delete orders in the system.
 */
public class OrderView extends JFrame {
    private JTable orderTable;
    private JScrollPane tableScrollPane;
    private JButton addButton;
    private JComboBox<Client> clientComboBox;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;

    private OrderBLL orderBLL;

    /**
     * Constructs a new OrderView instance and sets up the UI components.
     */
    public OrderView() {
        setTitle("Order Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        orderTable = new JTable();
        tableScrollPane = new JScrollPane(orderTable);
        add(tableScrollPane, BorderLayout.CENTER);
        addButton = new JButton("Add Order");
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(addButton);
        add(controlsPanel, BorderLayout.SOUTH);
        orderBLL = new OrderBLL();
        addButton.addActionListener(e -> showAddOrderDialog());

        loadOrderData();
    }

    /**
     * Loads all orders from the database and populates the table with order data.
     */
    public void loadOrderData() {
        try {

            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.getAllOrders();

            TableUtil.populateTable(orderTable, orders);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows a dialog to add a new order by selecting client, product, and quantity.
     */
    private void showAddOrderDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("Select Client:"));
        clientComboBox = new JComboBox<>();
        loadClientData();
        panel.add(clientComboBox);

        panel.add(new JLabel("Select Product:"));
        productComboBox = new JComboBox<>();
        loadProductData();
        panel.add(productComboBox);

        panel.add(new JLabel("Enter Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Order",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            createOrder();
        }
    }

    /**
     * Loads the client data into the clientComboBox.
     */
    private void loadClientData() {
        try {

            ClientBLL clientBLL = new ClientBLL();
            List<Client> clients = clientBLL.getAllClients();
            for (Client client : clients) {
                clientComboBox.addItem(client);
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + e.getMessage());
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads the product data into the productComboBox.
     */
    private void loadProductData() {
        try {
            ProductBLL productBLL = new ProductBLL();
            List<Product> products = productBLL.findAllProducts();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    /**
     * Clears the form fields (client, product, quantity) after an order is successfully placed.
     */
    private void clearForm() {
        clientComboBox.setSelectedIndex(0);
        productComboBox.setSelectedIndex(0);
        quantityField.setText("");
    }

    /**
     * Creates an order using the selected client, product, and quantity, then places the order.
     */
    private void createOrder() {
        try {
            Client client = (Client) clientComboBox.getSelectedItem();
            Product product = (Product) productComboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (client == null || product == null) {
                throw new BusinessException("Please select both client and product");
            }

            if (quantity <= 0) {
                throw new BusinessException("Quantity must be positive");
            }

            Order order = new Order();
            order.setClientId(client.getClientId());
            order.setProductId(product.getProductId());
            order.setQuantity(quantity);

            boolean success = orderBLL.placeOrder(order);
            if (success) {
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                loadOrderData();
                loadProductData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to place order");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JTable getOrderTable() {
        return orderTable;
    }

    public JButton getAddButton() {
        return addButton;
    }

}

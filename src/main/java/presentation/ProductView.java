package presentation;

import model.Product;
import businessLayer.ProductBLL;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * The ProductView class provides the user interface for managing products.
 * It allows the user to add, edit, and delete products through a GUI.
 */
public class ProductView extends JFrame {
    private JTable productTable;
    private JScrollPane tableScrollPane;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private ProductBLL productBLL;

    /**
     * Constructor for ProductView.
     * Initializes the GUI components and sets up event listeners.
     */
    public ProductView() {
        setTitle("Product Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        productBLL = new ProductBLL();

        productTable = new JTable();
        tableScrollPane = new JScrollPane(productTable);
        add(tableScrollPane, BorderLayout.CENTER);

        addButton = new JButton("Add Product");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadProductData();
        addButton.addActionListener(this::handleAddProduct);
        editButton.addActionListener(this::handleEditProduct);
        deleteButton.addActionListener(this::handleDeleteProduct);
    }

    /**
     * Loads all products and populates the table with the product data.
     */
    private void loadProductData() {
        try {
            List<Product> products = productBLL.findAllProducts();
            DefaultTableModel model = new DefaultTableModel();

            model.addColumn("ID");
            model.addColumn("Name");
            model.addColumn("Price");
            model.addColumn("Stock");

            for (Product product : products) {
                model.addRow(new Object[]{
                        product.getProductId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock()
                });
            }

            productTable.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action of adding a new product.
     * Opens a form dialog for input and submits the new product.
     */
    private void handleAddProduct(ActionEvent e) {
        ProductFormDialog dialog = new ProductFormDialog(this);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            try {
                Product newProduct = dialog.getProduct();
                productBLL.insertProduct(newProduct);
                loadProductData();
                JOptionPane.showMessageDialog(this, "Product added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the action of editing an existing product.
     * Opens a form dialog pre-filled with the product details for editing.
     */
    private void handleEditProduct(ActionEvent e) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productId = (int) productTable.getValueAt(selectedRow, 0);
            Product productToEdit = productBLL.findAllProducts().stream()
                    .filter(p -> p.getProductId() == productId)
                    .findFirst()
                    .orElse(null);

            if (productToEdit != null) {
                ProductFormDialog dialog = new ProductFormDialog(this, productToEdit);
                dialog.setVisible(true);

                if (dialog.isSubmitted()) {
                    Product updatedProduct = dialog.getProduct();
                    productBLL.updateProduct(updatedProduct);
                    loadProductData();
                    JOptionPane.showMessageDialog(this, "Product updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error editing product: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action of deleting a selected product.
     */
    private void handleDeleteProduct(ActionEvent e) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int productId = (int) productTable.getValueAt(selectedRow, 0);
                productBLL.deleteProduct(productId);
                loadProductData();
                JOptionPane.showMessageDialog(this, "Product deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
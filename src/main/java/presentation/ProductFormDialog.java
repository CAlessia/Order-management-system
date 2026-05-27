package presentation;

import model.Product;
import javax.swing.*;
import java.awt.*;

/**
 * Represents a dialog form for creating or editing products.
 * This dialog allows users to input or modify product details such as name, price, and stock.
 * It supports both creation of new products and editing of existing ones.
 */
public class ProductFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JButton submitButton;
    private boolean submitted = false;
    private Product existingProduct;

    public ProductFormDialog(JFrame parent) {
        super(parent, "Add Product", true);
        initializeDialog(parent);
    }

    public ProductFormDialog(JFrame parent, Product product) {
        super(parent, "Edit Product", true);
        this.existingProduct = product;
        initializeDialog(parent);
        populateFields(product);
    }

    private void initializeDialog(JFrame parent) {
        setLayout(new GridLayout(0, 2, 5, 5));
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            submitted = true;
            setVisible(false);
        });
        add(submitButton);

        getRootPane().setDefaultButton(submitButton);
    }

    private void populateFields(Product product) {
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Product getProduct() {
        if (existingProduct != null) {
            existingProduct.setName(nameField.getText());
            existingProduct.setPrice(Double.parseDouble(priceField.getText()));
            existingProduct.setStock(Integer.parseInt(stockField.getText()));
            return existingProduct;
        }


        return new Product(
                0,
                nameField.getText(),
                Double.parseDouble(priceField.getText()),
                Integer.parseInt(stockField.getText())
        );
    }
}
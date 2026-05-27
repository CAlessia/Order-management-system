import presentation.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The Main class serves as the entry point and main user interface for the "Management System".
 * It provides options for navigating to client, product, and order management sections
 * of the application. This class extends JFrame and sets up the main window for the system.
 */
public class Main extends JFrame {
    public Main() {
        setTitle("Management System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton clientButton = new JButton("Client Management");
        JButton productButton = new JButton("Product Management");
        JButton orderButton = new JButton("Order Management");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        clientButton.setFont(buttonFont);
        productButton.setFont(buttonFont);
        orderButton.setFont(buttonFont);

        clientButton.addActionListener(this::openClientView);
        productButton.addActionListener(this::openProductView);
        orderButton.addActionListener(this::openOrderView);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(clientButton);
        buttonPanel.add(productButton);
        buttonPanel.add(orderButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void openClientView(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            ClientView clientView = new ClientView();
            new ClientController(clientView);
            clientView.setVisible(true);
        });
    }

    private void openProductView(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            ProductView productView = new ProductView();
            productView.setVisible(true);
        });
    }

    private void openOrderView(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            OrderView orderView = new OrderView();
            orderView.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                Main main = new Main();
                main.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
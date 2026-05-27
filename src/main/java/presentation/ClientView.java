package presentation;

import javax.swing.*;
import model.Address;
import model.Client;
import dataAccess.ClientDAO;
import dataAccess.AddressDAO;
import util.TableUtil;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * The ClientView class represents the user interface for managing clients.
 * It displays a table of clients and provides controls to add, edit, or delete clients.
 */
public class ClientView extends JFrame {
    private JTable clientTable;
    private JScrollPane tableScrollPane;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;

    public ClientView() {
        setTitle("Client Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        clientTable = new JTable();
        tableScrollPane = new JScrollPane(clientTable);
        add(tableScrollPane, BorderLayout.CENTER);

        addButton = new JButton("Add Client");
        deleteButton = new JButton("Delete Selected Client");
        editButton = new JButton("Edit Selected Client");

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(addButton);
        controlsPanel.add(deleteButton);
        controlsPanel.add(editButton);
        add(controlsPanel, BorderLayout.SOUTH);

        loadClientData();
    }

    /**
     * Loads all client data from the database and displays it in the table.
     */
    public void loadClientData() {
        try {
            ClientDAO clientDAO = new ClientDAO();
            List<Client> clients = clientDAO.getAllClients();
            TableUtil.populateTable(clientTable, clients);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a dialog to edit the selected client's details.
     *
     * @param client The client to be edited.
     */
    public void showEditDialog(Client client) {
        ClientFormDialog dialog = new ClientFormDialog(this, client);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            try {
                Client updatedClient = dialog.getClient();
                Address updatedAddress = dialog.getAddress();

                AddressDAO addressDAO = new AddressDAO();
                if (client.getAddress() != null) {
                    addressDAO.update(updatedAddress);
                } else {
                    int addressId = addressDAO.insertAddress(updatedAddress);
                    updatedClient.setAddress(new Address(addressId,
                            updatedAddress.getStreet(),
                            updatedAddress.getNumber(),
                            updatedAddress.getCity(),
                            updatedAddress.getJudet()));
                }

                ClientDAO clientDAO = new ClientDAO();
                clientDAO.update(updatedClient);

                loadClientData();

                JOptionPane.showMessageDialog(this, "Client updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating client: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JTable getClientTable() {
        return clientTable;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getEditButton() {
        return editButton;
    }
}

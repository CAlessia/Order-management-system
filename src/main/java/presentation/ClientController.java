package presentation;

import model.*;
import dataAccess.ClientDAO;
import dataAccess.AddressDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * The ClientController class handles the logic for responding to user interactions
 * in the ClientView UI. It acts as a bridge between the view and data layers.
 */
public class ClientController {
    private ClientView view;
    private ClientDAO clientDAO;
    private AddressDAO addressDAO;

    public ClientController(ClientView view) {
        this.view = view;
        this.clientDAO = new ClientDAO();
        this.addressDAO = new AddressDAO();


        view.getAddButton().addActionListener(this::handleAddClient);
        view.getEditButton().addActionListener(this::handleEditClient);
        view.getDeleteButton().addActionListener(this::handleDeleteClient);
    }

    /**
     * Handles the action of adding a new client.
     *
     * @param e The triggered action event.
     */
    private void handleAddClient(ActionEvent e) {
        ClientFormDialog dialog = new ClientFormDialog(view);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            try {
                Client newClient = dialog.getClient();
                Address newAddress = dialog.getAddress();

                int addressId = addressDAO.insertAddress(newAddress);
                newClient.setAddressId(addressId);
                clientDAO.insertClient(newClient);
                view.loadClientData();

                JOptionPane.showMessageDialog(view, "Client added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view, "Error adding client: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the action of editing a selected client.
     *
     * @param e The triggered action event.
     */
    private void handleEditClient(ActionEvent e) {
        int selectedRow = view.getClientTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a client to edit",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int clientId = (int) view.getClientTable().getValueAt(selectedRow, 0);
            Client clientToEdit = clientDAO.getClientById(clientId);

            if (clientToEdit != null) {
                ClientFormDialog dialog = new ClientFormDialog(view, clientToEdit);
                dialog.setVisible(true);

                if (dialog.isSubmitted()) {
                    Client updatedClient = dialog.getClient();

                    if (clientDAO.updateClientWithAddress(updatedClient)) {
                        view.loadClientData();
                        JOptionPane.showMessageDialog(view,
                                "Client updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(view,
                                "Failed to update client",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                    "Error updating client: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Invalid number format in address field",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action of deleting a selected client.
     *
     * @param e The triggered action event.
     */
    private void handleDeleteClient(ActionEvent e) {
        int selectedRow = view.getClientTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a client to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this client?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int clientId = (int) view.getClientTable().getValueAt(selectedRow, 0);
                clientDAO.deleteClient(clientId);
                view.loadClientData();

                JOptionPane.showMessageDialog(view, "Client deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view, "Error deleting client: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
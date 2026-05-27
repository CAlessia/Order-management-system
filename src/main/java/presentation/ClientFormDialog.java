package presentation;

import model.Client;
import model.Address;

import javax.swing.*;
import java.awt.*;

/**
 * ClientFormDialog is a specialized JDialog used to handle the creation or editing
 * of client forms. It provides fields for inputting and modifying client data such
 * as name, email, telephone, and address details.
 *
 * The dialog can operate in two modes:
 * - Creating a new client.
 * - Editing an existing client.
 *
 * During initialization, the class sets up the fields, layout, and other visual components.
 * It also provides methods to retrieve and populate client data.
 */
public class ClientFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField streetField;
    private JTextField numberField;
    private JTextField cityField;
    private JTextField judetField;
    private JButton submitButton;
    private boolean submitted = false;
    private Client existingClient;

    public ClientFormDialog(JFrame parent) {
        super(parent, "Add Client", true);
        initializeDialog(parent);
    }

    public ClientFormDialog(JFrame parent, Client client) {
        super(parent, "Edit Client", true);
        this.existingClient = client;
        initializeDialog(parent);
        populateFields(client);
    }

    private void initializeDialog(JFrame parent) {
        setLayout(new GridLayout(0, 2));
        setSize(400, 400);
        setLocationRelativeTo(parent);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Telephone:"));
        telephoneField = new JTextField();
        add(telephoneField);

        add(new JLabel("Street:"));
        streetField = new JTextField();
        add(streetField);

        add(new JLabel("Number:"));
        numberField = new JTextField();
        add(numberField);

        add(new JLabel("City:"));
        cityField = new JTextField();
        add(cityField);

        add(new JLabel("Judet:"));
        judetField = new JTextField();
        add(judetField);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitted = true);
        add(submitButton);
    }

    private void populateFields(Client client) {
        nameField.setText(client.getName());
        emailField.setText(client.getEmail());
        telephoneField.setText(client.getTelephone());

        Address address = client.getAddress();
        if (address != null) {
            streetField.setText(address.getStreet());
            numberField.setText(String.valueOf(address.getNumber()));
            cityField.setText(address.getCity());
            judetField.setText(address.getJudet());
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Client getClient() {
        if (existingClient != null) {

            existingClient.setName(nameField.getText());
            existingClient.setEmail(emailField.getText());
            existingClient.setTelephone(telephoneField.getText());

            Address address = existingClient.getAddress();
            if (address == null) {
                address = new Address(0, streetField.getText(),
                        Integer.parseInt(numberField.getText()),
                        cityField.getText(), judetField.getText());
            } else {
                address.setStreet(streetField.getText());
                address.setNumber(Integer.parseInt(numberField.getText()));
                address.setCity(cityField.getText());
                address.setJudet(judetField.getText());
            }
            existingClient.setAddress(address);

            return existingClient;
        }

        Address address = new Address(0, streetField.getText(),
                Integer.parseInt(numberField.getText()),
                cityField.getText(), judetField.getText());

        return new Client(0, nameField.getText(), emailField.getText(),
                address, telephoneField.getText());
    }

    public Address getAddress() {
        return new Address(
                existingClient != null && existingClient.getAddress() != null
                        ? existingClient.getAddress().getId() : 0,
                streetField.getText(),
                Integer.parseInt(numberField.getText()),
                cityField.getText(),
                judetField.getText()
        );
    }
}
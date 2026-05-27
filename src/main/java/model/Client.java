package model;

/**
 * Represents a client with its associated details such as client ID, name, email,
 * address ID, telephone number, and an address object.
 */
public class Client {
    private int clientId;
    private String name;
    private String email;
    private int addressId;
    private String telephone;
    private Address address;

    /** Default constructor. */
    public Client() {
    }

    public Client(int clientId, String name, String email, int addressId, String telephone) {
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.addressId = addressId;
        this.telephone = telephone;
    }

    /**
     * Constructs a client without a client ID (e.g. before inserting into DB).
     */

    public Client(String name, String email, int addressId, String telephone) {
        this.name = name;
        this.email = email;
        this.addressId = addressId;
        this.telephone = telephone;
    }

    /**
     * Constructs a client with a full Address object.
     */
    public Client(int clientId, String name, String email, Address address, String telephone) {
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.telephone = telephone;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString(){
        return this.clientId+" "+this.name;
    }
}

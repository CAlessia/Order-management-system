package model;

/**
 * Represents an Address entity containing details such as ID, street name,
 * street number, city, and judet .
 */

public class Address {
    private int id;
    private String street;
    private int number;
    private String city;
    private String judet;

    /**
     * Constructs an Address with all its details.
     *
     * @param id     The unique identifier of the address.
     * @param street The street name.
     * @param number The street number.
     * @param city   The city of the address.
     * @param judet  The county (judet) of the address.
     */

    public Address(int id, String street, int number, String city, String judet) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.city = city;
        this.judet = judet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    /**
     * Returns a human-readable version of the address.
     *
     * @return Address formatted as "Street Number, City, County"
     */

    @Override
    public String toString() {
        return street + " " + number + ", " + city + ", " + judet;
    }
}

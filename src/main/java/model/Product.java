package model;

/**
 * Represents a product that can be purchased.
 */
public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;

    public Product() {
    }

    public Product(int productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Constructs a product without an ID, used before database insertion.
     */
    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "productId=" + productId + ", name=" + name + ", price=" + price + ", stock=" + stock + "]";
    }
}

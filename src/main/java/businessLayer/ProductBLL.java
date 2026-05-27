package businessLayer;

import dataAccess.ProductDAO;
import model.Product;
import java.sql.SQLException;
import java.util.List;

/**
 * The ProductBLL class handles the business logic for managing products.
 * It interacts with the ProductDAO to retrieve and modify product data.
 */
public class ProductBLL {
    private final ProductDAO productDAO;

    public ProductBLL() {
        this.productDAO = new ProductDAO();
    }

    /**
     * Retrieves all products from the database.
     *
     * @return List of all products.
     * @throws BusinessException If there is an error while retrieving products from the database.
     */
    public List<Product> findAllProducts() throws BusinessException {
        try {
            return productDAO.findAllProducts();
        } catch (SQLException e) {
            throw new BusinessException("Error retrieving products: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product.
     * @return The product corresponding to the given ID.
     * @throws BusinessException If the ID is invalid or there is an error retrieving the product.
     */
    public Product findProductById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("Invalid product ID");
        }

        try {
            Product product = productDAO.findProductById(id);
            if (product == null) {
                throw new BusinessException("Product not found with ID: " + id);
            }
            return product;
        } catch (SQLException e) {
            throw new BusinessException("Error finding product: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts a new product into the database.
     *
     * @param product The product to be inserted.
     * @throws BusinessException If there is an error during validation or insertion.
     */
    public void insertProduct(Product product) throws BusinessException {
        validateProduct(product);
        productDAO.insert(product);
    }

    /**
     * Updates an existing product in the database.
     *
     * @param product The product with updated details.
     * @throws BusinessException If the product ID is invalid or there is an error during the update.
     */
    public void updateProduct(Product product) throws BusinessException {
        if (product.getProductId() <= 0) {
            throw new BusinessException("Invalid product ID");
        }
        validateProduct(product);

        try {
            productDAO.updateProduct(product);
        } catch (SQLException e) {
            throw new BusinessException("Error updating product: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a product from the database.
     *
     * @param id The ID of the product to be deleted.
     * @throws BusinessException If the ID is invalid or there is an error during deletion.
     */
    public void deleteProduct(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("Invalid product ID");
        }

        try {
            productDAO.deleteProduct(id);
        } catch (SQLException e) {
            throw new BusinessException("Error deleting product: " + e.getMessage(), e);
        }
    }

    /**
     * Validates a product's details before inserting or updating.
     *
     * @param product The product to be validated.
     * @throws BusinessException If the product's details are invalid.
     */
    private void validateProduct(Product product) throws BusinessException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessException("Product name cannot be empty");
        }
        if (product.getPrice() <= 0) {
            throw new BusinessException("Product price must be positive");
        }
        if (product.getStock() < 0) {
            throw new BusinessException("Product stock cannot be negative");
        }
    }

    /**
     * Finds products with stock below a specified threshold.
     *
     * @param threshold The stock threshold.
     * @return A list of products with stock below the threshold.
     * @throws BusinessException If the threshold is negative or there is an error retrieving the products.
     */
    public List<Product> findProductsBelowStock(int threshold) throws BusinessException {
        if (threshold < 0) {
            throw new BusinessException("Stock threshold cannot be negative");
        }

        try {
            return productDAO.findProductsBelowStock(threshold);
        } catch (SQLException e) {
            throw new BusinessException("Error finding low stock products: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the stock for a specific product.
     *
     * @param productId The ID of the product.
     * @param newStock The new stock value.
     * @throws BusinessException If the product ID is invalid or the stock is negative.
     */
    public void updateProductStock(int productId, int newStock) throws BusinessException {
        if (productId <= 0) {
            throw new BusinessException("Invalid product ID");
        }
        if (newStock < 0) {
            throw new BusinessException("Stock cannot be negative");
        }

        try {
            productDAO.updateProductStock(productId, newStock);
        } catch (SQLException e) {
            throw new BusinessException("Error updating product stock: " + e.getMessage(), e);
        }
    }
}
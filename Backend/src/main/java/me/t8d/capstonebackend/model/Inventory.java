/**
 * Class to act as the primary storage and interface for the storage of Parts and Products.
 */
package me.t8d.capstonebackend.model;




import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NoSuchElementException;
/**
 * @author Thomas Miller
 */
@JsonSerialize
@JsonDeserialize
public class Inventory {
    private ArrayList<Part> allParts = new ArrayList<>();
    private ArrayList<Product> allProducts = new ArrayList<>();

    // All Part Methods

    /**
     * @param newPart the part to add into the inventory
     */
    public void addPart(Part newPart) {this.allParts.add(newPart);}

    /**
     * @param partId the ID of the desired part to find within the inventory
     * @return The desired part
     * @throws NoSuchElementException if no part by the provided ID is found within the inventory
     */
    public Part lookupPart(int partId) {
        for (Part part: this.allParts) {
            if (part.getId() == partId) return part;
        }
        throw new NoSuchElementException();
    }

    /**
     * @param partName the name of the desired part to find within the inventory
     * @return the desired part
     * @throws NoSuchElementException if no part by the provided name is found within the inventory
     */
    public ArrayList<Part> lookupPart(String partName) {
        ArrayList<Part> foundParts = new ArrayList<>();
        for (Part part: this.allParts) {
            if (part.getName().equals(partName)) foundParts.add(part);
        }
        if (foundParts.isEmpty()) throw new NoSuchElementException();
        else return foundParts;
    }

    /**
     * @param index the index within the inventory that the desired part to update is located at
     * @param selectedPart the updated part to replace the existing entry with
     */
    public void updatePart(int index, Part selectedPart) {
        for (Product product: allProducts) {
            Part foundPart = null;
            for (Part part: product.getAllAssociatedParts()) {
                if (part.getId() == selectedPart.getId()) {
                    foundPart = part;
                    break;
                }
            }
            if (foundPart != null) {
                product.deleteAssociatedPart(foundPart);
                product.addAssociatedPart(selectedPart);
            }
        }
        this.allParts.set(index, selectedPart);
    }

    /**
     * @param selectedPart the desired part to remove from the inventory
     * @return True if the part was removed, False if the part was not found within the inventory
     */
    public boolean deletePart(Part selectedPart) {
        for (Product product: allProducts) {
            Part tempPart = null;
            for (Part part: product.getAllAssociatedParts()) {
                if (part == selectedPart) {
                    tempPart = part;
                }
            }
            if (tempPart != null)
                product.deleteAssociatedPart(tempPart);
        }
        return this.allParts.remove(selectedPart);
    }

    /**
     * @return the list of all parts within the Inventory
     */
    public ArrayList<Part> getAllParts() {return this.allParts;}
    // All Product Methods

    /**
     * @param newProduct the product to add into the inventory
     */
    public void addProduct(Product newProduct) {this.allProducts.add(newProduct);}

    /**
     * @param productId the ID of the desired product to find within the inventory
     * @return the desired product
     * @throws NoSuchElementException if no product by the provided ID is found within the inventory
     */
    public Product lookupProduct(int productId) {
        for (Product product: this.allProducts) {
            if (product.getId() == productId) return product;
        }
        throw new NoSuchElementException();
    }

    /**
     * @param productName the name of the desired product to find within the inventory
     * @return the desired part
     * @throws NoSuchElementException if no product by the provided name is found within the inventory
     */
    public ArrayList<Product> lookupProduct(String productName) {
        ArrayList<Product> foundProducts = new ArrayList<>();
        for (Product product: this.allProducts) {
            if (product.getName().equals(productName)) foundProducts.add(product);
        }
        if (foundProducts.isEmpty()) throw new NoSuchElementException();
        else return foundProducts;
    }

    /**
     * @param index the index within the inventory that the desired product to update is located at
     * @param selectedProduct the updated product to replace the existing entry with
     */
    public void updateProduct(int index, Product selectedProduct) {
        this.allProducts.set(index, selectedProduct);
    }

    /**
     * @param selectedProduct the desired product to remove from the inventory
     * @return True if the product was removed, False if the product was not found within the inventory
     */
    public boolean deleteProduct(Product selectedProduct) {
        return this.allProducts.remove(selectedProduct);
    }

    /**
     * @return the list of all products within the Inventory
     */
    public ArrayList<Product> getAllProducts() {return this.allProducts;}
}

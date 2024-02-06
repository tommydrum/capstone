/**
 * Class to define a Product, and contain a list of its attributed parts
 */
package me.t8d.capstone.frontend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.t8d.capstone.frontend.utils.ObservableListDeserializer;

/**
 * @author Thomas Miller
 */
@JsonSerialize
@JsonDeserialize
public class Product {
    @JsonDeserialize(using = ObservableListDeserializer.class)
    private ObservableList<Part> allAssociatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
        this.allAssociatedParts = FXCollections.observableArrayList();
    }
    public Product() {
        this.allAssociatedParts = FXCollections.observableArrayList();
    }
    /**
     * @param id the ID of the product
     */
    public void setId(int id) {this.id = id;}

    /**
     * @return the ID of the product
     */
    public int getId() {return this.id;}

    /**
     * @param name the name of the product
     */
    public void setName(String name) {this.name = name;}

    /**
     * @return the name of the product
     */
    public String getName() {return this.name;}

    /**
     * @param price the price of the product
     */
    public void setPrice(double price) {this.price = price;}

    /**
     * @return the price of the product
     */
    public double getPrice() {return this.price;}

    /**
     * @param stock the stock of the product
     */
    public void setStock(int stock) {this.stock = stock;}

    /**
     * @return the stock of the product
     */
    public int getStock() {return this.stock;}

    /**
     * @param min the minimum allowed stock
     */
    public void setMin(int min) {this.min = min;}

    /**
     * @return the minimum allowed stock
     */
    public int getMin() {return this.min;}

    /**
     * @param max the maximum allowed stock
     */
    public void setMax(int max) {this.max = max;}

    /**
     * @return the maximum allowed stock
     */
    public int getMax() {return this.max;}

    /**
     * @param part the additional part to associate the Product with
     */
    public void addAssociatedPart(Part part) {this.allAssociatedParts.add(part);}

    /**
     * @param selectedAssociatedPart the part that is desired to be removed from the associated part list
     * @return True if it was removed, False if it was not found.
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        return this.allAssociatedParts.remove(selectedAssociatedPart);
    }

    /**
     * @return the list of associated parts
     */
    public ObservableList<Part> getAllAssociatedParts() {return this.allAssociatedParts;}
    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
    public static Product fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Product.class);
    }
}

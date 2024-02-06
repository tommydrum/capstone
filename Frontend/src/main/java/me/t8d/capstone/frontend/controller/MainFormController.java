package me.t8d.capstone.frontend.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.t8d.capstone.frontend.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for the main window of this application. This allows the user to view and manage an inventory of parts and products.
 * @author Thomas Miller
 */

public class MainFormController {
    @FXML
    private TextField PartSearchInput;
    @FXML
    private TextField ProductSearchInput;
    @FXML
    private VBox rootVBox;
    private Inventory inv;
    // Tables and Columns
    @FXML
    private TableView<Part> PartsTable;
    @FXML
    private TableColumn<Part, Integer> PartIDCol;
    @FXML
    private TableColumn<Part, String> PartNameCol;
    @FXML
    private TableColumn<Part, Integer> PartInvLevelCol;
    @FXML
    private TableColumn<Part, Float> PartCPUCol;

    @FXML
    private TableView<Product> ProductsTable;
    @FXML
    private TableColumn<Product, Integer> ProductIDCol;
    @FXML
    private TableColumn<Product, String> ProductNameCol;
    @FXML
    private TableColumn<Product, Integer> ProductInvLevelCol;
    @FXML
    private TableColumn<Product, Float> ProductCPUCol;
    // Buttons
    @FXML
    private Button exitButton;
    // Button actions
    /**
     * Closes out of this application.
     */
    @FXML
    private void handleExitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    /**
     * Handle the Add Part button, creating the Add Part window and initializing its controller.
     */
    @FXML
    private void addPartButtonClick() {
        setWindowDisabled();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/t8d/capstone/frontend/view/AddPartForm.fxml"));
            Parent root = loader.load();
            AddPartFormController dialogController = loader.getController();
            dialogController.initialize(this, getNextPartID());
            // As the main window is disabled while the addPart dialog is open, a user cannot create this window multiple
            // times, preventing the possibility of getNextPartID() from being assigned to two different 'new parts'.
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setWindowEnabled();
        }

    }
    /**
     * Interface function for subclasses to call, to add parts into the inventory.
     * @param part for the newly created part
     */
    public void addPart(Part part) {
        // Interface function to add part to inventory from child controllers.
        inv.addPart(part);
        // reload the table to show the new part
        PartsTable.setItems(inv.getAllParts());
    }
    /**
     * Interface function for subclasses to call, replace/update a part within the inventory.
     * @param part for the newly created/modified part
     * @param index for the old part's index within the inventory
     */
    public void replacePart(Part part, int index) {
        inv.updatePart(index, part);
        // reload the table to show the new part
        PartsTable.setItems(inv.getAllParts());
    }

    /**
     * Interface function for subclasses to call, replace/update a product within the inventory.
     * @param product for the newly created/modified product
     * @param index for the old product's index within the inventory
     */
    public void replaceProduct(Product product, int index) {
        inv.updateProduct(index, product);
        // reload the table to show the new product
        ProductsTable.setItems(inv.getAllProducts());
    }

    /**
     * When a new part is created, this finds the next available ID to use for this part.
     * @return the next available ID to be used for a new part.
     */
    public int getNextPartID() {
        // Algorithm to find the first available ID
        List<Part> tempParts = new ArrayList<>(inv.getAllParts());
        if (tempParts.isEmpty())
            return 1;
        tempParts.sort(Comparator.comparingInt(Part::getId));
        int expectedId = 1;

        for (Part part : tempParts) {
            if (part.getId() != expectedId) {
                return expectedId;
            }
            expectedId++;
        }
        return expectedId;
    }
    /**
     * When a new product is created, this finds the next available ID to use for this product.
     * @return the next available ID to be used for a new product.
     */
    public int getNextProductID() {
        // Algorithm to find the first available ID
        List<Product> tempProducts = new ArrayList<>(inv.getAllProducts());
        if (tempProducts.isEmpty())
            return 1;
        tempProducts.sort(Comparator.comparingInt(Product::getId));
        int expectedId = 1;

        for (Product product : tempProducts) {
            if (product.getId() != expectedId) {
                return expectedId;
            }
            expectedId++;
        }
        return expectedId;
    }
    /**
     * Interface function for subclasses to call, to add products into the inventory.
     * @param product for the newly created product
     */
    public void addProduct(Product product) {
        inv.addProduct(product);
        // reload the table to show the new product
        ProductsTable.setItems(inv.getAllProducts());
    }
    /**
     * Interface function for subclasses to call, to enable user interaction with the main window.
     */
    public void setWindowEnabled() {
        rootVBox.setDisable(false);
    }
    /**
     * Interface function for subclasses to call, to disable user interaction with the main window.
     */
    public void setWindowDisabled() {
        rootVBox.setDisable(true);
    }
    /**
     * Creates a new window for the user to modify an existing part.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void modifyPart(ActionEvent actionEvent) {
        setWindowDisabled();
        Part selectedPart = PartsTable.getFocusModel().getFocusedItem();
        if (selectedPart == null) {
            setWindowEnabled();
            return;
        }
        int index = PartsTable.getFocusModel().getFocusedIndex();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/t8d/capstone/frontend/view/ModifyPartForm.fxml"));
            Parent root = loader.load();
            ModifyPartFormController dialogController = loader.getController();
            dialogController.initialize(this, selectedPart, index);
            // As the main window is disabled while the modifyPart dialog is open, a user cannot create this window multiple
            // times, preventing the possibility of getNextPartID() from being assigned to two different parts.
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setWindowEnabled();
        }
    }
    /**
     * Delete the currently selected part (prompts for confirmation first).
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void deletePart(ActionEvent actionEvent) {
        setWindowDisabled();
        Part selectedPart = PartsTable.getFocusModel().getFocusedItem();
        if (selectedPart == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Action Confirmation");
        alert.setContentText("Are you sure you want to delete this part?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){

            this.inv.deletePart(selectedPart);
            // refresh the table to show the part was removed
            PartsTable.setItems(inv.getAllParts());
        }
        setWindowEnabled();
    }
    /**
     * Creates a window for the user to create/add a new product.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void addProduct(ActionEvent actionEvent) {
        setWindowDisabled();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/t8d/capstone/frontend/view/AddProductForm.fxml"));
            Parent root = loader.load();
            AddProductFormController dialogController = loader.getController();
            dialogController.initialize(this, this.inv, getNextProductID());
            // As the main window is disabled while the addProduct dialog is open, a user cannot create this window multiple
            // times, preventing the possibility of getNextProductID() from being assigned to two different 'new product'.
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setWindowEnabled();
        }
    }
    /**
     * Creates a window for the user to modify an existing product.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void modifyProduct(ActionEvent actionEvent) {
        setWindowDisabled();
        Product selectedProduct = ProductsTable.getFocusModel().getFocusedItem();
        if (selectedProduct == null) {
            setWindowEnabled();
            return;
        }
        int index = ProductsTable.getFocusModel().getFocusedIndex();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/t8d/capstone/frontend/view/ModifyProductForm.fxml"));
            Parent root = loader.load();
            ModifyProductFormController dialogController = loader.getController();
            dialogController.initialize(this, inv, selectedProduct, index);
            // As the main window is disabled while the modifyProduct dialog is open, a user cannot create this window multiple
            // times, preventing the possibility of getNextProductID() from being assigned to two different products.
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setWindowEnabled();
        }
    }
    /**
     * Delete the currently selected product (prompts for confirmation first).
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void deleteProduct(ActionEvent actionEvent) {
        setWindowDisabled();
        Product selectedProduct = ProductsTable.getFocusModel().getFocusedItem();
        if (selectedProduct == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Action Confirmation");
        alert.setContentText("Are you sure you want to delete this product?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){

            if (!selectedProduct.getAllAssociatedParts().isEmpty()) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setTitle("Removal Error");
                alertError.setHeaderText(null);
                alertError.setContentText("The selected product has part(s) associated with it. Remove them to delete this product");
                alertError.showAndWait();
                return;
            }
            this.inv.deleteProduct(selectedProduct);
            // refresh the table to show the product was removed
            ProductsTable.setItems(inv.getAllProducts());
        }
        setWindowEnabled();
    }

    /**
     * Initializes this controller/window by creating an Inventory, connecting the inventory's parts/products to
     * the tables within the window, initializing the search bars, and adding some test data to pre-fill the inventory.
     */
    public void initialize() {
        setWindowDisabled();
        this.inv = new Inventory();
        PartsTable.setItems(inv.getAllParts());
        PartIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        PartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        PartInvLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        PartCPUCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        ProductsTable.setItems(inv.getAllProducts());
        ProductIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        ProductNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ProductInvLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        ProductCPUCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Setup the listener for both search bars
        PartSearchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            partSearch(newValue);
        });
        ProductSearchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            productSearch(newValue);
        });
        setWindowEnabled();
        // Test data to initialize -- remove/comment when Add/Remove/Modify are completed
//        inv.addPart(new InHouse(1, "testName", 1, 2, 1, 5, 1));
//        inv.addProduct(new Product(1, "testProduct", 5, 1, 1, 5));
//        Product testProduct = new Product(2, "testProduct2", 5, 1, 1, 5);
//        testProduct.addAssociatedPart(inv.lookupPart(2));
//        inv.addProduct(testProduct);
//
//        inv.lookupProduct(1).addAssociatedPart(inv.lookupPart(1));

    }

    /**
     * @param input as the user input for part's search bar. This filters down the part's table according to matching name or part ID.
     *              - This resets the table to the original state upon clearing the input.
     */
    private void partSearch(String input) {
        ObservableList<Part> filteredList = FXCollections.observableArrayList();
        // If the input is empty/clear, reset the list to all parts
        if (input == null || input.isEmpty())
        {
            filteredList = inv.getAllParts();
        }
        // If the input is an int, assume it's an ID and cast it to an int and to the search. No result means an empty list.
        else if (input.matches("^-?\\d+$")) {
            int id = Integer.parseInt(input);
            try {
                Part foundPart = inv.lookupPart(id);
                filteredList.add(foundPart);
            } catch (NoSuchElementException ignored) {};
        // If the input is a string, assume it's a name and perform a lookup as such.
        } else {
            try {
                filteredList = inv.lookupPart(input);
            } catch (NoSuchElementException ignored) {};
        }
        PartsTable.setItems(filteredList);
    }
    /**
     * @param input as the user input for product's search bar. This filters down the product's table according to matching name or product ID.
     *              - This resets the table to the original state upon clearing the input.
     */
    private void productSearch(String input) {
        ObservableList<Product> filteredList = FXCollections.observableArrayList();
        // If the input is empty/clear, reset the list to all parts
        if (input == null || input.isEmpty())
        {
            filteredList = inv.getAllProducts();
        }
        // If the input is an int, assume it's an ID and cast it to an int and to the search. No result means an empty list.
        else if (input.matches("^-?\\d+$")) {
            int id = Integer.parseInt(input);
            try {
                Product foundProduct = inv.lookupProduct(id);
                filteredList.add(foundProduct);
            } catch (NoSuchElementException ignored) {};
            // If the input is a string, assume it's a name and perform a lookup as such.
        } else {
            try {
                filteredList = inv.lookupProduct(input);
            } catch (NoSuchElementException ignored) {};
        }
        ProductsTable.setItems(filteredList);
    }
    /**
     * Creates a CSV file of the current inventory's parts and products, and saves it to the user's computer.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     * @throws IOException if the file cannot be written to the user's computer.
     */
    @FXML
    private void handleInventoryReportButtonClick(ActionEvent actionEvent) throws IOException {
        setWindowDisabled();
        List<Product> products = inv.getAllProducts();
        List<Part> parts = inv.getAllParts();

        ArrayList<String> lines = new ArrayList<>();
        String filename;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        filename = "inventory_level_" + formattedDateTime + ".csv";

        //csv header
        lines.add("ID,Name,Inventory Level,Price,Type,Company Name,Machine ID");
        for (Part part : parts) {
            String line = part.getId() + "," + part.getName() + "," + part.getStock()
                    + "," + part.getPrice() + "," + part.getType()
                    + "," + (part instanceof Outsourced ? ((Outsourced) part).getCompanyName() : "")
                    + "," + (part instanceof InHouse ? ((InHouse) part).getMachineId() : "");
            lines.add(line);
        }
        for (Product product : products) {
            String line = product.getId() + "," + product.getName() + "," + product.getStock()
                    + "," + product.getPrice() + ",Product,,";
            lines.add(line);
        }
        // write csv file
        File file = new File(filename);
        FileWriter fileWriter = new FileWriter(file);
        for (String line : lines) {
            fileWriter.write(line + "\n");
        }
        fileWriter.close();
        // popup alert to notify user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventory Report");
        alert.setHeaderText("Inventory Report");
        alert.setContentText("Inventory report saved to " + file.getAbsolutePath());
        alert.showAndWait();

        setWindowEnabled();
    }
    /**
     * Creates a CSV file of the current inventory's parts, and saves it to the user's computer.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     * @throws IOException if the file cannot be written to the user's computer.
     */
    @FXML
    private void reportOnProductPrice(ActionEvent actionEvent) throws IOException {
        setWindowDisabled();
        List<Product> products = inv.getAllProducts();

        ArrayList<String> lines = new ArrayList<>();
        String filename;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        filename = "product_prices_" + formattedDateTime + ".csv";

        //csv header
        lines.add("ID,Name,Type,Price");
        for (Product product : products) {
            String line = product.getId() + "," + product.getName() + ",Product," + product.getPrice();
            lines.add(line);
            for (Part part : product.getAllAssociatedParts()) {
                // ID for parts for this report will be productId-partId
                line = product.getId() + "-" + part.getId() + "," + part.getName() + "," + part.getType() + "," + part.getPrice();
                lines.add(line);
            }
        }

        // write csv file
        File file = new File(filename);
        FileWriter fileWriter = new FileWriter(file);
        for (String line : lines) {
            fileWriter.write(line + "\n");
        }
        fileWriter.close();
        // popup alert to notify user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Price Report");
        alert.setHeaderText("Product Price Report");
        alert.setContentText("Product price report saved to " + file.getAbsolutePath());
        alert.showAndWait();

        setWindowEnabled();
    }
}
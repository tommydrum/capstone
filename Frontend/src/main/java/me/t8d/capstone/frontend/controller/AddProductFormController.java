package me.t8d.capstone.frontend.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.t8d.capstone.frontend.model.Inventory;
import me.t8d.capstone.frontend.model.Part;
import me.t8d.capstone.frontend.model.Product;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * UI/UX for the user to create and add a new product. The user fills in all fields, optionally adding associated parts, and if all is valid, creates the product, adding it into the applications Inventory.
 * RUNTIME ERROR: Resizing window was left enabled.. while the GridPanes behaved right, the bottom Pane for the save/cancel
 * buttons did not move with the resize, leaving it possible to hide the buttons. It was also possible to resize the
 * window smaller to have the form cover up the form label to the left, and smaller yet to make the radio button labels
 * unreadable. The simplest fix is just to disable window resizing.
 * @author Thomas Miller
 */

public class AddProductFormController {
    @FXML
    private TextField searchField;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button partRemoveBtn;
    @FXML
    private TableColumn<Part, Float> productAssociatedPriceCol;
    @FXML
    private TableColumn<Part, Integer> productAssociatedInvCol;
    @FXML
    private TableColumn<Part, String> productAssociatedNameCol;
    @FXML
    private TableColumn<Part, Integer> productAssociatedIDCol;
    @FXML
    private TableView<Part> productAssociatedTable;
    @FXML
    private Button partAddBtn;
    @FXML
    private TableColumn<Part, Float> productUnassociatedPriceCol;
    @FXML
    private TableColumn<Part, Integer> productUnassociatedInvCol;
    @FXML
    private TableColumn<Part, String> productUnassociatedNameCol;
    @FXML
    private TableColumn<Part, Integer> productUnassociatedIDCol;
    @FXML
    private TableView<Part> productUnassociatedTable;
    @FXML
    private TextField productMin;
    @FXML
    private TextField productMax;
    @FXML
    private TextField productPrice;
    @FXML
    private TextField productInvLevel;
    @FXML
    private TextField productName;
    @FXML
    private VBox rootVBox;
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private ObservableList<Part> associatedPartsCpy = FXCollections.observableArrayList();
    private ObservableList<Part> unassociatedParts;
    private ObservableList<Part> unassociatedPartsCpy;
    private MainFormController mainControllerRef;
    private int productId;
    /**
     * Initialize the controller by setting up the tables (prefilling the unassociated parts to the inventory's parts), and all the textProperties for all forms.
     * @param mainFormControllerRef as the reference to the running MainFormController
     * @param inv as the reference to the MainController's inventory.
     * @param newProductId as the ID to assign to the new product.
     */
    public void initialize(MainFormController mainFormControllerRef, Inventory inv, int newProductId) {
        mainControllerRef = mainFormControllerRef;
        unassociatedParts = FXCollections.observableArrayList(inv.getAllParts()); //shallow copy of all part refs
        productId = newProductId;
        //Initialize both tables
        productUnassociatedTable.setItems(unassociatedParts);
        productUnassociatedIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productUnassociatedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productUnassociatedInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productUnassociatedPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productAssociatedTable.setItems(associatedParts);
        productAssociatedIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productAssociatedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productAssociatedInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productAssociatedPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        // Setup the listener for the search bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            partSearch(newValue);
        });
        //Manage all the TextField input validations
        productInvLevel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                productInvLevel.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        productPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    // Remove currency symbol and commas
                    String sanitizedValue = newValue.replaceAll("[$,]", "");
                    double value = Double.parseDouble(sanitizedValue);
                    String formattedValue = currencyFormat.format(value);
                    productPrice.setText(formattedValue);
                } catch (NumberFormatException e) {
                    productPrice.setText(oldValue);
                }
            }
        });
        productMax.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                productMax.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        productMin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                productMin.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        // Keep copies of the part lists so if the search is emptied, it can go back to the original
        unassociatedPartsCpy = FXCollections.observableArrayList(unassociatedParts);
        associatedPartsCpy = FXCollections.observableArrayList(associatedParts);
    }
    /**
     * Creates the product and adds it into the inventory. This Performs input validations on all fields, give errors/prevent continuation if invalid inputs
     *    -- if input is valid, create new product, and give it back to the MainFormController to add into its inventory.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void saveBtnOnClick(ActionEvent actionEvent) {
        String name = productName.getText();
        String invLevel = productInvLevel.getText();
        String price = productPrice.getText();
        String min = productMin.getText();
        String max = productMax.getText();
        // Name validation
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a product name");
            alert.showAndWait();
            productName.setStyle("-fx-border-color: red;");
            return;
        } else {
            productName.setStyle("");
        }
        // Inv level validation
        if (invLevel.isEmpty()) {
            // Note, number enforcement is handled by the field input restrictions
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the Inventory Level");
            alert.showAndWait();
            productInvLevel.setStyle("-fx-border-color: red;");
            return;
        } else {
            productInvLevel.setStyle("");
        }
        int invLevelInt = Integer.parseInt(invLevel);
        // Price/cost validation
        if (price.isEmpty() || price.equals("$0.00")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the Cost per Product");
            alert.showAndWait();
            productPrice.setStyle("-fx-border-color: red;");
            return;
        } else {
            productPrice.setStyle("");
        }
        double priceDbl = Double.parseDouble(price.replace('$', ' ').strip());
        // Max validation
        if (max.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the maximum allowed inventory level (Max)");
            alert.showAndWait();
            productMax.setStyle("-fx-border-color: red;");
            return;
        } else {
            productMax.setStyle("");
        }
        int maxInt = Integer.parseInt(max);
        // Min validation
        if (min.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the minimum allowed inventory level (Min)");
            alert.showAndWait();
            productMin.setStyle("-fx-border-color: red;");
            return;
        } else {
            productMin.setStyle("");
        }
        int minInt = Integer.parseInt(min);
        if (minInt > maxInt) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("The Maximum must be larger than the Minimum");
            alert.showAndWait();
            productMin.setStyle("-fx-border-color: red;");
            productMax.setStyle("-fx-border-color: red;");
            return;
        } else {
            productMin.setStyle("");
            productMax.setStyle("");
        }
        if (invLevelInt < minInt || invLevelInt > maxInt) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("The Inventory Level must be between the Maximum and Minimum");
            productInvLevel.setStyle("-fx-border-color: red;");
            alert.showAndWait();
            return;
        } else {
            productInvLevel.setStyle("");
        }

        // Create part and attach the associated parts.
        Product newProduct = new Product(productId, name, priceDbl, invLevelInt, minInt, maxInt);
        for (Part part: associatedParts) {
            newProduct.addAssociatedPart(part);
        }
        mainControllerRef.addProduct(newProduct);
        // Close window
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.close();
        mainControllerRef.setWindowEnabled();
    }
    /**
     * Cancel out of this window, discarding data, and putting the user back into the MainForm for user input.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void cancelBtnOnClick(ActionEvent actionEvent) {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.close();
        mainControllerRef.setWindowEnabled();
    }
    /**
     * Takes the currently selected part from the unassociated part table, and adds it into the associated part table (removing from unassociated parts)
     * RUNTIME ERROR: It previously allowed adding an empty item (example: if the productUnassociatedTable was empty)
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    private void addBtnOnClick(ActionEvent actionEvent) { //add selected item on unassociatedParts to associated
        Part selectedItem = productUnassociatedTable.getFocusModel().getFocusedItem();
        if (selectedItem == null) {
            return;
        }
        if (associatedParts.contains(selectedItem) || associatedPartsCpy.contains(selectedItem)) {
            return;
        }
        unassociatedParts.remove(selectedItem);
        unassociatedPartsCpy.remove(selectedItem);
        associatedParts.add(selectedItem);
        associatedPartsCpy.add(selectedItem);
    }
    /**
     * Takes the currently selected part from the associated part table, and adds it into the unassociated part table (removing from associated parts)
     *      -- This prompts for user confirmation before continuing.
     * RUNTIME ERROR: It previously allowed working with empty items (example: if the productAssociatedTable was empty)
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    @FXML
    public void deleteBtnOnClick(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Action Confirmation");
        alert.setContentText("Are you sure you de-associate this part?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Part selectedItem = productAssociatedTable.getFocusModel().getFocusedItem();
            if (selectedItem == null) {
                return;
            }
            associatedParts.remove(selectedItem);
            associatedPartsCpy.remove(selectedItem);
            unassociatedParts.add(selectedItem);
            unassociatedPartsCpy.add(selectedItem);
        }
    }
    /**
     * Searches both tables for a matching name or ID
     * RUNTIME ERROR: When using the search, attempting to add/remove didn't get reflected in view, causing the same item to
     *         be able to be duplicated (as seen when the search is cleared). This was fixed by setting the tables AND their corresponding
     *         observableLists to the newly created filtered(Un)AssociatedLists. Previously this was only setting the Table view.
     *         and not the data-structure that was used within the rest of the class.
     * @param input as the user input for search bar. This filters down the both part tables according to matching name or part ID.
     *              - This resets both tables to the original state upon clearing the input.
     */
    private void partSearch(String input) {
        ObservableList<Part> filteredAssociatedList = FXCollections.observableArrayList();
        ObservableList<Part> filteredUnassociatedList = FXCollections.observableArrayList();
        // If the input is empty/clear, reset the list to all parts
        if (input == null || input.isEmpty())
        {
            filteredAssociatedList = associatedPartsCpy;
            filteredUnassociatedList = unassociatedPartsCpy;
        }
        // If the input is an int, assume it's an ID and cast it to an int and to the search. No result means an empty list.
        else if (input.matches("^-?\\d+$")) {
            int id = Integer.parseInt(input);
            try {
                Part foundAssociatedPart = lookupPart(id, associatedPartsCpy);
                filteredAssociatedList.add(foundAssociatedPart);
            } catch (NoSuchElementException ignored) {};
            try {
                Part foundUnassociatedPart = lookupPart(id, unassociatedPartsCpy);
                filteredUnassociatedList.add(foundUnassociatedPart);
            } catch (NoSuchElementException ignored) {};
        // If the input is a string, assume it's a name and perform a lookup as such.
        } else {
            try {
                filteredAssociatedList = lookupPart(input, associatedPartsCpy);
            } catch (NoSuchElementException ignored) {};
            try {
                filteredUnassociatedList = lookupPart(input, unassociatedPartsCpy);
            } catch (NoSuchElementException ignored) {};
        }
        productAssociatedTable.setItems(filteredAssociatedList);
        productUnassociatedTable.setItems(filteredUnassociatedList);
        associatedParts = filteredAssociatedList;
        unassociatedParts = filteredUnassociatedList;
    }
    /**
     * Note: this exists as a copy from the inventory function, but since this isn't using an Inventory for it's temporary tables, this is needed.
     * @param partName the name of the desired part to find within the inventory
     * @return the desired part
     * @throws NoSuchElementException if no part by the provided name is found
     */
    private ObservableList<Part> lookupPart(String partName, ObservableList<Part> lookupList) {
        ObservableList<Part> foundParts = FXCollections.observableArrayList();
        for (Part part: lookupList) {
            if (part.getName().equals(partName)) foundParts.add(part);
        }
        if (foundParts.isEmpty()) throw new NoSuchElementException();
        else return foundParts;
    }
    /**
     * Note: this exists as a copy from the inventory function, but since this isn't using an Inventory for it's temporary tables, this is needed.
     * @param partId the ID of the desired part to find within the inventory
     * @return The desired part
     * @throws NoSuchElementException if no part by the provided ID is found
     */
    public Part lookupPart(int partId, ObservableList<Part> lookupList) {
        for (Part part: lookupList) {
            if (part.getId() == partId) return part;
        }
        throw new NoSuchElementException();
    }
}

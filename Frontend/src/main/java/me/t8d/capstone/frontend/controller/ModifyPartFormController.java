package me.t8d.capstone.frontend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.t8d.capstone.frontend.model.InHouse;
import me.t8d.capstone.frontend.model.Outsourced;
import me.t8d.capstone.frontend.model.Part;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * UI/UX for the user to modify an existing part. The forms are prefilled with the parts existing data, allowing
 * the user to modify data as long as it's valid. If valid, the part is updated within the applications inventory.
 * RUNTIME ERROR: Resizing window was left enabled.. while the GridPanes behaved right, the bottom Pane for the save/cancel
 * buttons did not move with the resize, leaving it possible to hide the buttons. It was also possible to resize the
 * window smaller to have the form cover up the form label to the left, and smaller yet to make the radio button labels
 * unreadable. The simplest fix is just to disable window resizing.
 * @author Thomas Miller
 */

//Found bug:
public class ModifyPartFormController {
    @FXML
    private TextField inHouseId;
    @FXML
    private TextField outsourcedId;
    @FXML
    private TextField inHousePartName;
    @FXML
    private TextField inHouseInvLevel;
    @FXML
    private TextField inHousePrice;
    @FXML
    private TextField inHouseMax;
    @FXML
    private TextField inHouseMin;
    @FXML
    private TextField inHouseMachineID;
    @FXML
    private TextField outsourcedName;
    @FXML
    private TextField outsourcedInvLevel;
    @FXML
    private TextField outsourcedPrice;
    @FXML
    private TextField outsourcedMax;
    @FXML
    private TextField outsourcedMin;
    @FXML
    private TextField outsourcedCompanyName;
    @FXML
    private RadioButton outsourcedRadioBtn;
    @FXML
    private RadioButton inHouseRadioBtn;
    @FXML
    private GridPane inHouseGridPane;
    @FXML
    private GridPane outsourcedGridPane;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ToggleGroup viewToggleGroup;
    @FXML
    private VBox rootVBox;
    private MainFormController mainControllerRef;
    private Part part;
    private int index;
    private boolean isInHouse;
    private boolean isOutsourced;
    /**
     * Initialize the controller by creating the management for the inHouse/outsourced panes, and all the textProperties for all forms,
     *      and prefilling all forms with data from the given/selected part.
     * RUNTIME ERROR: - If the part being modified was an outsourced part, the radio buttons weren't in the right state.
     * This didn't allow the ability to save an outsourced as inHouse, as the forms state was still inHouse.
     * Adding the radio button fire depending on the part type fixes this issue.
     * @param mainFormControllerRef as the reference to the running MainFormController
     * @param oldPart as the old part to prefill form into
     * @param oldIndex as the index of the original part, so when this is submitted for updating, the modified part overwrites the old.
     */
    public void initialize(MainFormController mainFormControllerRef, Part oldPart, int oldIndex) {
        mainControllerRef = mainFormControllerRef;
        part = oldPart;
        index = oldIndex;
        isInHouse = oldPart.getClass().getSimpleName().equals("InHouse");

        // Pre-fill in form data with existing class data.
        // Always fill every field (except for InHouse/Outsourced specific fields) to the common part field.
        inHouseId.setText(String.valueOf(part.getId()));
        outsourcedId.setText(String.valueOf(part.getId()));
        inHousePartName.setText(part.getName());
        inHouseInvLevel.setText(String.valueOf(part.getStock()));
        inHousePrice.setText("$" + String.valueOf(part.getPrice()));
        inHouseMax.setText(String.valueOf(part.getMax()));
        inHouseMin.setText(String.valueOf(part.getMin()));
        outsourcedName.setText(part.getName());
        outsourcedInvLevel.setText(String.valueOf(part.getStock()));
        outsourcedPrice.setText("$" + String.valueOf(part.getPrice()));
        outsourcedMax.setText(String.valueOf(part.getMax()));
        outsourcedMin.setText(String.valueOf(part.getMin()));
        if (isInHouse) {
            inHouseGridPane.setDisable(false);
            inHouseGridPane.setVisible(true);
            outsourcedGridPane.setDisable(true);
            outsourcedGridPane.setVisible(false);
            isOutsourced = false;

            inHouseMachineID.setText(String.valueOf(((InHouse)part).getMachineId()));
            inHouseRadioBtn.fire();

        } else {
            inHouseGridPane.setDisable(true);
            inHouseGridPane.setVisible(false);
            outsourcedGridPane.setDisable(false);
            outsourcedGridPane.setVisible(true);
            isOutsourced = true;

            outsourcedCompanyName.setText(((Outsourced)part).getCompanyName());
            outsourcedRadioBtn.fire();
        }

        // Adds all the listeners to the form objects
        viewToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == inHouseRadioBtn) {
                isInHouse = true;
                inHouseGridPane.setDisable(false);
                inHouseGridPane.setVisible(true);
                isOutsourced = false;
                outsourcedGridPane.setDisable(true);
                outsourcedGridPane.setVisible(false);
            }
            else { //outsourced
                isOutsourced = false;
                inHouseGridPane.setDisable(true);
                inHouseGridPane.setVisible(false);
                isOutsourced = true;
                outsourcedGridPane.setDisable(false);
                outsourcedGridPane.setVisible(true);
            }
        });
        //Manage all the TextField input validations
        //Inventory level should only be an int, delete non-numeric characters as added.
        inHouseInvLevel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inHouseInvLevel.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        outsourcedInvLevel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                outsourcedInvLevel.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        //Price/cost needs to fit a dollar like regex.
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        inHousePrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    // Remove currency symbol and commas
                    String sanitizedValue = newValue.replaceAll("[$,]", "");
                    double value = Double.parseDouble(sanitizedValue);
                    String formattedValue = currencyFormat.format(value);
                    inHousePrice.setText(formattedValue);
                } catch (NumberFormatException e) {
                    inHousePrice.setText(oldValue);
                }
            }
        });
        outsourcedPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    // Remove currency symbol and commas
                    String sanitizedValue = newValue.replaceAll("[$,]", "");
                    double value = Double.parseDouble(sanitizedValue);
                    String formattedValue = currencyFormat.format(value);
                    outsourcedPrice.setText(formattedValue);
                } catch (NumberFormatException e) {
                    outsourcedPrice.setText(oldValue);
                }
            }
        });
        // Max and Min just need to be an integer.
        inHouseMax.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inHouseMax.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        outsourcedMax.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                outsourcedMax.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        inHouseMin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inHouseMin.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        outsourcedMin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                outsourcedMin.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        // The in-house machine ID needs to be an int
        inHouseMachineID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inHouseMachineID.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    /**
     * Saves the modified part. This performs input validations on all fields, give errors/prevent continuation if invalid inputs
     *      -- if input is valid, create new part, and give it back to the MainFormController (along with the old index)
     *      for the MainFormController to overwrite the old part with this new part.
     * RUNTIME ERROR: - this accepted $0.00 as a value in the price field
     * RUNTIME ERROR: - this didn't remove the $ in the price field, and hence threw an exception on the parse.
     * RUNTIME ERROR: - The final part management was placed only on the outsourced, so submitting inHouse didn't do anything.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    public void saveBtnOnClick(ActionEvent actionEvent) {
        //mainControllerRef.addPart(newPart);
        var toggle = viewToggleGroup.getSelectedToggle();
        if (toggle == inHouseRadioBtn) {
            // Variable collection
            String name = inHousePartName.getText();
            String invLevel = inHouseInvLevel.getText();
            String price = inHousePrice.getText();
            String min = inHouseMin.getText();
            String max = inHouseMax.getText();
            String machineId = inHouseMachineID.getText();
            // Name validation
            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a part name");
                alert.showAndWait();
                inHousePartName.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHousePartName.setStyle("");
            }
            // Inv level validation
            if (invLevel.isEmpty()) {
                // Note, number enforcement is handled by the field input restrictions
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the Inventory Level");
                alert.showAndWait();
                inHouseInvLevel.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHouseInvLevel.setStyle("");
            }
            int invLevelInt = Integer.parseInt(invLevel);
            // Price/cost validation
            if (price.isEmpty() || price.equals("$0.00")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the Cost per Part");
                alert.showAndWait();
                inHousePrice.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHousePrice.setStyle("");
            }
            double priceDbl = Double.parseDouble(price.replace('$', ' ').strip());
            // Max validation
            if (max.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the maximum allowed inventory level (Max)");
                alert.showAndWait();
                inHouseMax.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHouseMax.setStyle("");
            }
            int maxInt = Integer.parseInt(max);
            // Min validation
            if (min.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the minimum allowed inventory level (Min)");
                alert.showAndWait();
                inHouseMin.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHouseMin.setStyle("");
            }
            int minInt = Integer.parseInt(min);
            if (minInt > maxInt) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("The Maximum must be larger than the Minimum");
                alert.showAndWait();
                inHouseMin.setStyle("-fx-border-color: red;");
                inHouseMax.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHouseMin.setStyle("");
                inHouseMax.setStyle("");
            }
            if (invLevelInt < minInt || invLevelInt > maxInt) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("The Inventory Level must be between the Maximum and Minimum");
                inHouseInvLevel.setStyle("-fx-border-color: red;");
                alert.showAndWait();
                return;
            } else {
                inHouseInvLevel.setStyle("");
            }
            // Machine ID validation
            if (machineId.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the Machine ID");
                alert.showAndWait();
                inHouseMachineID.setStyle("-fx-border-color: red;");
                return;
            } else {
                inHouseMachineID.setStyle("");
            }
            int machineIdInt = Integer.parseInt(machineId);
            mainControllerRef.replacePart(new InHouse(part.getId(), name, priceDbl, invLevelInt, minInt, maxInt, machineIdInt), index);
        }
        else { //outsourced
            // Variable collection
            String name = outsourcedName.getText();
            String invLevel = outsourcedInvLevel.getText();
            String price = outsourcedPrice.getText();
            String min = outsourcedMin.getText();
            String max = outsourcedMax.getText();
            String companyName = outsourcedCompanyName.getText();
            // Name validation
            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a part name");
                alert.showAndWait();
                outsourcedName.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedName.setStyle("");
            }
            // Inv level validation
            if (invLevel.isEmpty()) {
                // Note, number enforcement is handled by the field input restrictions
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the Inventory Level");
                alert.showAndWait();
                outsourcedInvLevel.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedInvLevel.setStyle("");
            }
            int invLevelInt = Integer.parseInt(invLevel);
            // Price/cost validation
            if (price.isEmpty() || price.equals("$0.00")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the Cost per Part");
                alert.showAndWait();
                outsourcedPrice.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedPrice.setStyle("");
            }
            double priceDbl = Double.parseDouble(price.replace('$', ' ').strip());
            // Max validation
            if (max.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the maximum allowed inventory level (Max)");
                alert.showAndWait();
                outsourcedMax.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedMax.setStyle("");
            }
            int maxInt = Integer.parseInt(max);
            // Min validation
            if (min.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the minimum allowed inventory level (Min)");
                alert.showAndWait();
                outsourcedMin.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedMin.setStyle("");
            }
            int minInt = Integer.parseInt(min);
            if (minInt > maxInt) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("The Maximum must be larger than the Minimum");
                alert.showAndWait();
                outsourcedMin.setStyle("-fx-border-color: red;");
                outsourcedMax.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedMin.setStyle("");
                outsourcedMax.setStyle("");
            }
            if (invLevelInt < minInt || invLevelInt > maxInt) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("The Inventory Level must be between the Maximum and Minimum");
                outsourcedInvLevel.setStyle("-fx-border-color: red;");
                alert.showAndWait();
                return;
            } else {
                outsourcedInvLevel.setStyle("");
            }
            // Company Name validation
            if (companyName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the outsourced company's name");
                alert.showAndWait();
                outsourcedCompanyName.setStyle("-fx-border-color: red;");
                return;
            } else {
                outsourcedCompanyName.setStyle("");
            }
            // Create part and add into mainController's inventory
            mainControllerRef.replacePart(new Outsourced(part.getId(), name, priceDbl, invLevelInt, minInt, maxInt, companyName), index);
        }
        // Close window
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.close();
        mainControllerRef.setWindowEnabled();
    }
    /**
     * Cancel out of this window, discarding data, and putting the user back into the MainForm for user input.
     * @param actionEvent is unused, but passed by the FXML Button as an onClick argument.
     */
    public void cancelBtnOnClick(ActionEvent actionEvent) {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.close();
        mainControllerRef.setWindowEnabled();
    }
}

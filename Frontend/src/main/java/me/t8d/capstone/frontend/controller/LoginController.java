package me.t8d.capstone.frontend.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.t8d.capstone.frontend.MainApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import me.t8d.capstone.frontend.model.Inventory;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;


/**
 * Represents the login screen with the username and password fields.
 * Note: this class is a controller for src/main/resources/me/t8d/c195/login.fxml
 * This shows the current region, and if the region is France, the text is in French.
 *
 * @author Thomas Miller
 */
public class LoginController {

    @FXML
    private Text passwordText;
    @FXML
    private Text usernameText;
    @FXML
    private Button loginBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Text locationText;
    @FXML
    private Text locationValue;
    private Locale locale;
    /**
     * Constructs a new LoginController instance.
     */
    @FXML
    public void initialize() {
        setLocale();
        // set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Platform.runLater(() -> genericErrorAlert(throwable));
        });

    }

    /**
     * This method is called when the login button is clicked. It validates the input, authenticates the user, and opens the main window.
     * @param actionEvent The event that triggered this method.
//     * @throws SQLException If there is a problem connecting to the database.
     */
    public void onLoginClick(ActionEvent actionEvent) { //throws SQLException {
        boolean isValid = true;
        // clean input, validate input, authenticate user, open main window
        // prevent sqli
        if (username.getText().matches(".*[;=].*") || password.getText().matches(".*[;=].*")) {
            // Ignore input and return
            return;
        }
        // validate input
        if (username.getText().isEmpty()) {
            usernameText.setStyle("-fx-fill: red;");
            isValid = false;
        } else {
            //Reset usernameText
            usernameText.setStyle("");
        }
        if (password.getText().isEmpty()) {
            passwordText.setStyle("-fx-fill: red;");
            isValid = false;
        } else {
            //Reset passwordText
            passwordText.setStyle("");
        }
        if (!isValid) {
            return;
        }
        String clientId = "1jj707hfhj80cqnsl2vep5qq5j";
        String clientSecret = "1bnmu2l1jpk8p1av2feskt8vb29mg7hk02u9o3or73hldpkcl2d6";
        String secretHash = calculateSecretHash(clientId, clientSecret, username.getText());

        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(software.amazon.awssdk.regions.Region.AP_NORTHEAST_3)
                .build()) {
            Map<String, String> authParameters = new HashMap<>();
            authParameters.put("USERNAME", username.getText());
            authParameters.put("PASSWORD", password.getText());
            authParameters.put("SECRET_HASH", secretHash);
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .clientId(clientId)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(authParameters)
                    .build();

            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

            if (authResponse.authenticationResult() != null) {
                String idToken = authResponse.authenticationResult().idToken();
                Inventory.setToken(idToken);
                System.out.println("ID Token: " + idToken);
                // Use this token for your APIGateway REST calls
            } else {
                System.out.println("Authentication failed or requires additional steps.");
                // Invalid password
                // Create error dialog with error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                String errorMessage = "Invalid username or password.";
                alert.setContentText(errorMessage);
                alert.showAndWait();
                username.setStyle("-fx-border-color: red;");
                password.setStyle("-fx-border-color: red;");
                return;
            }
        } catch(NotAuthorizedException e) {
            // Invalid password
            // Create error dialog with error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            String errorMessage = "Invalid username or password.";
            alert.setContentText(errorMessage);
            alert.showAndWait();
            username.setStyle("-fx-border-color: red;");
            password.setStyle("-fx-border-color: red;");
            return;
        } catch (CognitoIdentityProviderException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            // open main window
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/MainForm.fxml"));
            stage.setTitle("Part and Product Management Portal");
            stage.setResizable(false);
            stage.setWidth(950);  // Set the width to 800 pixels
            stage.setHeight(550);  // Set the height to 600 pixels
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (IOException e) { //throws if fxml file not found
            e.printStackTrace();
        }
    }
    /**
     * This method is called when the close button is clicked. It closes the application.
     * @param actionEvent The event that triggered this method.
     */
    public void onCloseClick(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
    /**
     * Sets the locale and the window text language according to the system's locale.
     */
    public void setLocale() {
        locale = Locale.getDefault();
        // Location in - Country (Timezone)
        locationValue.setText(locale.getDisplayCountry() + " (" + ZoneId.systemDefault() + ")");
        usernameText.setText("Username");
        passwordText.setText("Password");
        loginBtn.setText("Login");
        closeBtn.setText("Close");
        locationText.setText("Location");
    }
    /**
     * Creates a generic error alert with the specified throwable.
     * @param throwable The throwable to create the alert with.
     */
    public void genericErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        // English/French error message and title
        String errorMessage = "An error occurred.";
        String title = "Error";

        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.setContentText(throwable.getMessage());

        // Create a TextArea for the stack trace
        TextArea textArea = new TextArea(Arrays.toString(throwable.getStackTrace()));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        // Set expandable Exception into the dialog pane.
        throwable.printStackTrace();
        alert.getDialogPane().setExpandableContent(textArea);

        alert.showAndWait();
    }
    private static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        String hashString = userName + userPoolClientId;
        byte[] hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, userPoolClientSecret).hmac(hashString);
        return Base64.getEncoder().encodeToString(hmac);
    }
}

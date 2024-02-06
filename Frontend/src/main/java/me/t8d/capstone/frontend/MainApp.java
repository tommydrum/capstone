/**
 * Main entrypoint for the MVC App
 */
package me.t8d.capstone.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Start the MainForm window along with its controller.
 * FUTURE ENHANCEMENT: Within each of the MainFormController's child classes (each sub-window), I currently pass a reference
 * to the parent controller (the MainFormController) in each of the initialize functions. This is largely used to have the child windows
 * enable/disable the parent window, and to add/modify parts/products in its inventory. I'd rather not pass the MainFormController ref,
 * and for the add/modify parts/products, but I'd want to make some sort of callback functionality for its children, to have the parent class
 * (the MainFormController) be the one to accept the new/modified part/product, and manage its own window status.
 *
 * FUTURE ENHANCEMENT: There is a bit of code cleanup that can be done, slimming down FXML objects that have ID's and entries in the controllers,
 * but not used in code. (Those had alternate ways of assuming the status of objects).
 *
 * FUTURE ENHANCEMENT: In the Product controllers, I take a reference of the inventory, which is only used for the associated/unassociated parts tables.
 * I'd rather just pass the list of all parts. Honestly a simple change, but it was too low of a priority to do so.
 *
 * FUTURE ENHANCEMENT: There's also room for me to remove redundant code regarding the add/modify components.. those realistically can be modified
 * to be combined, just having a simple inheritance to a shared part/product controller. This would also allow me to combine the modify/add FXML's together.
 * Granted, I haven't tested if FXML can be attached to an interface class, when the actual controller was inheriting the interface. FXML may not support
 * this.
 * @author Thomas Miller
 */
public class MainApp extends Application {
    /**
     * JavaFX application entrypoint.
     * @param stage provided by JavaFX, stage for the JavaFX application
     * @throws IOException if FXML failed to load
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/login.fxml"));
        stage.setTitle("Part and Product Management Portal");
        stage.setResizable(false);
        stage.setWidth(550);  // Set the width to 800 pixels
        stage.setHeight(350);  // Set the height to 600 pixels
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /**
     * Java execution entrypoint
     * The Javadoc index is located at c482/apidocs/index.html
     * @param args for the argv args, unused.
     */
    public static void main(String[] args) {
        launch();
    }
}
package me.t8d.capstone.frontend.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import me.t8d.capstone.frontend.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit.ApplicationTest;
import org.mockito.Mockito;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainFormControllerTest extends ApplicationTest {
    @Mock
    private Inventory inventory;
    @InjectMocks
    private MainFormController controller;

    @Override
    public void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        inventory = mock(Inventory.class);

        // Mock parts
        Part part1 = new Outsourced(1, "Part 1", 1.0, 1, 1, 1, "Company 1");
        Part part2 = new InHouse(2, "Part 2", 2.0, 2, 2, 2, 2);
        ObservableList<Part> parts = FXCollections.observableArrayList(Arrays.asList(part1, part2));
        when(inventory.getAllParts()).thenReturn(parts);
        when(inventory.lookupPart(1)).thenReturn(part1);
        when(inventory.lookupPart(2)).thenReturn(part2);
        when(inventory.lookupPart("Part 1")).thenReturn(FXCollections.observableArrayList(List.of(part1)));
        when(inventory.lookupPart("Part 2")).thenReturn(FXCollections.observableArrayList(List.of(part2)));
        when(inventory.deletePart(part1)).thenReturn(true);
        when(inventory.deletePart(part2)).thenReturn(true);

        // Mock products
        Product product1 = new Product(1, "Product 1", 1.0, 1, 1, 1);
        Product product2 = new Product(2, "Product 2", 2.0, 2, 2, 2);
        product1.addAssociatedPart(part1);
        product2.addAssociatedPart(part2);
        ObservableList<Product> products = FXCollections.observableArrayList(Arrays.asList(product1, product2));
        when(inventory.getAllProducts()).thenReturn(products);
        when(inventory.lookupProduct(1)).thenReturn(product1);
        when(inventory.lookupProduct(2)).thenReturn(product2);
        when(inventory.lookupProduct("Product 1")).thenReturn(FXCollections.observableArrayList(List.of(product1)));
        when(inventory.lookupProduct("Product 2")).thenReturn(FXCollections.observableArrayList(List.of(product2)));
        when(inventory.deleteProduct(product1)).thenReturn(true);
        when(inventory.deleteProduct(product2)).thenReturn(true);

        controller = new MainFormController(inventory);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/t8d/capstone/frontend/view/MainForm.fxml"));
        loader.setControllerFactory(param -> {
            if (param.isAssignableFrom(MainFormController.class)) {
                return controller;
            } else {
                try {
                    return param.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
    // Test that the initialize method populates the parts and products tables
    @Test
    public void testInitialize() {
        controller.initialize();

        TableView<Part> partsTable = lookup("#PartsTable").queryTableView();
        TableView<Product> productsTable = lookup("#ProductsTable").queryTableView();

        assertEquals(2, partsTable.getItems().size());
        assertEquals("Part 1", partsTable.getItems().get(0).getName());
        assertEquals("Part 2", partsTable.getItems().get(1).getName());
        assertEquals(2, productsTable.getItems().size());
        assertEquals("Product 1", productsTable.getItems().get(0).getName());
        assertEquals("Product 2", productsTable.getItems().get(1).getName());
    }
    // Test that the addPart method opens the AddPartForm
    @Test
    public void testAddPart() {
        clickOn("#AddPartButton");
        // Verify that the AddPartForm is open
        verifyThat("#AddPartForm", hasText("Add Part"));
    }
}
package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;

import java.sql.ResultSet;
import java.util.Map;
/**
 * Serverless function to add a product to the database
 * @author Thomas Miller
 */

public class AddProduct implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public AddProduct() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public AddProduct(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to add a product to the database
     * @param stringObjectMap the request body, including the product to add
     * @param context the context of the request
     * @return the response to the request, including the status code and body
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("body").toString().matches(regex)) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        // Parse the request body into a Product object
        ObjectMapper mapper = new ObjectMapper();
        String body = (String) stringObjectMap.get("body");
        System.out.println(body);
        int id = 0; // this will be set to the id of the product we add
        try {
            JsonNode bodyNode = mapper.readTree(body);
            String name = bodyNode.get("name").asText();
            double price = bodyNode.get("price").asDouble();
            int stock = bodyNode.get("stock").asInt();
            int min = bodyNode.get("min").asInt();
            int max = bodyNode.get("max").asInt();
            // Add the product to the database
            db.getStmt().execute("INSERT INTO Products (name, price, stock, min, max) VALUES ('" + name + "', " + price + ", " + stock + ", " + min + ", " + max + ")");
            ResultSet rs = db.getStmt().executeQuery("SELECT id FROM Products WHERE name = '" + name + "' AND price = " + price + " AND stock = " + stock + " AND min = " + min + " AND max = " + max);
            rs.next(); // move the cursor to the first row
            id = rs.getInt("id");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(e)
                    .build();
        }
        // Add the associatedParts to the productParts table
        try {
            JsonNode bodyNode = mapper.readTree(body);
            JsonNode associatedPartsNode = bodyNode.get("allAssociatedParts");
            if (associatedPartsNode != null && associatedPartsNode.isArray()) {
                for (JsonNode associatedPartNode : associatedPartsNode) {
                    // Parse associatedPart into a Part object
                    int partId = associatedPartNode.get("id").asInt();
                    // update this part in the productparts table
                    try {
                        // Then add the new associatedPart to the productParts table
                        db.getStmt().execute("INSERT INTO ProductParts (productId, partId) VALUES (" + id + ", " + partId + ")");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                        return ApiGatewayResponse.builder()
                                .setStatusCode(400)
                                .setObjectBody(e)
                                .build();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(e)
                    .build();
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .build();
    }
}

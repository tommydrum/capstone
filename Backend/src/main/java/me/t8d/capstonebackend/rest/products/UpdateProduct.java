package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class UpdateProduct implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
        // Retrieve product fields from request body, as well as associatedParts into an array
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("body").toString().matches(regex)) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        ObjectMapper mapper = new ObjectMapper();
        String body = (String) stringObjectMap.get("body");
        System.out.println(body);
        JsonNode bodyNode = null;
        try {
            bodyNode = mapper.readTree(body);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(e)
                    .build();
        }
        String name = bodyNode.get("name").asText();
        double price = bodyNode.get("price").asDouble();
        int stock = bodyNode.get("stock").asInt();
        int min = bodyNode.get("min").asInt();
        int max = bodyNode.get("max").asInt();
        int id = bodyNode.get("id").asInt();
//        int id = (int) stringObjectMap.get("id");
//        String name = (String) stringObjectMap.get("name");
//        double price = (double) stringObjectMap.get("price");
//        int stock = (int) stringObjectMap.get("stock");
//        int min = (int) stringObjectMap.get("min");
//        int max = (int) stringObjectMap.get("max");
        // Retrieve associatedParts from request body
        // Clear the productParts table for this product
        try {
            Statement statement = new DB().getStmt();
            statement.execute("DELETE FROM ProductParts WHERE productId = " + id);
        } catch (SQLException e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(e)
                    .build();
        }
        try {
            JsonNode associatedPartsNode = bodyNode.get("allAssociatedParts");
            if (associatedPartsNode != null && associatedPartsNode.isArray()) {
                for (JsonNode associatedPartNode : associatedPartsNode) {
                    // Parse associatedPart into a Part object
                    int partId = associatedPartNode.get("id").asInt();
                    // update this part in the productparts table
                    try {
                        // Then add the new associatedPart to the productParts table
                        new DB().getStmt().execute("INSERT INTO ProductParts (productId, partId) VALUES (" + id + ", " + partId + ")");
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
        // Update the product in the database
        try {
            Statement statement = new DB().getStmt();
            statement.execute("UPDATE Products SET name = '" + name + "', price = " + price + ", stock = " + stock + ", min = " + min + ", max = " + max + " WHERE id = " + id);
        } catch (SQLException e) {
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

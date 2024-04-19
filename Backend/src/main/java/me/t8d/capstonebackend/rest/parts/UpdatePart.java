package me.t8d.capstonebackend.rest.parts;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.InHouse;
import me.t8d.capstonebackend.model.Outsourced;
import me.t8d.capstonebackend.model.Part;

import java.util.Map;

/**
 * Serverless function to update a part in the database
 * @author Thomas Miller
 */
public class UpdatePart implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public UpdatePart() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public UpdatePart(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to update a part in the database
     * @param stringObjectMap the request body containing the part to update
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
        // Log the request body
        System.out.println(stringObjectMap);
        ObjectMapper mapper = new ObjectMapper();
        // Parse the request body into a Part object

        try {
            String body = (String) stringObjectMap.get("body");
            Map<String, Object> bodyMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            Part part = Part.fromJson(bodyMap);
            // Add the part to the database
            if (part instanceof InHouse) {
                db.getStmt().execute("UPDATE Parts SET name = '" + part.getName() + "', price = " + part.getPrice() + ", stock = " + part.getStock() + ", min = " + part.getMin() + ", max = " + part.getMax() + ", machineId = " + ((InHouse) part).getMachineId() + ", type = 'InHouse' WHERE id = " + part.getId());
            } else {
                db.getStmt().execute("UPDATE Parts SET name = '" + part.getName() + "', price = " + part.getPrice() + ", stock = " + part.getStock() + ", min = " + part.getMin() + ", max = " + part.getMax() + ", companyName = '" + ((Outsourced) part).getCompanyName() + "', type = 'Outsourced' WHERE id = " + part.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .setObjectBody(e)
                    .build();
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .build();
//        DB db = new DB();
//        // Parse the request body into a Part object
//        int id = (int) stringObjectMap.get("id");
//        String name = (String) stringObjectMap.get("name");
//        double price = (double) stringObjectMap.get("price");
//        int stock = (int) stringObjectMap.get("stock");
//        int min = (int) stringObjectMap.get("min");
//        int max = (int) stringObjectMap.get("max");
//        String type = (String) stringObjectMap.get("type");
//        int machineId = (int) stringObjectMap.get("machineId");
//        String companyName = (String) stringObjectMap.get("companyName");
//        try {
//            // Update the part in the database
//            if (companyName == null) {
//                db.getStmt().execute("UPDATE Parts SET name = '" + name + "', price = " + price + ", stock = " + stock + ", min = " + min + ", max = " + max + ", machineId = " + machineId + ", type = '" + type + "' WHERE id = " + id);
//            } else {
//                db.getStmt().execute("UPDATE Parts SET name = '" + name + "', price = " + price + ", stock = " + stock + ", min = " + min + ", max = " + max + ", companyName = '" + companyName + "', type = '" + type + "' WHERE id = " + id);
//            }
//        } catch (Exception e) {
//            return ApiGatewayResponse.builder()
//                    .setStatusCode(400)
//                    .setObjectBody(e)
//                    .build();
//        }
//        return ApiGatewayResponse.builder()
//                .setStatusCode(200)
//                .build();
    }
}

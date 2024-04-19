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
 * Serverless function to add a part to the database
 * @author Thomas Miller
 */
public class AddPart implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;

    /**
     * Default constructor to setup the database
     */
    public AddPart() {
        db = new DB();
    }

    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public AddPart(DB db) {
        this.db = db;
    }

    /**
     * Handle the request to add a part to the database
     * @param stringObjectMap the request body, including the part to add
     * @param context the context of the request
     * @return the response to the request, including the status code and body.
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
                db.getStmt().execute("INSERT INTO Parts (name, price, stock, min, max, machineId, type) VALUES ('" + part.getName() + "', " + part.getPrice() + ", " + part.getStock() + ", " + part.getMin() + ", " + part.getMax() + ", " + ((InHouse) part).getMachineId() + ", 'InHouse')");
            } else {
                db.getStmt().execute("INSERT INTO Parts (name, price, stock, min, max, companyName, type) VALUES ('" + part.getName() + "', " + part.getPrice() + ", " + part.getStock() + ", " + part.getMin() + ", " + part.getMax() + ", '" + ((Outsourced) part).getCompanyName() + "', 'Outsourced')");
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
    }
}

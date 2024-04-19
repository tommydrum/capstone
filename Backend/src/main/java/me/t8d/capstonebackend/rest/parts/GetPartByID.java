package me.t8d.capstonebackend.rest.parts;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.InHouse;
import me.t8d.capstonebackend.model.Inventory;
import me.t8d.capstonebackend.model.Outsourced;
import me.t8d.capstonebackend.model.Part;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serverless function to get a part from the database by ID
 * @author Thomas Miller
 */

public class GetPartByID implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public GetPartByID() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public GetPartByID(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to get a part from the database by ID
     * @param stringObjectMap the request body containing the id of the part to get
     * @param context the context of the request
     * @return the response to the request, including the status code and body
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        System.out.println(stringObjectMap);
        System.out.println(context);
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("pathParameters").toString().matches(regex)) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        // URI is /parts/{id}, retrieve the id
        Map<String,String> pathParameters = (Map<String,String>) stringObjectMap.get("pathParameters");
        int id = Integer.parseInt(pathParameters.get("id"));
        try {
            Statement statement = db.getStmt();
            statement.execute("SELECT * FROM Parts WHERE id = " + id);
            // Get results and serialize them into the parts model

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
//                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                int min = resultSet.getInt("min");
                int max = resultSet.getInt("max");
                int machineId;
                String companyName;
                List<Part> parts = new ArrayList<>();
                if (type.equals("InHouse")) {
                    machineId = resultSet.getInt("machineId");
                    parts.add(new InHouse(id, name, price, stock, min, max, machineId));
                    return ApiGatewayResponse.builder().
                            setStatusCode(200)
                            .setObjectBody(parts)
                            .build();
                } else {
                    companyName = resultSet.getString("companyName");
                    parts.add(new Outsourced(id, name, price, stock, min, max, companyName));
                    return ApiGatewayResponse.builder()
                            .setStatusCode(200)
                            .setObjectBody(parts)
                            .build();
                    }
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .build();
            }
        } catch (SQLException e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(e)
                    .build();
        }
    }
}

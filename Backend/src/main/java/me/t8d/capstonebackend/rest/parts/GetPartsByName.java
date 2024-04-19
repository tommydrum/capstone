package me.t8d.capstonebackend.rest.parts;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.InHouse;
import me.t8d.capstonebackend.model.Inventory;
import me.t8d.capstonebackend.model.Outsourced;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Serverless function to get matching parts from the database by name
 * @author Thomas Miller
 */
public class GetPartsByName implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public GetPartsByName() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public GetPartsByName(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to get matching parts from the database by name
     * @param stringObjectMap the request body containing the name of the part to get
     * @param context the context of the request
     * @return the response to the request, including the status code and body, containing all matching parts
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("pathParameters").toString().matches(regex)) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        Inventory inventory = new Inventory();
        // URI is /parts/byName/{}, retrieve the string
        Map<String,String> pathParameters = (Map<String,String>) stringObjectMap.get("pathParameters");
        String name = pathParameters.get("name");
        try {
            Statement statement = db.getStmt();
            statement.execute("SELECT * FROM Parts WHERE name like '%" + name + "%'");
            // Get results and serialize them into the parts model

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                String partName = resultSet.getString("name");
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                int min = resultSet.getInt("min");
                int max = resultSet.getInt("max");
                int machineId;
                String companyName;
                if (type.equals("InHouse")) {
                    machineId = resultSet.getInt("machineId");
                    inventory.addPart(new InHouse(id, partName, price, stock, min, max, machineId));
                } else {
                    companyName = resultSet.getString("companyName");
                    inventory.addPart(new Outsourced(id, partName, price, stock, min, max, companyName));
                }
            }
        } catch (SQLException e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(e)
                    .build();
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(inventory.getAllParts())
                .build();
    }
}

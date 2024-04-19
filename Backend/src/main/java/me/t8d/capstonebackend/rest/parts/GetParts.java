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
 * Serverless function to get all parts from the database
 * @author Thomas Miller
 */
public class GetParts implements RequestHandler<Map<String, Object>, ApiGatewayResponse>  {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public GetParts() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public GetParts(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to get all parts from the database
     * @param stringObjectMap the request body, ignored in this function
     * @param context the context of the request
     * @return the response to the request, including the status code and body, containing all parts
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        List<Part> parts = new ArrayList<>();
        try {
            Statement statement = db.getStmt();
            statement.execute("SELECT * FROM Parts");
            // Get results and serialize them into the parts model

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
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
                    parts.add(new InHouse(id, name, price, stock, min, max, machineId));
                } else {
                    companyName = resultSet.getString("companyName");
                    parts.add(new Outsourced(id, name, price, stock, min, max, companyName));
                }
            }
        } catch (Exception e) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(e)
                    .build();
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(parts)
                .build();
    }
}

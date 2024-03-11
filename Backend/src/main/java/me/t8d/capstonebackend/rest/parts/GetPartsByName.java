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

public class GetPartsByName implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    public GetPartsByName() {
        db = new DB();
    }
    public GetPartsByName(DB db) {
        this.db = db;
    }
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

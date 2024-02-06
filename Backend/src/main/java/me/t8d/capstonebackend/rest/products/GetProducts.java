package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.Inventory;
import me.t8d.capstonebackend.model.Part;
import me.t8d.capstonebackend.model.Product;

import java.sql.ResultSet;
import java.util.Map;

public class GetProducts implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
        DB db = new DB();
        Inventory inventory = new Inventory();
        try {
            db.getStmt().execute("SELECT * FROM Products");
            ResultSet resultSet = db.getStmt().getResultSet();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                int min = resultSet.getInt("min");
                int max = resultSet.getInt("max");
                inventory.addProduct(new Product(id, name, price, stock, min, max));
            }
            // Retrieve ProductParts and add them to the product
            for (Product product : inventory.getAllProducts()) {
                db.getStmt().execute("SELECT * FROM ProductParts WHERE productId = " + product.getId());
                resultSet = db.getStmt().getResultSet();
                while (resultSet.next()) {
                    int partId = resultSet.getInt("partId");
                    db.getStmt().execute("SELECT * FROM Parts WHERE id = " + partId);
                    // map the result set to a part
                    ResultSet partResultSet = db.getStmt().getResultSet();
                    partResultSet.next();
                    String name = partResultSet.getString("name");
                    String type = partResultSet.getString("type");
                    double price = partResultSet.getDouble("price");
                    int stock = partResultSet.getInt("stock");
                    int min = partResultSet.getInt("min");
                    int max = partResultSet.getInt("max");
                    int machineId;
                    String companyName;
                    if (type.equals("InHouse")) {
                        machineId = partResultSet.getInt("machineId");
                        Part part = new me.t8d.capstonebackend.model.InHouse(partId, name, price, stock, min, max, machineId);
                        inventory.addPart(part);
                        product.addAssociatedPart(part);
                    } else {
                        companyName = partResultSet.getString("companyName");
                        Part part = new me.t8d.capstonebackend.model.Outsourced(partId, name, price, stock, min, max, companyName);
                        inventory.addPart(part);
                        product.addAssociatedPart(part);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(e)
                    .build();
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(inventory.getAllProducts())
                .build();
    }
}

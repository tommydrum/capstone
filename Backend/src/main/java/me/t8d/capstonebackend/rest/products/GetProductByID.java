package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.Product;
import me.t8d.capstonebackend.model.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetProductByID implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    public GetProductByID() {
        db = new DB();
    }
    public GetProductByID(DB db) {
        this.db = db;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        System.out.println(stringObjectMap);
        System.out.println(context);
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("pathParameters").toString().matches(regex)) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        Inventory inventory = new Inventory();
        // URI is /products/{id}, retrieve the id
        Map<String,String> pathParameters = (Map<String,String>) stringObjectMap.get("pathParameters");
        int id = Integer.parseInt(pathParameters.get("id"));
        try {
            Statement statement = db.getStmt();
            statement.execute("SELECT * FROM Products WHERE id = " + id);
            // Get results and serialize them into the product model

            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                int min = resultSet.getInt("min");
                int max = resultSet.getInt("max");
                Product foundProduct = new Product(id, name, price, stock, min, max);
                // Retrieve ProductParts and add them to the product
                db.getStmt().execute("SELECT * FROM ProductParts WHERE productId = " + foundProduct.getId());
                resultSet = db.getStmt().getResultSet();
                while (resultSet.next()) {
                    int partId = resultSet.getInt("partId");
                    foundProduct.addAssociatedPart(inventory.lookupPart(partId));
                }
                List<Product> products = new ArrayList<>();
                products.add(foundProduct);
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(products)
                        .build();
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
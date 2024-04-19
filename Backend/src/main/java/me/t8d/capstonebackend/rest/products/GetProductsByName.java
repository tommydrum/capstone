package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.model.Inventory;
import me.t8d.capstonebackend.model.Part;
import me.t8d.capstonebackend.model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
/**
 * Serverless function to get all products from the database by name
 * @author Thomas Miller
 */
public class GetProductsByName implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    /**
     * Default constructor to setup the database
     */
    public GetProductsByName() {
        db = new DB();
    }
    /**
     * Constructor with a database, used for testing purposes
     * @param db the database to use, allows a mock db
     */
    public GetProductsByName(DB db) {
        this.db = db;
    }
    /**
     * Handle the request to get matching products from the database by name
     * @param stringObjectMap the request body containing the name of the product to get
     * @param context the context of the request
     * @return the response to the request, including the status code and body, containing matching products and their associated parts
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
        // URI is /products/byName/{}, retrieve the string
        Map<String,String> pathParameters = (Map<String,String>) stringObjectMap.get("pathParameters");
        String name = pathParameters.get("name");
        try {
            Statement statement = db.getStmt();
            statement.execute("SELECT * FROM Products WHERE name LIKE '%" + name + "%'");
            // Get results and serialize them into the product model

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                int id = resultSet.getInt("id");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                int min = resultSet.getInt("min");
                int max = resultSet.getInt("max");
                inventory.addProduct(new Product(id, productName, price, stock, min, max));
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
                    String partName = partResultSet.getString("name");
                    String type = partResultSet.getString("type");
                    double price = partResultSet.getDouble("price");
                    int stock = partResultSet.getInt("stock");
                    int min = partResultSet.getInt("min");
                    int max = partResultSet.getInt("max");
                    int machineId;
                    String companyName;
                    if (type.equals("InHouse")) {
                        machineId = partResultSet.getInt("machineId");
                        Part part = new me.t8d.capstonebackend.model.InHouse(partId, partName, price, stock, min, max, machineId);
                        inventory.addPart(part);
                        product.addAssociatedPart(part);
                    } else {
                        companyName = partResultSet.getString("companyName");
                        Part part = new me.t8d.capstonebackend.model.Outsourced(partId, partName, price, stock, min, max, companyName);
                        inventory.addPart(part);
                        product.addAssociatedPart(part);
                    }
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
                .setObjectBody(inventory.getAllProducts())
                .build();
    }
}
package me.t8d.capstonebackend.rest.parts;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;

import java.util.Map;

public class DeletePart implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private DB db;
    public DeletePart() {
        db = new DB();
    }
    public DeletePart(DB db) {
        this.db = db;
    }
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
//        DB db = new DB();
        // SQLi regex, not foolproof but because of the server-client nature, it will catch most scenarios.
        String regex = ".*[;\\\\].*";
        if (stringObjectMap.get("pathParameters").toString().matches(regex) ) {
            return ApiGatewayResponse.builder()
                    .setStatusCode(400)
                    .build();
        }
        try {
            Map<String,String> pathParameters = (Map<String,String>) stringObjectMap.get("pathParameters");
            int id = Integer.parseInt(pathParameters.get("id"));
            db.getStmt().execute("DELETE FROM Parts WHERE id = " + id + ";");
        } catch (Exception e) {
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

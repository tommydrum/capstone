package me.t8d.capstonebackend.rest.products;

import com.amazonaws.services.lambda.runtime.Context;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.DB;
import me.t8d.capstonebackend.rest.DBMockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateProductTest {
    private DB dbMock;
    private UpdateProduct updateProduct;
    @BeforeEach
    void setUp() throws SQLException {
        dbMock = DBMockProvider.getDBMock();
        updateProduct = new UpdateProduct(dbMock);
    }

    @Test
    void handleRequest() {
        Map<String, Object> stringObjectMapMock = mock(Map.class);
        Context contextMock = mock(Context.class);
        when(stringObjectMapMock.get("body")).thenReturn("{\"type\":\"Outsourced\",\"id\":1,\"name\":\"test4\",\"price\":5.0,\"stock\":1,\"min\":1,\"max\":4,\"companyName\":\"name\"}");
        ApiGatewayResponse response = updateProduct.handleRequest(stringObjectMapMock, contextMock);
        assertEquals(200, response.getStatusCode());
    }
}
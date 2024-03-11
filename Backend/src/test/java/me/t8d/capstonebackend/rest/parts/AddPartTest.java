package me.t8d.capstonebackend.rest.parts;
import com.amazonaws.services.lambda.runtime.Context;
import me.t8d.capstonebackend.ApiGatewayResponse;
import me.t8d.capstonebackend.rest.DBMockProvider;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.t8d.capstonebackend.DB;

import java.sql.SQLException;
import java.util.Map;

class AddPartTest {
    private DB dbMock;
    private AddPart addPart;
    @BeforeEach
    void setUp() throws SQLException {
        dbMock = DBMockProvider.getDBMock();
        addPart = new AddPart(dbMock);
    }

    @Test
    void handleRequest() {
        Map<String, Object> stringObjectMapMock = mock(Map.class);
        Context contextMock = mock(Context.class);
        when(stringObjectMapMock.get("body")).thenReturn("{\"type\":\"Outsourced\",\"id\":1,\"name\":\"test4\",\"price\":5.0,\"stock\":1,\"min\":1,\"max\":4,\"companyName\":\"name\"}");
        ApiGatewayResponse response = addPart.handleRequest(stringObjectMapMock, contextMock);
        assertEquals(200, response.getStatusCode());
    }
}
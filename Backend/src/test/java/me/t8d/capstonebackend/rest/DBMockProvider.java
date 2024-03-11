package me.t8d.capstonebackend.rest;

import org.mockito.Mockito;
import me.t8d.capstonebackend.DB;

import java.sql.SQLException;

public class DBMockProvider {
    public static DB getDBMock() throws SQLException {
        DB db = Mockito.mock(DB.class);
        // Mock the getStmt method
        Mockito.when(db.getStmt()).thenReturn(Mockito.mock(java.sql.Statement.class));
        // Mock the execute method
        Mockito.when(db.getStmt().execute(Mockito.anyString())).thenReturn(true);
        // Mock resultset for getStmt().executeQuery
        Mockito.when(db.getStmt().executeQuery(Mockito.anyString())).thenReturn(Mockito.mock(java.sql.ResultSet.class));
        // Mock for statement getResultSet
        Mockito.when(db.getStmt().getResultSet()).thenReturn(Mockito.mock(java.sql.ResultSet.class));
        return db;
    }
}

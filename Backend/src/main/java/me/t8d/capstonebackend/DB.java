package me.t8d.capstonebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.util.Map;
import java.sql.*;

/**
 * DB class for serverless methods to interact with the database
 * @author Thoams Miller
 */
public class DB {
    static private String username;
    static private String password;
    private static String url;
    Connection conn;
    Statement stmt;

    /**
     * Constructor for DB class
     * Retrieves the database credentials from AWS Secrets Manager, and connects to the database
     */
    public DB() {
        // Get credentials from AWS Secrets Manager
        if (username == null || password == null || url == null) {
            getSecret();
        }
        connect();
    }

    /**
     * Retrieves the database credentials from AWS Secrets Manager, and sets the username, password, and url
     * into the class variables
     */
    private void getSecret() {

        String secretName = "db";
        Region region = Region.of("ap-northeast-3");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> secretMap = objectMapper.readValue(secret, Map.class);
            username = secretMap.get("username");
            password = secretMap.get("password");
            String host = secretMap.get("host");
            System.out.println("host: " + host);
            url = "jdbc:mariadb://" + host + ":3306/capstone?useSSL=false&serverTimezone=UTC";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the database
     */
    private void connect() {
        try {
            this.conn = DriverManager.getConnection(url, username, password);
            this.stmt = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the connection to the database, connecting if not already connected
     * @return Statement object for interacting with the database
     * @throws SQLException If there is a problem connecting to the database
     */
    public Statement getStmt() throws SQLException {
        if (this.conn.isClosed()) {
            connect();
        }
        return this.stmt;
    }

    /**
     * Closes the connection to the database
     * @throws SQLException If there is a problem closing the connection
     */
    public void close() throws SQLException {
        this.conn.close();
    }
}

package me.t8d.capstonebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.util.Map;
import java.sql.*;


public class DB {
    static private String username;
    static private String password;
    private static String url;
    Connection conn;
    Statement stmt;
    public DB() {
        // Get credentials from AWS Secrets Manager
        if (username == null || password == null || url == null) {
            getSecret();
        }
        connect();
    }
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
    private void connect() {
        try {
            this.conn = DriverManager.getConnection(url, username, password);
            this.stmt = this.conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Statement getStmt() throws SQLException {
        if (this.conn.isClosed()) {
            connect();
        }
        return this.stmt;
    }
    public void close() throws SQLException {
        this.conn.close();
    }
}

/**
 * Class to act as the primary storage and interface for the storage of Parts and Products.
 */
package me.t8d.capstone.frontend.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * @author Thomas Miller
 */
public class Inventory {
    // HTTP Util Class
    public static class HttpUtils {

        public static String sendGetRequest(String endpointUrl) throws Exception {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                System.out.println(conn.getResponseMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Error response body:" + content.toString());
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        }

        public static String sendPostRequest(String endpointUrl, String jsonInputString) throws Exception {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                System.out.println(conn.getResponseMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Error response body:" + content.toString());                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        }
        public static String sendPutRequest(String endpointUrl, String jsonInputString) throws Exception {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                System.out.println(conn.getResponseMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Error response body:" + content.toString());                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        }
        public static String sendDeleteRequest(String endpointUrl) throws Exception {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                System.out.println(conn.getResponseMessage());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Error response body:" + content.toString());                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        }
    }
    final String apiUri = "https://u7w5pz757a.execute-api.ap-northeast-3.amazonaws.com/dev/";
    private static String token;
    public static void setToken(String token) {
        Inventory.token = token;
    }
//    private ObservableList<Part> allParts = FXCollections.observableArrayList();
//    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    // All Part Methods

    /**
     * @param newPart the part to add into the inventory
     */
    public void addPart(Part newPart) {
//        this.allParts.add(newPart);
        try {
            String response = HttpUtils.sendPostRequest(apiUri + "parts", newPart.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param partId the ID of the desired part to find within the inventory
     * @return The desired part
     * @throws NoSuchElementException if no part by the provided ID is found within the inventory
     */
    public Part lookupPart(int partId) {
        try {
            String response = HttpUtils.sendGetRequest(apiUri + "parts/" + partId);
            if (response.isEmpty()) return null;
            if (response.equals("[]")) return null;
            ObjectMapper mapper = new ObjectMapper();
            List<Part> partList = mapper.readValue(response, new TypeReference<List<Part>>(){});
//            Part part = mapper.readValue(response, new TypeReference<Part>(){});
            return partList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
//        for (Part part: this.allParts) {
//            if (part.getId() == partId) return part;
//        }
//        throw new NoSuchElementException();
    }

    /**
     * @param partName the name of the desired part to find within the inventory
     * @return the desired part
     * @throws NoSuchElementException if no part by the provided name is found within the inventory
     */
    public ObservableList<Part> lookupPart(String partName) {
        // uri is /parts/byName/{name}
        try {
            String response = HttpUtils.sendGetRequest(apiUri + "parts/byName/" + partName);
            if (response.isEmpty()) return FXCollections.observableArrayList();
            if (response.equals("[]")) return FXCollections.observableArrayList();
            // split array into parts
            ObjectMapper mapper = new ObjectMapper();
            List<Part> partList = mapper.readValue(response, new TypeReference<List<Part>>(){});
            return FXCollections.observableArrayList(partList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
//        ObservableList<Part> foundParts = FXCollections.observableArrayList();
//        for (Part part: this.allParts) {
//            if (part.getName().equals(partName)) foundParts.add(part);
//        }
//        if (foundParts.isEmpty()) throw new NoSuchElementException();
//        else return foundParts;
    }

    /**
     * @param index the index within the inventory that the desired part to update is located at
     * @param selectedPart the updated part to replace the existing entry with
     */
    public void updatePart(int index, Part selectedPart) {
        // uri is /parts/{id} PUT
        try {
            String response = HttpUtils.sendPutRequest(apiUri + "parts/" + selectedPart.getId(), selectedPart.toJson());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Product product: allProducts) {
//            Part foundPart = null;
//            for (Part part: product.getAllAssociatedParts()) {
//                if (part.getId() == selectedPart.getId()) {
//                    foundPart = part;
//                    break;
//                }
//            }
//            if (foundPart != null) {
//                product.deleteAssociatedPart(foundPart);
//                product.addAssociatedPart(selectedPart);
//            }
//        }
//        this.allParts.set(index, selectedPart);
    }

    /**
     * @param selectedPart the desired part to remove from the inventory
     * @return True if the part was removed, False if the part was not found within the inventory
     */
    public boolean deletePart(Part selectedPart) {
        try {
            String response = HttpUtils.sendDeleteRequest(apiUri + "parts/" + selectedPart.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        for (Product product: allProducts) {
//            Part tempPart = null;
//            for (Part part: product.getAllAssociatedParts()) {
//                if (part == selectedPart) {
//                    tempPart = part;
//                }
//            }
//            if (tempPart != null)
//                product.deleteAssociatedPart(tempPart);
//        }
//        return this.allParts.remove(selectedPart);
        return true;
    }

    /**
     * @return the list of all parts within the Inventory
     */
    public ObservableList<Part> getAllParts() {
        try {
            String response = HttpUtils.sendGetRequest(apiUri + "parts");
            ObjectMapper mapper = new ObjectMapper();
            List<Part> partList = mapper.readValue(response, new TypeReference<List<Part>>(){});
            return FXCollections.observableArrayList(partList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
//        return this.allParts;
    }
    // All Product Methods

    /**
     * @param newProduct the product to add into the inventory
     */
    public void addProduct(Product newProduct) {
        try {
            String response = HttpUtils.sendPostRequest(apiUri + "products", newProduct.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.allProducts.add(newProduct);
    }

    /**
     * @param productId the ID of the desired product to find within the inventory
     * @return the desired product
     * @throws NoSuchElementException if no product by the provided ID is found within the inventory
     */
    public Product lookupProduct(int productId) {
        try {
            String response = HttpUtils.sendGetRequest(apiUri + "products/" + productId);
            if (response.isEmpty()) return null;
            if (response.equals("[]")) return null;
            ObjectMapper mapper = new ObjectMapper();

            JsonNode top = mapper.readTree(response);
            ArrayList<Product> productList = new ArrayList<>();
            for (JsonNode node: top) { // I actually know this is only ever going to return a single product
                // map this node as a product, not including the associated parts
                // Strip the associated parts from the node
                JsonNode deepCopy = node.deepCopy();
                JsonNode allAssociatedParts = deepCopy.get("allAssociatedParts");
                ((com.fasterxml.jackson.databind.node.ObjectNode) deepCopy).remove("allAssociatedParts");
                Product product = mapper.readValue(deepCopy.toString(), Product.class);
                if (allAssociatedParts == null || allAssociatedParts.isNull() || allAssociatedParts.isEmpty()) {
                    productList.add(product);
                    continue;
                }
                for (JsonNode partNode : allAssociatedParts) {
                    // if part contains a machineId, it is an InHouse part
                    if (partNode.has("machineId")) {
                        Part part = mapper.readValue(partNode.toString(), InHouse.class);
                        product.addAssociatedPart(part);
                    } else {
                        Part part = mapper.readValue(partNode.toString(), Outsourced.class);
                        product.addAssociatedPart(part);
                    }
                }
                productList.add(product);
            }
//            List<Product> productList = mapper.readValue(response, new TypeReference<List<Product>>(){});
            return productList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
    }

    /**
     * @param productName the name of the desired product to find within the inventory
     * @return the desired part
     * @throws NoSuchElementException if no product by the provided name is found within the inventory
     */
    public ObservableList<Product> lookupProduct(String productName) {
        try {
            String response = HttpUtils.sendGetRequest(apiUri + "products/byName/" + productName);
            if (response.isEmpty()) return FXCollections.observableArrayList();
            if (response.equals("[]")) return FXCollections.observableArrayList();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode top = mapper.readTree(response);
            ArrayList<Product> productList = new ArrayList<>();
            for (JsonNode node: top) {
                // map this node as a product, not including the associated parts
                // Strip the associated parts from the node
                JsonNode deepCopy = node.deepCopy();
                JsonNode allAssociatedParts = deepCopy.get("allAssociatedParts");
                ((com.fasterxml.jackson.databind.node.ObjectNode) deepCopy).remove("allAssociatedParts");
                Product product = mapper.readValue(deepCopy.toString(), Product.class);
                if (allAssociatedParts == null || allAssociatedParts.isNull() || allAssociatedParts.isEmpty()) {
                    productList.add(product);
                    continue;
                }
                for (JsonNode partNode : allAssociatedParts) {
                    // if part contains a machineId, it is an InHouse part
                    if (partNode.has("machineId")) {
                        Part part = mapper.readValue(partNode.toString(), InHouse.class);
                        product.addAssociatedPart(part);
                    } else {
                        Part part = mapper.readValue(partNode.toString(), Outsourced.class);
                        product.addAssociatedPart(part);
                    }
                }
                productList.add(product);
            }
//            List<Product> productList = mapper.readValue(response, new TypeReference<List<Product>>(){});
            return FXCollections.observableArrayList(productList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
//        ObservableList<Product> foundProducts = FXCollections.observableArrayList();
//        for (Product product: this.allProducts) {
//            if (product.getName().equals(productName)) foundProducts.add(product);
//        }
//        if (foundProducts.isEmpty()) throw new NoSuchElementException();
//        else return foundProducts;
    }

    /**
     * @param index the index within the inventory that the desired product to update is located at
     * @param selectedProduct the updated product to replace the existing entry with
     */
    public void updateProduct(int index, Product selectedProduct) {
        try {
            String response = HttpUtils.sendPutRequest(apiUri + "products/" + selectedProduct.getId(), selectedProduct.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.allProducts.set(index, selectedProduct);
    }

    /**
     * @param selectedProduct the desired product to remove from the inventory
     * @return True if the product was removed, False if the product was not found within the inventory
     */
    public boolean deleteProduct(Product selectedProduct) {
        try {
            String response = HttpUtils.sendDeleteRequest(apiUri + "products/" + selectedProduct.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        return this.allProducts.remove(selectedProduct);
    }

    /**
     * @return the list of all products within the Inventory
     */
    public ObservableList<Product> getAllProducts() {
        try {
            ArrayList<Product> productList = new ArrayList<>();
            String response = HttpUtils.sendGetRequest(apiUri + "products");
            ObjectMapper mapper = new ObjectMapper();
            // check if empty
            if (response.equals("[]")) return FXCollections.observableArrayList();
//            List<Product> productList = mapper.readValue(response, new TypeReference<List<Product>>(){});
            JsonNode top = mapper.readTree(response);
            // top is an array
            for (JsonNode node: top) {
                // map this node as a product, not including the associated parts
                // Strip the associated parts from the node
                JsonNode deepCopy = node.deepCopy();
                JsonNode allAssociatedParts = deepCopy.get("allAssociatedParts");
                ((com.fasterxml.jackson.databind.node.ObjectNode) deepCopy).remove("allAssociatedParts");
                Product product = mapper.readValue(deepCopy.toString(), Product.class);
                if (allAssociatedParts == null || allAssociatedParts.isNull() || allAssociatedParts.isEmpty()) {
                    productList.add(product);
                    continue;
                }
                for (JsonNode partNode: allAssociatedParts) {
                    // if part contains a machineId, it is an InHouse part
                    if (partNode.has("machineId")) {
                        Part part = mapper.readValue(partNode.toString(), InHouse.class);
                        product.addAssociatedPart(part);
                    } else {
                        Part part = mapper.readValue(partNode.toString(), Outsourced.class);
                        product.addAssociatedPart(part);
                    }
                }
                productList.add(product);
            }
            return FXCollections.observableArrayList(productList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException();
        }
    }
}

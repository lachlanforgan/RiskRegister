import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Sample client to demonstrate API usage
 */
public class APIClient {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    
    /**
     * Get all risks from the API
     */
    public static String getAllRisks() throws IOException {
        URL url = new URL(BASE_URL + "/risks");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        return getResponse(conn);
    }
    
    /**
     * Get a specific risk by ID and project ID
     */
    public static String getRisk(long riskID, long projectID) throws IOException {
        URL url = new URL(BASE_URL + "/risks?id=" + riskID + "&pid=" + projectID);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        return getResponse(conn);
    }
    
    /**
     * Create a new risk
     */
    public static String createRisk(JSONObject riskData) throws IOException {
        URL url = new URL(BASE_URL + "/risks");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // Write the JSON data to the request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = riskData.toJSONString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        return getResponse(conn);
    }
    
    /**
     * Update an existing risk
     */
    public static String updateRisk(JSONObject riskData) throws IOException {
        URL url = new URL(BASE_URL + "/risks");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // Write the JSON data to the request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = riskData.toJSONString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        return getResponse(conn);
    }
    
    /**
     * Check API health
     */
    public static String checkHealth() throws IOException {
        URL url = new URL(BASE_URL + "/health");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        return getResponse(conn);
    }
    
    /**
     * Delete a risk by ID and project ID
     */
    public static String deleteRisk(long riskID, long projectID) throws IOException {
        URL url = new URL(BASE_URL + "/risks/delete?id=" + riskID + "&pid=" + projectID);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        
        return getResponse(conn);
    }
    
    /**
     * Read the response from an HTTP connection
     */
    private static String getResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), "utf-8"))) {
            
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        return response.toString();
    }
    
    /**
     * Demo method to show API usage
     */
    public static void main(String[] args) {
        try {
            // Check API health
            System.out.println("Checking API health...");
            String healthResponse = checkHealth();
            System.out.println("Health Response: " + healthResponse);
            
            // Create a sample risk
            JSONObject risk = new JSONObject();
            risk.put("riskID", 1001);
            risk.put("projectID", 5001);
            risk.put("title", "Security Vulnerability");
            risk.put("description", "Potential security vulnerability in authentication module");
            risk.put("likelihood", "MEDIUM");
            risk.put("impact", "HIGH");
            risk.put("mitigationPlan", "Implement additional security measures");
            risk.put("owner", "John Doe");
            risk.put("status", "OPEN");
            
            System.out.println("\nCreating a new risk...");
            String createResponse = createRisk(risk);
            System.out.println("Response: " + createResponse);
            
            System.out.println("\nGetting all risks...");
            String allRisks = getAllRisks();
            System.out.println("Response: " + allRisks);
            
            System.out.println("\nGetting specific risk...");
            String specificRisk = getRisk(1001, 5001);
            System.out.println("Response: " + specificRisk);
            
            // Update the risk
            JSONObject updatedRisk = new JSONObject();
            updatedRisk.put("riskID", 1001);
            updatedRisk.put("projectID", 5001);
            updatedRisk.put("title", "Updated Security Vulnerability");
            updatedRisk.put("impact", "CRITICAL");
            updatedRisk.put("likelihood", "HIGH");
            
            System.out.println("\nUpdating the risk...");
            String updateResponse = updateRisk(updatedRisk);
            System.out.println("Response: " + updateResponse);
            
            // Verify the update
            System.out.println("\nGetting updated risk...");
            String updatedRiskResponse = getRisk(1001, 5001);
            System.out.println("Response: " + updatedRiskResponse);
            
            System.out.println("\nDeleting the risk...");
            String deleteResponse = deleteRisk(1001, 5001);
            System.out.println("Response: " + deleteResponse);
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
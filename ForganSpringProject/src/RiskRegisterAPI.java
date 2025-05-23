import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * REST API for the Risk Register system
 * Provides endpoints for creating, reading, updating, and deleting risks
 */
public class RiskRegisterAPI {
    private int port;
    private RiskService riskService;

    public RiskRegisterAPI() {
        this.port = ConfigLoader.getIntProperty("api.port");
        this.riskService = new RiskService();
    }

    /**
     * Start the API server
     */
    public void startServer() throws IOException {
        // Create server with a reasonable backlog
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 100);
        
        // Register endpoints
        server.createContext("/api/risks", new RisksHandler());
        server.createContext("/api/risks/delete", new DeleteRiskHandler());
        server.createContext("/api/health", new HealthCheckHandler());
        
        // Create a thread pool with fixed number of threads
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Risk Register API server started on port " + port);
        System.out.println("Available endpoints:");
        System.out.println("  GET    /api/health          - Health check");
        System.out.println("  GET    /api/risks           - Get all risks");
        System.out.println("  GET    /api/risks?id=X&pid=Y - Get a specific risk");
        System.out.println("  POST   /api/risks           - Create a new risk");
        System.out.println("  PUT    /api/risks           - Update a risk");
        System.out.println("  DELETE /api/risks/delete?id=X&pid=Y - Delete a risk");
    }
    
    /**
     * Simple health check handler for monitoring
     */
    private class HealthCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            JSONObject response = new JSONObject();
            response.put("status", "UP");
            response.put("timestamp", System.currentTimeMillis());
            
            try {
                // Check database connection
                Connection conn = ConnectionManager.getConnection();
                conn.close();
                response.put("database", "Connected");
            } catch (Exception e) {
                response.put("database", "Error: " + e.getMessage());
            }
            
            sendResponse(exchange, 200, response.toJSONString());
        }
    }

    /**
     * Handler for the /api/risks endpoint
     */
    private class RisksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            try {
                switch (method) {
                    case "GET":
                        handleGetRisks(exchange);
                        break;
                    case "POST":
                        handleCreateRisk(exchange);
                        break;
                    case "PUT":
                        handleUpdateRisk(exchange);
                        break;
                    case "OPTIONS":
                        // Handle CORS preflight request
                        handleCorsPreflightRequest(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, "Method Not Allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Internal Server Error");
                errorResponse.put("message", e.getMessage());
                sendResponse(exchange, 500, errorResponse.toJSONString());
            }
        }
        
        /**
         * Handle CORS preflight requests
         */
        private void handleCorsPreflightRequest(HttpExchange exchange) throws IOException {
            // Set CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.getResponseHeaders().add("Access-Control-Max-Age", "86400");
            
            // Respond with 200 OK
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        }
        
        /**
         * Handle GET requests to retrieve risks
         */
        private void handleGetRisks(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            
            // If query parameters are provided, get a specific risk
            if (query != null && !query.isEmpty()) {
                // Parse query parameters
                long riskID = -1;
                long projectID = -1;
                
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        
                        if (key.equals("id")) {
                            riskID = Long.parseLong(value);
                        } else if (key.equals("pid")) {
                            projectID = Long.parseLong(value);
                        }
                    }
                }
                
                // If both IDs are provided, get the specific risk
                if (riskID != -1 && projectID != -1) {
                    RiskEntity risk = riskService.getRisk(riskID, projectID);
                    
                    if (risk != null) {
                        JSONObject riskJson = new JSONObject();
                        riskJson.put("riskID", risk.getRiskID());
                        riskJson.put("projectID", risk.getProjectID());
                        riskJson.put("title", risk.getTitle());
                        riskJson.put("description", risk.getDescription());
                        riskJson.put("likelihood", risk.generateLikelihood());
                        riskJson.put("impact", risk.generateImpact());
                        riskJson.put("mitigationPlan", risk.getMitigationPlan());
                        riskJson.put("owner", risk.getOwner());
                        riskJson.put("status", risk.getStatus());
                        
                        sendResponse(exchange, 200, riskJson.toJSONString());
                    } else {
                        sendResponse(exchange, 404, "Risk not found");
                    }
                } else {
                    sendResponse(exchange, 400, "Missing required parameters: id and pid");
                }
            } 
            // Otherwise, get all risks
            else {
                // Get all risks from the database
                try {
                    Connection conn = ConnectionManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM risk");
                    ResultSet rs = stmt.executeQuery();
                    
                    JSONArray risksArray = new JSONArray();
                    
                    while (rs.next()) {
                        JSONObject riskJson = new JSONObject();
                        riskJson.put("riskID", rs.getInt("riskID"));
                        riskJson.put("projectID", rs.getInt("projectID"));
                        riskJson.put("title", rs.getString("title"));
                        riskJson.put("description", rs.getString("description"));
                        riskJson.put("likelihood", rs.getString("likelihood"));
                        riskJson.put("impact", rs.getString("impact"));
                        riskJson.put("mitigationPlan", rs.getString("mitigation_plan"));
                        riskJson.put("owner", rs.getString("owner"));
                        riskJson.put("status", rs.getString("status"));
                        
                        risksArray.add(riskJson);
                    }
                    
                    conn.close();
                    
                    sendResponse(exchange, 200, risksArray.toJSONString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500, "Database error: " + e.getMessage());
                }
            }
        }
        
        /**
         * Handle POST requests to create a new risk
         */
        private void handleCreateRisk(HttpExchange exchange) throws IOException {
            // Read the request body
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
            
            try {
                // Parse the JSON request
                JSONParser parser = new JSONParser();
                JSONObject riskJson = (JSONObject) parser.parse(requestBody);
                
                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (!riskJson.containsKey("riskID")) missingFields.add("riskID");
                if (!riskJson.containsKey("projectID")) missingFields.add("projectID");
                if (!riskJson.containsKey("title")) missingFields.add("title");
                if (!riskJson.containsKey("description")) missingFields.add("description");
                
                if (!missingFields.isEmpty()) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Missing required fields: " + String.join(", ", missingFields));
                    sendResponse(exchange, 400, response.toJSONString());
                    return;
                }
                
                // Extract risk data
                int riskID = ((Number) riskJson.get("riskID")).intValue();
                int projectID = ((Number) riskJson.get("projectID")).intValue();
                String title = (String) riskJson.get("title");
                String description = (String) riskJson.get("description");
                String mitigationPlan = (String) riskJson.getOrDefault("mitigationPlan", "");
                String owner = (String) riskJson.getOrDefault("owner", "");
                String status = (String) riskJson.getOrDefault("status", "OPEN");
                
                // Validate string fields
                if (title.trim().isEmpty()) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Title cannot be empty");
                    sendResponse(exchange, 400, response.toJSONString());
                    return;
                }
                
                // Parse likelihood
                RiskEntity.Likelihood likelihood;
                String likelihoodStr = (String) riskJson.getOrDefault("likelihood", "LOW");
                try {
                    switch (likelihoodStr.toUpperCase()) {
                        case "HIGH":
                            likelihood = RiskEntity.Likelihood.HIGH;
                            break;
                        case "MEDIUM":
                            likelihood = RiskEntity.Likelihood.MEDIUM;
                            break;
                        case "LOW":
                            likelihood = RiskEntity.Likelihood.LOW;
                            break;
                        default:
                            JSONObject response = new JSONObject();
                            response.put("success", false);
                            response.put("message", "Invalid likelihood value: " + likelihoodStr);
                            sendResponse(exchange, 400, response.toJSONString());
                            return;
                    }
                } catch (Exception e) {
                    likelihood = RiskEntity.Likelihood.LOW;
                }
                
                // Parse impact
                RiskEntity.Impact impact;
                String impactStr = (String) riskJson.getOrDefault("impact", "LOW");
                try {
                    switch (impactStr.toUpperCase()) {
                        case "CRITICAL":
                            impact = RiskEntity.Impact.CRITICAL;
                            break;
                        case "HIGH":
                            impact = RiskEntity.Impact.HIGH;
                            break;
                        case "MEDIUM":
                            impact = RiskEntity.Impact.MEDIUM;
                            break;
                        case "LOW":
                            impact = RiskEntity.Impact.LOW;
                            break;
                        default:
                            JSONObject response = new JSONObject();
                            response.put("success", false);
                            response.put("message", "Invalid impact value: " + impactStr);
                            sendResponse(exchange, 400, response.toJSONString());
                            return;
                    }
                } catch (Exception e) {
                    impact = RiskEntity.Impact.LOW;
                }
                
                // Create the risk
                boolean success = riskService.createRisk(riskID, projectID, title, description, 
                        mitigationPlan, owner, status, likelihood, impact);
                
                if (success) {
                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("message", "Risk created successfully");
                    response.put("riskID", riskID);
                    response.put("projectID", projectID);
                    sendResponse(exchange, 201, response.toJSONString());
                } else {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Failed to create risk. Risk ID may already exist.");
                    sendResponse(exchange, 400, response.toJSONString());
                }
                
            } catch (ParseException e) {
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "Invalid JSON: " + e.getMessage());
                sendResponse(exchange, 400, response.toJSONString());
            } catch (ClassCastException e) {
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "Invalid data type: " + e.getMessage());
                sendResponse(exchange, 400, response.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "Error creating risk: " + e.getMessage());
                sendResponse(exchange, 500, response.toJSONString());
            }
        }
        
        /**
         * Handle PUT requests to update an existing risk
         */
        private void handleUpdateRisk(HttpExchange exchange) throws IOException {
            // Read the request body
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
            
            try {
                // Parse the JSON request
                JSONParser parser = new JSONParser();
                JSONObject riskJson = (JSONObject) parser.parse(requestBody);
                
                // Validate required fields
                if (!riskJson.containsKey("riskID") || !riskJson.containsKey("projectID")) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Missing required fields: riskID and projectID are required");
                    sendResponse(exchange, 400, response.toJSONString());
                    return;
                }
                
                // Get the risk ID and project ID
                int riskID = ((Number) riskJson.get("riskID")).intValue();
                int projectID = ((Number) riskJson.get("projectID")).intValue();
                
                // Check if the risk exists
                RiskEntity existingRisk = riskService.getRisk(riskID, projectID);
                if (existingRisk == null) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Risk not found");
                    sendResponse(exchange, 404, response.toJSONString());
                    return;
                }
                
                // Update the risk with the new values
                if (riskJson.containsKey("title")) {
                    existingRisk.setTitle((String) riskJson.get("title"));
                }
                
                if (riskJson.containsKey("description")) {
                    existingRisk.setDescription((String) riskJson.get("description"));
                }
                
                if (riskJson.containsKey("mitigationPlan")) {
                    existingRisk.setMitigationPlan((String) riskJson.get("mitigationPlan"));
                }
                
                if (riskJson.containsKey("owner")) {
                    existingRisk.setOwner((String) riskJson.get("owner"));
                }
                
                if (riskJson.containsKey("status")) {
                    existingRisk.setStatus((String) riskJson.get("status"));
                }
                
                // Update likelihood if provided
                if (riskJson.containsKey("likelihood")) {
                    String likelihoodStr = (String) riskJson.get("likelihood");
                    try {
                        switch (likelihoodStr.toUpperCase()) {
                            case "HIGH":
                                existingRisk.setLikelihood(RiskEntity.Likelihood.HIGH);
                                break;
                            case "MEDIUM":
                                existingRisk.setLikelihood(RiskEntity.Likelihood.MEDIUM);
                                break;
                            case "LOW":
                                existingRisk.setLikelihood(RiskEntity.Likelihood.LOW);
                                break;
                            default:
                                JSONObject response = new JSONObject();
                                response.put("success", false);
                                response.put("message", "Invalid likelihood value: " + likelihoodStr);
                                sendResponse(exchange, 400, response.toJSONString());
                                return;
                        }
                    } catch (Exception e) {
                        // Keep existing value if error
                    }
                }
                
                // Update impact if provided
                if (riskJson.containsKey("impact")) {
                    String impactStr = (String) riskJson.get("impact");
                    try {
                        switch (impactStr.toUpperCase()) {
                            case "CRITICAL":
                                existingRisk.setImpact(RiskEntity.Impact.CRITICAL);
                                break;
                            case "HIGH":
                                existingRisk.setImpact(RiskEntity.Impact.HIGH);
                                break;
                            case "MEDIUM":
                                existingRisk.setImpact(RiskEntity.Impact.MEDIUM);
                                break;
                            case "LOW":
                                existingRisk.setImpact(RiskEntity.Impact.LOW);
                                break;
                            default:
                                JSONObject response = new JSONObject();
                                response.put("success", false);
                                response.put("message", "Invalid impact value: " + impactStr);
                                sendResponse(exchange, 400, response.toJSONString());
                                return;
                        }
                    } catch (Exception e) {
                        // Keep existing value if error
                    }
                }
                
                // Delete the existing risk and create a new one with the updated values
                riskService.deleteRisk(riskID, projectID);
                boolean success = riskService.createRisk(existingRisk);
                
                if (success) {
                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("message", "Risk updated successfully");
                    sendResponse(exchange, 200, response.toJSONString());
                } else {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "Failed to update risk");
                    sendResponse(exchange, 500, response.toJSONString());
                }
                
            } catch (ParseException e) {
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "Invalid JSON: " + e.getMessage());
                sendResponse(exchange, 400, response.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "Error updating risk: " + e.getMessage());
                sendResponse(exchange, 500, response.toJSONString());
            }
        }
    }
    
    /**
     * Handler for the /api/risks/delete endpoint
     */
    private class DeleteRiskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            if (!method.equals("DELETE")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            try {
                String query = exchange.getRequestURI().getQuery();
                
                if (query == null || query.isEmpty()) {
                    sendResponse(exchange, 400, "Missing required parameters: id and pid");
                    return;
                }
                
                // Parse query parameters
                long riskID = -1;
                long projectID = -1;
                
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        
                        if (key.equals("id")) {
                            riskID = Long.parseLong(value);
                        } else if (key.equals("pid")) {
                            projectID = Long.parseLong(value);
                        }
                    }
                }
                
                // If both IDs are provided, delete the risk
                if (riskID != -1 && projectID != -1) {
                    boolean success = riskService.deleteRisk(riskID, projectID);
                    
                    JSONObject response = new JSONObject();
                    if (success) {
                        response.put("success", true);
                        response.put("message", "Risk deleted successfully");
                        sendResponse(exchange, 200, response.toJSONString());
                    } else {
                        response.put("success", false);
                        response.put("message", "Failed to delete risk");
                        sendResponse(exchange, 404, response.toJSONString());
                    }
                } else {
                    sendResponse(exchange, 400, "Missing required parameters: id and pid");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Error deleting risk: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send an HTTP response
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    /**
     * Main method to start the API server
     */
    public static void main(String[] args) {
        try {
            RiskRegisterAPI api = new RiskRegisterAPI();
            api.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to start API server: " + e.getMessage());
        }
    }
}
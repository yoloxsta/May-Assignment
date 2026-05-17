package app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // Initialize database connection
        Database.init();

        // Create server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // API endpoints
        server.createContext("/api/tasks", new TaskHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Backend server running at http://localhost:8080");
        System.out.println("API endpoint: http://localhost:8080/api/tasks");
    }

    // Handles task API requests
    static class TaskHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            // CORS headers - allow frontend to connect
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            String method = exchange.getRequestMethod();

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            String response = "";
            int statusCode = 200;

            try {
                switch (method) {
                    case "GET":
                        response = getTasks();
                        break;
                    case "POST":
                        response = addTask(exchange);
                        break;
                    case "PUT":
                        response = toggleTask(exchange);
                        break;
                    case "DELETE":
                        response = deleteTask(exchange);
                        break;
                    default:
                        statusCode = 405;
                        response = "{\"error\":\"Method not allowed\"}";
                }
            } catch (Exception e) {
                statusCode = 500;
                response = "{\"error\":\"" + e.getMessage() + "\"}";
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String getTasks() {
            List<Task> tasks = Database.getAllTasks();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < tasks.size(); i++) {
                if (i > 0) json.append(",");
                json.append(tasks.get(i).toJson());
            }
            json.append("]");
            return json.toString();
        }

        private String addTask(HttpExchange exchange) throws IOException {
            String body = readBody(exchange);
            String title = parseJsonValue(body, "title");
            
            if (title == null || title.isEmpty()) {
                return "{\"error\":\"Title is required\"}";
            }

            Task task = Database.addTask(title);
            return task != null ? task.toJson() : "{\"error\":\"Failed to add task\"}";
        }

        private String toggleTask(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int id = parseQueryId(query);
            
            if (id <= 0) {
                return "{\"error\":\"Invalid task ID\"}";
            }

            boolean success = Database.toggleTask(id);
            return success ? "{\"success\":true}" : "{\"error\":\"Task not found\"}";
        }

        private String deleteTask(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            int id = parseQueryId(query);
            
            if (id <= 0) {
                return "{\"error\":\"Invalid task ID\"}";
            }

            boolean success = Database.deleteTask(id);
            return success ? "{\"success\":true}" : "{\"error\":\"Task not found\"}";
        }

        private String readBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }

        private int parseQueryId(String query) {
            if (query == null) return 0;
            String[] params = query.split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                if (kv.length == 2 && kv[0].equals("id")) {
                    try {
                        return Integer.parseInt(kv[1]);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }
            return 0;
        }

        private String parseJsonValue(String json, String key) {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
            return matcher.find() ? matcher.group(1) : null;
        }
    }
}

package com.example.demo.utils;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebServer {
    private static final int[] PORTS = {8080, 8081, 8082, 8083, 8084};
    private static HttpServer server;
    private static int currentPort;

    public static void start() {
        for (int port : PORTS) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                currentPort = port;
                server.createContext("/captcha", exchange -> {
                    if ("GET".equals(exchange.getRequestMethod())) {
                        // Read the HTML file
                        InputStream is = WebServer.class.getResourceAsStream("/com/example/demo/captcha.html");
                        String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        
                        // Send the response
                        exchange.getResponseHeaders().set("Content-Type", "text/html");
                        exchange.sendResponseHeaders(200, html.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(html.getBytes());
                        }
                    }
                });
                server.setExecutor(null);
                server.start();
                System.out.println("Web server started on port " + port);
                return;
            } catch (IOException e) {
                System.err.println("Port " + port + " is in use, trying next port...");
            }
        }
        throw new RuntimeException("Could not start web server on any available port");
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Web server stopped");
        }
    }

    public static String getCaptchaUrl() {
        return "http://127.0.0.1:" + currentPort + "/captcha";
    }
} 
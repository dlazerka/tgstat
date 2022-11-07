package com.victoriametrics.tgstat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    private static String makeApiCall(
            String token,
            String apiCall
    ) throws IOException {
        System.out.println("Sending: " + apiCall);

        URL url = new URL("https://api.telegram.org/bot" + token + apiCall);
        System.out.println("Sending: " + url);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);


        int status = con.getResponseCode();

        if (status != 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            System.err.println(reader.lines().toList());
            throw new IOException("Status is not 200, but is " + status);
        }

        byte[] responseBytes = con.getInputStream().readAllBytes();
        String response = new String(responseBytes);

        con.disconnect();

        System.out.println("Response: " + response);

        return response;
    }

    private static String readToken() throws IOException {
        Path path = Paths.get("secrets/tg.token.txt");

        byte[] bytes = Files.readAllBytes(path);
        String token = new String(bytes).trim();
        System.out.println(token);
        return token;
    }

    public static void main(String[] args) throws IOException {
        String token = readToken();
        // makeApiCall(token, "/getMe");
        // makeApiCall(token, "/getUpdates");
        // makeApiCall(token, "/sendMessage?chat_id=388268832&text=yo");
        // makeApiCall(token, "/setWebhook?url=https%3A%2F%2Feivr.eu.ngrok.io");

        int port = 8765;
        System.out.println("Listening on: " + port);

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);

        server.createContext("/", new MyHttpHandler());
        Executor threadPoolExecutor = Executors.newFixedThreadPool(2);
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

    static class MyHttpHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());

            int c = isr.read();
            while (c != -1) {
                System.out.print(((char) c));
                c = isr.read();
            }
            System.out.println();

            // List<String> lines = reader.lines().toList();
            // System.out.println(lines);

            String response = "This is the response " + System.currentTimeMillis();
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}

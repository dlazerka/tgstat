package com.victoriametrics.tgstat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {

        String token = readToken();

        makeApiCall(token, "/getMe");

        makeApiCall(token, "/getUpdates");
    }

    private static String makeApiCall(String token, String apiCall) throws IOException {
        System.out.println("Sending: " + apiCall);

        URL url = new URL("https://api.telegram.org/bot" + token + apiCall);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        int status = con.getResponseCode();

        if (status != 200) {
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
}

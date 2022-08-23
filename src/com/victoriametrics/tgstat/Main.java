package com.victoriametrics.tgstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        // String token = readToken();
        // makeApiCall(token, "/getMe");
        // makeApiCall(token, "/getUpdates");
        // makeApiCall(token, "/sendMessage?chat_id=388268832&text=yo");

        try (ServerSocket server = new ServerSocket(8888)) {

            Socket conn = server.accept();

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            System.out.println(bufferedReader.readLine());

            conn.close();
        }
    }
}

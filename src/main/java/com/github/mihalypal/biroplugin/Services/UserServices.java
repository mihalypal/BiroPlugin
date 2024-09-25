package com.github.mihalypal.biroplugin.Services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServices {

    /*private static String authToken;

    public static boolean authenticateUser(String username, String password) {
        try {
            // Biro login API endpoint
            URL url = new URL("https://biro3.inf.u-szeged.hu/api/v1/auth/login/student");
            //URL url = new URL("http://127.0.0.1:5000/authenticate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response headers
            Map<String, List<String>> headers = conn.getHeaderFields();

            // Extract the refresh token from the Set-Cookie header
            List<String> cookies = headers.get("Set-Cookie");
            if (cookies != null) {
                for (String cookie : cookies) {
                    // Use regex to extract the token
                    Pattern pattern = Pattern.compile("refresh-token=([^;]+)");
                    Matcher matcher = pattern.matcher(cookie);
                    if (matcher.find()) {
                        String token = matcher.group(1);
                        authToken = token;
                        //System.out.println("Extracted token: " + token);
                    }
                }
            } else {
//                System.out.println("No Set-Cookie header found");
            }

            *//*System.out.println("Response message: " + conn.getResponseMessage());
            System.out.println("Response content type: " + conn.getContentType());
            System.out.println("Response header fields: " + conn.getHeaderFields());
            System.out.println("Response token: " + authToken);*//*

            // Get the response code
            int responseCode = conn.getResponseCode();
            // If the response code is 200 OK, then the login was successful
            // and we can extract the accessToken from the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Parse the JSON response to extract the accessToken
                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                    String accessToken = jsonResponse.get("accessToken").getAsString();
                    authToken = accessToken; // Store the accessToken
                    System.out.println("Response: " + jsonResponse);
                    System.out.println("Extracted accessToken: " + accessToken);
                }
                return true;
            } else {
                System.out.println("POST request failed. Response Code: " + responseCode);
                return false;
            }
            //return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

}

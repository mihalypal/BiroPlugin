package com.github.mihalypal.biroplugin.Services;

import com.github.mihalypal.biroplugin.config.PluginConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class ReportService {

    public static String fetchReport(int evaluationID) {
        String url = PluginConstants.BASE_URL + "/api/v1/students/evaluations/" + evaluationID + "/reports";
        StringBuilder report = new StringBuilder();

        try {
            UserServices.refreshToken(UserServices.getRefreshToken());
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        report.append(line);
                    }
                }
                // debug lines
                System.out.println("Response Code: " + responseCode);
                System.out.println("Raw Response: " + report.toString());

                // Parse the report string to extract the report content
                JsonArray jsonArray = JsonParser.parseString(report.toString()).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    if ("riport.txt".equals(jsonObject.get("filename").getAsString())) {
                        String Base64Content = jsonObject.get("content").getAsString();
                        byte[] decodedBytes = Base64.getDecoder().decode(Base64Content);
                        return new String(decodedBytes);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch report", e);
        }

        return null;
    }

    public static String fetchSolution(int submissionID) {

        // TODO: kezelni, hogy nem ismert a fájl neve, mint a riportnál
        // TODO: több fájl is lehet, ezeket JsonArray-ben lehet tárolni, így itt az kell visszaadni
        // TODO: ReportDisplayDialog-ban lekezelni és megjeleníteni, vagy saját Dialog-ot írni erre.

        return "Ez a funkció még nincs implementálva.";

        /*String url = PluginConstants.BASE_URL + "/api/v1/students/submissions/" + submissionID + "/uploaded-files";
        StringBuilder solution = new StringBuilder();

        try {
            UserServices.refreshToken(UserServices.getRefreshToken());
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        solution.append(line);
                    }
                }
                // debug lines
                System.out.println("Response Code: " + responseCode);
                System.out.println("Raw Response: " + solution.toString());

                // Parse the report string to extract the report content
                JsonArray jsonArray = JsonParser.parseString(solution.toString()).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    if ("riport.txt".equals(jsonObject.get("filename").getAsString())) {
                        String Base64Content = jsonObject.get("content").getAsString();
                        byte[] decodedBytes = Base64.getDecoder().decode(Base64Content);
                        return new String(decodedBytes);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch report", e);
        }

        return null;*/
    }

}

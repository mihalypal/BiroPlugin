//package com.github.mihalypal.biroplugin.Services;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class FileDownloader {
//
//    public static void downloadFile(String fileURL, String saveDir, String accessToken, String fileName) throws IOException {
//        URL url = new URL(fileURL);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
//        httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
//        httpConn.setRequestProperty("Accept-Language", "hu");
//        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
//        httpConn.setRequestProperty("Connection", "keep-alive");
//        int responseCode = httpConn.getResponseCode();
//
//        // Check HTTP response code
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            // Open input stream from the HTTP connection
//            try (InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
//                 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveDir + File.separator + fileName))) {
//
//                int bytesRead;
//                byte[] buffer = new byte[4096];
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//
//                System.out.println("File downloaded: " + saveDir + File.separator + fileName);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw e;
//            }
//        } else {
//            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
//        }
//        httpConn.disconnect();
//        System.out.println("Disconnected from server.");
//    }
//}

//package com.github.mihalypal.biroplugin.Services;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Base64;
//
//public class FileDownloader {
//
//    public static void downloadFile(String fileURL, String saveDir, String accessToken) throws IOException {
//
//        URL url = new URL(fileURL);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
//        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
//        httpConn.setRequestMethod("GET");
//
//        int responseCode = httpConn.getResponseCode();
//
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            // JSON feldolgozása
//            String jsonResponse = response.toString();
//            System.out.println("Received JSON: " + jsonResponse);
//
//            // Fájl nevének kinyerése
//            String fileName = extractJsonValue(jsonResponse, "filename");
//            String fileContentBase64 = extractJsonValue(jsonResponse, "content");
//
//            if (fileName == null || fileContentBase64 == null) {
//                System.out.println("Error: JSON response is missing required fields.");
//                return;
//            }
//
//            // Fájl dekódolása és mentése
//            byte[] fileData = Base64.getDecoder().decode(fileContentBase64);
//            File outputFile = new File(saveDir, fileName);
//
//            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
//                fos.write(fileData);
//            }
//
//            System.out.println("File saved successfully: " + outputFile.getAbsolutePath());
//        } else {
//            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
//        }
//        httpConn.disconnect();
//        System.out.println("Disconnected from server.");
//    }
//
//    // JSON-ból adott kulcs értékének kinyerése egyszerű regex segítségével
//    private static String extractJsonValue(String json, String key) {
//        String pattern = "\"" + key + "\"\\s*:\\s*\"(.*?)\"";
//        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher matcher = regex.matcher(json);
//        return matcher.find() ? matcher.group(1) : null;
//    }
//}

package com.github.mihalypal.biroplugin.Services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.io.*;
import java.util.Base64;

public class FileDownloader {

    public static void downloadFile(String fileURL, String accessToken) throws IOException {
        // Lekérjük az aktuális projektet
        Project project = ProjectManager.getInstance().getOpenProjects()[0]; // Az első megnyitott projektet vesszük
        if (project == null) {
            System.out.println("No open project found.");
            return;
        }

        // A projekt gyökérmappájának lekérése
        String projectBasePath = project.getBasePath();
        if (projectBasePath == null) {
            System.out.println("Project base path not found.");
            return;
        }

        // A `src` mappa elérési útja
        File srcDir = new File(projectBasePath, "src");
        if (!srcDir.exists()) {

            // Ha nincs src mappa, akkor valószínűleg nem egy Java projekt, szóval biro_files mappába mentjük
            srcDir = new File(projectBasePath, "biro_files");

            boolean created = true;
            if (!srcDir.exists())
                created = srcDir.mkdirs(); // Ha nem létezik, létrehozzuk
            if (!created) {
                System.out.println("Failed to create src directory.");
                return;
            }
        }

        // Letöltés HTTP-n keresztül
        String jsonResponse = fetchFileData(fileURL, accessToken);
        if (jsonResponse == null) {
            System.out.println("Failed to fetch file data.");
            return;
        }

        // JSON feldolgozása
        String fileName = extractJsonValue(jsonResponse, "filename");
        String fileContentBase64 = extractJsonValue(jsonResponse, "content");

        if (fileName == null || fileContentBase64 == null) {
            System.out.println("Invalid JSON response.");
            return;
        }

        // Egyedi fájlnév generálása, ha már létezik
        File outputFile = getUniqueFileName(srcDir, fileName);

        // Fájl dekódolása és mentése a `src` mappába
        byte[] fileData = Base64.getDecoder().decode(fileContentBase64);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(fileData);
        }

        System.out.println("File saved successfully to: " + outputFile.getAbsolutePath());
    }

    // Segédfüggvény JSON letöltésére az API-ról
    private static String fetchFileData(String fileURL, String accessToken) throws IOException {
        java.net.URL url = new java.net.URL(fileURL);
        java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpConn.setRequestMethod("GET");

        int responseCode = httpConn.getResponseCode();
        if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
            System.out.println("Failed to download file. HTTP Code: " + responseCode);
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        httpConn.disconnect();

        return response.toString();
    }

    // JSON-ból adott kulcs értékének kinyerése
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"(.*?)\"";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    // Ha a fájl már létezik, generál egy új nevet sorszámozással
    private static File getUniqueFileName(File directory, String originalFileName) {
        File file = new File(directory, originalFileName);
        System.out.println("File: " + file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println("File: " + directory + " : " + originalFileName);
            return file; // Ha nincs ilyen fájl, akkor ezt használjuk
        }

        // Fájlnév kiterjesztés kezelése
        String name = originalFileName;
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            name = originalFileName.substring(0, dotIndex);
            extension = originalFileName.substring(dotIndex);
        }

        // Keressük meg az első szabad sorszámot
        int counter = 1;
        while (file.exists()) {
            file = new File(directory, name + " (" + counter + ")" + extension);
            counter++;
        }
        return file;
    }
}

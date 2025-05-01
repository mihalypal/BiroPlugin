package com.github.mihalypal.biroplugin.Services;

import com.github.mihalypal.biroplugin.config.PluginConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Ez az osztály végzi a fájlok feltöltését és a státusz-/adatlekéréseket HttpURLConnection segítségével.
 */
public class UploadService {
    private String accessToken;  // „Bearer …” tokened

    public UploadService() {

    }

    /**
     * Feltölti a feladat leadását:
     * - submissionName (String)
     * - file[] (többszörös "file" part)
     * Visszatér a létrejött SimpleId.id értékével.
     */
    public int submitFiles(int exerciseId,
                           String submissionName,
                           List<VirtualFile> files) throws IOException {
        System.out.println("Submitting files to exerciseId: " + exerciseId);
        System.out.println("Files: " + files);
        System.out.println("Base URL: " + PluginConstants.BASE_URL);

        // refresh the token
        UserServices.refreshToken(UserServices.getRefreshToken());

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        URL url = new URL(PluginConstants.BASE_URL
                + "/api/v1/students/exercises/"
                + exerciseId
                + "/submissions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Language", "hu");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        conn.setDoOutput(true);

        try (OutputStream out = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8), true))
        {
            // 1) submissionName part
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"submissionName\"\r\n");
            writer.append("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
            writer.append(submissionName).append("\r\n").flush();

            // 2) minden fájl egy "file" nevű part
            for (VirtualFile vf : files) {
                String filename = vf.getName();
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; ")
                        .append("filename=\"").append(filename).append("\"\r\n");
                writer.append("Content-Type: application/octet-stream\r\n\r\n");
                writer.flush();

                // fájl tartalmát String-be, kommenteljük a package-sort
                byte[] raw = vf.contentsToByteArray();
                String original = new String(raw, StandardCharsets.UTF_8);

                // Így “//package com.foo.bar;” kerül a fájl elejére, ha van package-sor
                String modified = original.replaceAll(
                        "(?m)^(\\s*package\\s+[^;]+;)",
                        "//$1"
                );

                // és ezt küldjük el
                byte[] data = modified.getBytes(StandardCharsets.UTF_8);
                out.write(data);
                out.flush();

                // lezáró CRLF
                writer.append("\r\n").flush();

                writer.append("\r\n").flush();
            }

            // 3) záró boundary
            writer.append("--").append(boundary).append("--\r\n").flush();
        }

        // válasz feldolgozása
        int code = conn.getResponseCode();
        String resp = readStream(code < 400
                ? conn.getInputStream()
                : conn.getErrorStream());
        if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
            JsonObject obj = JsonParser.parseString(resp).getAsJsonObject();
            return obj.get("id").getAsInt();
        } else {
            throw new IOException("submitFiles failed: HTTP " + code + " – " + resp);
        }
    }

    /** 2a. Egyszeri státuszlekérdezés. */
    public SubmissionStatus fetchSubmissionStatus(int submissionId) throws IOException {
        // refresh the token
        UserServices.refreshToken(UserServices.getRefreshToken());

        URL url = new URL(PluginConstants.BASE_URL + "/api/v1/students/submissions/" + submissionId + "/status");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Language", "hu");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            String resp = readStream(conn.getInputStream());
            JsonObject obj = JsonParser.parseString(resp).getAsJsonObject();

            SubmissionStatus st = new SubmissionStatus();
            st.state = obj.get("state").getAsString();
            st.finished = obj.get("finished").getAsBoolean();
            st.maxScore = obj.get("maxScore").getAsDouble();
            st.score = obj.has("score") && !obj.get("score").isJsonNull()
                    ? obj.get("score").getAsDouble()
                    : null;
            st.evaluationId = obj.has("evaluationId") && !obj.get("evaluationId").isJsonNull()
                    ? obj.get("evaluationId").getAsInt()
                    : null;
            return st;
        } else {
            throw new IOException("fetchSubmissionStatus failed: HTTP " + code);
        }
    }

    /** 2b. Polling: amíg finished==false, vár (delayMs), újra lekérdez. */
    public SubmissionStatus waitUntilEvaluated(int submissionId, long delayMs)
            throws IOException, InterruptedException
    {
        SubmissionStatus st;
        do {
            st = fetchSubmissionStatus(submissionId);
            if (!st.finished) {
                Thread.sleep(delayMs);
            }
        } while (!st.finished);
        return st;
    }

    /** 3. Assignment lekérése JSON-ként. */
    public JsonObject fetchAssignment(int assignmentId) throws IOException {
        // refresh the token
        UserServices.refreshToken(UserServices.getRefreshToken());

        URL url = new URL(PluginConstants.BASE_URL + "/api/v1/students/assignments/" + assignmentId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Language", "hu");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return JsonParser.parseString(readStream(conn.getInputStream()))
                    .getAsJsonObject();
        } else {
            throw new IOException("fetchAssignment failed: HTTP " + code);
        }
    }

    /** 4. Exercise részletek lekérése JSON-ként. */
    public JsonObject fetchExercise(int exerciseId) throws IOException {
        // refresh the token
        UserServices.refreshToken(UserServices.getRefreshToken());

        URL url = new URL(PluginConstants.BASE_URL + "/api/v1/students/exercises/" + exerciseId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + UserServices.getAccessToken());
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Accept-Language", "hu");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return JsonParser.parseString(readStream(conn.getInputStream()))
                    .getAsJsonObject();
        } else {
            throw new IOException("fetchExercise failed: HTTP " + code);
        }
    }

    // Segédfüggvény az InputStream-ből String-be olvasáshoz
    private static String readStream(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /** Modell a státuszválasz deszerializációjához */
    public static class SubmissionStatus {
        public String state;
        public boolean finished;
        public Double score;
        public double maxScore;
        public Integer evaluationId;
    }
}

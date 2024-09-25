package com.github.mihalypal.biroplugin.UIForms;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.openapi.application.PathManager;

import com.github.mihalypal.biroplugin.Services.UserServices;

public class BiroUILoginForm {

    private JPanel mainPanel;
    private JLabel loginLabel;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox rememberMeCheckBox;
    private String authToken;
    private BiroUIMainForm biroUIMainForm;
    private static final String CREDENTIALS_FILE_PATH = PathManager.getPluginsPath() + File.separator + "BiroPlugin" + File.separator + "credentials.txt";


    public BiroUILoginForm() {

        // Set border for the labels
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        String[] credentials = loadCredentials();
        if (credentials != null) {
            usernameTextField.setText(credentials[0]);
            passwordField.setText(credentials[1]);
            rememberMeCheckBox.setSelected(true);
        }

        loginButton.addActionListener(e -> {
            String username = usernameTextField.getText();
            String password = new String(passwordField.getPassword());
            //System.out.println("Username: " + username + " Password: " + password);

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Add meg a h-s azonosítód és a jelszavad!", "Hiba", JOptionPane.ERROR_MESSAGE);
            } else {
                if (username.length() == 7) {
                    boolean authenticated = authenticateUser(username, password);
                    if (authenticated) {
                        if (rememberMeCheckBox.isSelected()) {
                            saveCredentials(username, password);
                            // TODO: elmenteni a refresh-tokent is az auto login miatt, Ha még él a token
                        }
                        JOptionPane.showMessageDialog(null, "Sikeres bejelentkezés", "Bejelentkezés", JOptionPane.INFORMATION_MESSAGE);
                        /*System.out.println("GET /students/subject-instances: " + callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances"));

                        System.out.println("GET /students/subject-instances/1: " + callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/1"));

                        System.out.println("GET /students/subject-instances/1/assignments: " + callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/1/assignments"));*/

                        String subjects = callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances");
                        String[] subjectInstances = subjects.split("\\},\\{");
                        // Get the subjects in String Array with JSON format
                        for (int i = 0; i < subjectInstances.length; i++) {
                            if (subjectInstances[i].startsWith("[{")) {
                                subjectInstances[i] = subjectInstances[i].substring(1) + "}";
                            } else if (subjectInstances[i].endsWith("}]")) {
                                subjectInstances[i] = "{" + subjectInstances[i].substring(0, subjectInstances[i].length() - 1);
                            } else {
                                subjectInstances[i] = "{" + subjectInstances[i] + "}";
                            }
                        }
                        /*JsonObject jsonResponse = JsonParser.parseString(subjects).getAsJsonObject();
                        System.out.println("Response: " + jsonResponse);
                        System.out.println("Subjects: " + jsonResponse.get("subjectInstances"));*/

                        // after successfull login
                        showMainForm();

                    }
                    else
                        JOptionPane.showMessageDialog(null, "Hibás h-s azonosító vagy jelszó", "Hiba", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Hibás h-s azonosító", "Hiba", JOptionPane.ERROR_MESSAGE);
                }
            }


        });

    }

    private void showMainForm() {
        biroUIMainForm = new BiroUIMainForm();
        JPanel parentPanel = (JPanel) mainPanel.getParent();
        parentPanel.removeAll();
        parentPanel.add(biroUIMainForm.getMainPanel());
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    private boolean authenticateUser(String username, String password) {
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

            System.out.println("Response message: " + conn.getResponseMessage());
            System.out.println("Response content type: " + conn.getContentType());
            System.out.println("Response header fields: " + conn.getHeaderFields());
            System.out.println("Response token: " + authToken);

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
    }

    private String callGetApi(String apiUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + authToken); // JWT token hozzáadása
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private void saveCredentials(String username, String password) {
        try (FileWriter writer = new FileWriter(CREDENTIALS_FILE_PATH)) {
            writer.write(username + "\n" + encrypt(password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] loadCredentials() {
        File credentialsFile = new File(CREDENTIALS_FILE_PATH);
        if (!credentialsFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE_PATH))) {
            String username = reader.readLine();
            String password = decrypt(reader.readLine());
            return new String[]{username, password};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encrypt(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    private String decrypt(String data) {
        return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
    }

    // ENDPOINTS
    // https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances
    // https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/1
    // https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/1/assignments

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

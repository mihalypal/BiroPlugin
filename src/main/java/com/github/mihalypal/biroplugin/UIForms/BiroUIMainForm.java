package com.github.mihalypal.biroplugin.UIForms;

import com.github.mihalypal.biroplugin.Services.UserServices;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;

public class BiroUIMainForm {
    private JPanel mainPanel;
    private JButton tokenRefreshTest;
    private DefaultListModel<String> listModel;
    private JList<String> list1;
    private String accessToken;
    private String refreshToken;
    private UserServices userServices;

    public BiroUIMainForm(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        userServices = new UserServices();
        userServices.setAccessToken(accessToken);
        userServices.setRefreshToken(refreshToken);
        listModel = new DefaultListModel<>();

        //System.out.println("Access token: " + accessToken);
        //System.out.println("Refresh token: " + refreshToken);

        tokenRefreshTest.addActionListener(e -> {
            //System.out.println("Access token: " + accessToken);
            //System.out.println("Refresh token: " + refreshToken);
            System.out.println("Access token: " + userServices.getAccessToken());
            System.out.println("Refresh token: " + userServices.getRefreshToken());
            userServices.refreshToken(refreshToken);
            System.out.println("Access token: " + userServices.getAccessToken());
            System.out.println("Refresh token: " + userServices.getRefreshToken());
        });

        String subjects = userServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances");
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
        /*subjects = subjects.replace("[{", "{");
        subjects = subjects.replace("}]", "}");
        JsonObject jsonResponse = JsonParser.parseString(subjects).getAsJsonObject();
        System.out.println("Subjects: " + jsonResponse.get("subjectName"));*/
        System.out.println("Subjects: " + subjectInstances.length);
        System.out.println("Subjects: " + subjectInstances[0]);
        for (String subjectInstance : subjectInstances) {
            JsonObject jsonResponse = JsonParser.parseString(subjectInstance).getAsJsonObject();
            System.out.println("Subjects: " + jsonResponse.get("subjectName"));
            listModel.addElement(jsonResponse.get("subjectName").getAsString());
        }
        list1.setModel(listModel);

        list1.addListSelectionListener(e -> {
            System.out.println("Selected: " + list1.getSelectedValue());

            // TODO:
            // Mivel ki lehet választani a tárgyat és van rá ActionListener,
            // ezért a kiválasztott tárgyhoz tartozó adatokat lekérhetjük
            // és megjeleníthetjük a felületen dinamikusan.
            // Alul egy "számonkérések" listát lehetne megjeleníteni
            // a kiválasztott tárgyhoz tartozó számonkérésekkel.
            // Ehhez a tárgyak assignmnetjeit előre is le lehetne kérni, hogy ne kelljen minden kattintásra lekérni.
            // Vagy elsőre lekéri az összes tárgyat és azokhoz tartozó számonkéréseket.
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}

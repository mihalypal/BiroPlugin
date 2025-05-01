package com.github.mihalypal.biroplugin.UIForms;

import com.github.mihalypal.biroplugin.Services.UserServices;
import com.github.mihalypal.biroplugin.appearanceChanges.AssignmentCellRenderer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BiroUIMainForm {
    private JPanel mainPanel;
    private JButton tokenRefreshTest;
    private DefaultListModel<String> subjectAssignmentList;
    private JList<String> list1;
    private JList<String> list2;
    private JButton openAssignmentButton;

    public BiroUIMainForm(String accessToken, String refreshToken) {
        UserServices.setAccessToken(accessToken);
        UserServices.setRefreshToken(refreshToken);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        subjectAssignmentList = new DefaultListModel<>();
        ArrayList<String> subjectNamesWithIds = new ArrayList<>();
        ArrayList<String> allAssignmentsOfSelectedSubject = new ArrayList<>();

        //System.out.println("Access token: " + accessToken);
        //System.out.println("Refresh token: " + refreshToken);

        tokenRefreshTest.addActionListener(e -> {
            //System.out.println("Access token: " + accessToken);
            //System.out.println("Refresh token: " + refreshToken);
            System.out.println("Access token: " + UserServices.getAccessToken());
            System.out.println("Refresh token: " + UserServices.getRefreshToken());
            UserServices.refreshToken(refreshToken);
            System.out.println("Access token: " + UserServices.getAccessToken());
            System.out.println("Refresh token: " + UserServices.getRefreshToken());
        });

        String subjects = UserServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances");
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

        // TODO: do the check availability for the subjects and check their dates from-to

        for (String subjectInstance : subjectInstances) {
            JsonObject jsonResponse = JsonParser.parseString(subjectInstance).getAsJsonObject();
            //System.out.println("Subjects: " + jsonResponse.get("subjectName"));
            String subjectName = jsonResponse.get("subjectName").getAsString();
            String subjectInstanceId = jsonResponse.get("subjectInstanceId").getAsString();
            //listModel.addElement(jsonResponse.get("subjectName").getAsString());
            subjectNamesWithIds.add(subjectName + ";" + subjectInstanceId);
            listModel.addElement(subjectName);
        }
        list1.setModel(listModel);

        list1.addListSelectionListener(e -> {
            //System.out.println("Selected: " + list1.getSelectedValue());
            //System.out.println("Selected: " + Arrays.toString(list1.getSelectedValue().split(";")));

            list2.setCellRenderer(new AssignmentCellRenderer());
            // Clear the list of assignments
            subjectAssignmentList.clear();

            // Get the selected subject name and instance ID
            String subjectName = list1.getSelectedValue();
            String subjectInstanceId = subjectNamesWithIds.stream()
                    .filter(s -> s.contains(subjectName))
                    .findFirst()
                    .orElse("")
                    .split(";")[1];

            // Call the API to get the assignments for the selected subject
            System.out.println("Selected: " + subjectName + " - " + subjectInstanceId);
            String endpoint = "https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/" + subjectInstanceId + "/assignments";
            String assignments = apiCall(endpoint);
            System.out.println("Assignments for " + subjectName + ": " + assignments);

            // Next is to parse the JSON response and display the assignments on UI with list elements
            // The assignments can be selected and an ActionListener can be opened to display the details and assign the exercises
            /*             * Az assignments JSON objektum tartalmazza a következő
             * kulcsok:
             * assignmentAssignedStudentId - kell az API híváshoz
             * startTime - kezdési időpont
             * endTime - határidő
             * assignmentName - feladat neve
             */
            // iterate over the assignments which is a string what contains JSON objects in an array
            //convert the string to JSON objects
            String[] assignmentInstances = assignments.split("\\},\\{");
            for (int i = 0; i < assignmentInstances.length; i++) {
                if (assignmentInstances[i].startsWith("[{")) {
                    assignmentInstances[i] = assignmentInstances[i].substring(1) + "}";
                } else if (assignmentInstances[i].endsWith("}]")) {
                    assignmentInstances[i] = "{" + assignmentInstances[i].substring(0, assignmentInstances[i].length() - 1);
                } else {
                    assignmentInstances[i] = "{" + assignmentInstances[i] + "}";
                }
            }
            for (String assignmentInstance : assignmentInstances) {
                JsonObject jsonResponse = JsonParser.parseString(assignmentInstance).getAsJsonObject();
                //System.out.println("Assignment: " + jsonResponse.get("assignmentName")); // assignmentName list
                String assignmentName = jsonResponse.get("assignmentName").getAsString();
                String endTime = jsonResponse.get("endTime").getAsString().replace("-", ".").replace("T", " ");

                String assignmentData = assignmentName + ";" + endTime;

                // get local time and add just those assignments which are not expired and not started yet
                if (LocalDateTime.now().isBefore(LocalDateTime.parse(jsonResponse.get("endTime").getAsString()))        // még nem járt le
                        && LocalDateTime.now().isAfter(LocalDateTime.parse(jsonResponse.get("startTime").getAsString()))) {    // már elkezdődött
                    subjectAssignmentList.addElement(assignmentData);
                }
                allAssignmentsOfSelectedSubject.add(assignmentInstance);

                //subjectAssignmentList.addElement(assignmentData);
            }
            list2.setModel(subjectAssignmentList);

            // TODO:
            // Mivel ki lehet választani a tárgyat és van rá ActionListener,
            // ezért a kiválasztott tárgyhoz tartozó adatokat lekérhetjük
            // és megjeleníthetjük a felületen dinamikusan.
            // Alul egy "számonkérések" listát lehetne megjeleníteni
            // a kiválasztott tárgyhoz tartozó számonkérésekkel.
            // Ehhez a tárgyak assignmnetjeit előre is le lehetne kérni, hogy ne kelljen minden kattintásra lekérni.
            // Vagy elsőre lekéri az összes tárgyat és azokhoz tartozó számonkéréseket.
        });

        // test output
        list2.addListSelectionListener(e -> System.out.println("Selected: " + list2.getSelectedValue()));

        openAssignmentButton.addActionListener(e -> {
            //System.out.println("Selected: " + list2.getSelectedValue()); // test output
            String selectedAssignment = allAssignmentsOfSelectedSubject.stream()
                .filter(s -> s.contains(list2.getSelectedValue().split(";")[0]))
                .findFirst()
                .orElse("");
            JsonObject selectedAssignmentJSON = JsonParser.parseString(selectedAssignment).getAsJsonObject();
            System.out.println(selectedAssignmentJSON.get("assignmentName").getAsString() + " nevű feladat kezdése...");
            //System.out.println("Selected assignment: " + selectedAssignment); test output
            showAssignmentView(accessToken, refreshToken, selectedAssignment);
        });
    }

    private void showAssignmentView(String accessToken, String refreshToken, String assignment) {
        AssignmentView assignmentView = new AssignmentView(accessToken, refreshToken, assignment);
        JPanel parentPanel = (JPanel) mainPanel.getParent();
        parentPanel.removeAll();
        parentPanel.add(assignmentView.getMainPanel());
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    // api: https://biro3.inf.u-szeged.hu/api/v1/students/subject-instances/4/assignments
    private String apiCall(String url) {
        return UserServices.callGetApi(url);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

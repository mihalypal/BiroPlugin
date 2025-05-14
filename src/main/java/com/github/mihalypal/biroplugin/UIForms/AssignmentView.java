package com.github.mihalypal.biroplugin.UIForms;

import com.github.mihalypal.biroplugin.Dialog.*;
import com.github.mihalypal.biroplugin.Model.*;
import com.github.mihalypal.biroplugin.Services.*;
import com.github.mihalypal.biroplugin.appearanceChanges.CustomButtonUI;
import com.github.mihalypal.biroplugin.appearanceChanges.CustomProgressBarUI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.html.HtmlRenderer;

import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssignmentView {
    private JPanel mainPanel;
    private JLabel assignmentNameLabel;
    private JLabel scoreOfOpenedExercise;
    private JProgressBar progressBar;
    private JEditorPane assignmentDescriptionPane;
    private JPanel exercisePanel;
    private String accessToken;
    private String refreshToken;
    private UserServices userServices;
    private ArrayList<JsonObject> exercises;
    private ArrayList<StarterFile> starterFiles;
    private int currentExerciseId;
    private BiroUIMainForm biroUIMainForm;
    private JButton backToMainButton;
    private JPanel topPanel;
    private FileService fileDownloader;
    private final UploadService uploadService;
    private List<JButton> exerciseButtons;
    public static Exercise currentExercise;
    public static Assignment currentAssignment;

    public AssignmentView(String accessToken, String refreshToken, String AssignmentTextFromMainForm) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userServices = new UserServices();
        this.exercises = new ArrayList<>();
        this.starterFiles = new ArrayList<>();
        this.uploadService = new UploadService();
        this.exerciseButtons = new ArrayList<>();
        currentExercise = new Exercise();
        currentAssignment = new Assignment();

        // tokenek kiírása - tesztelésre
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
        System.out.println("Assignment: " + AssignmentTextFromMainForm);

        // get the concrete assignment
        JsonObject wholeAssignmentJson = JsonParser.parseString(AssignmentTextFromMainForm).getAsJsonObject();
        String assignmentStudentId = wholeAssignmentJson.get("assignmentAssignedStudentId").getAsString();
        String currentAssignmentString = userServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/assignments/" + assignmentStudentId);
        JsonObject currentAssignmentJson = JsonParser.parseString(currentAssignmentString).getAsJsonObject();
        currentAssignmentJson = currentAssignmentJson.get("assignmentDetails").getAsJsonObject();
        System.out.println("Current assignment: " + currentAssignment);

        //Assignment currentAssignment = new Assignment();
        currentAssignment.setAssignmentAssignedStudentId(checkJsonObjectIsNullInt(currentAssignmentJson.get("assignmentAssignedStudentId")));
        currentAssignment.setStartTime(LocalDateTime.parse(checkJsonObjectIsNull(currentAssignmentJson.get("startTime"))));
        currentAssignment.setEndTime(LocalDateTime.parse(checkJsonObjectIsNull(currentAssignmentJson.get("endTime"))));
        currentAssignment.setAssignmentName(checkJsonObjectIsNull(currentAssignmentJson.get("assignmentName")));
        currentAssignment.setAssignmentDescription(checkJsonObjectIsNull(currentAssignmentJson.get("assignmentDescription")));
        currentAssignment.setAssignmentType(checkJsonObjectIsNull(currentAssignmentJson.get("assignmentType")));
        currentAssignment.setMaxScore(checkJsonObjectIsNullInt(currentAssignmentJson.get("maxScore")));
        currentAssignment.setMinScore(checkJsonObjectIsNullInt(currentAssignmentJson.get("minScore")));
        currentAssignment.setScore(checkJsonObjectIsNullInt(currentAssignmentJson.get("score")));
        currentAssignment.setSubjectName(checkJsonObjectIsNull(currentAssignmentJson.get("subjectName")));
        currentAssignment.setStudentGroupName(checkJsonObjectIsNull(currentAssignmentJson.get("studentGroupName")));
        currentAssignment.setSubjectInstanceId(checkJsonObjectIsNull(currentAssignmentJson.get("subjectInstanceId")));
        System.out.println("Assignment: " + currentAssignment);


        // TODO: check-avaibility request-el megnézni, hogy elérhető-e a feladat, mielőtt lekérem, mert IDE Error Occured lesz és meghal a plugin | BiroUIMain-ben kell még !!!
        JsonObject exercisesOfTheAssignment = JsonParser.parseString(currentAssignmentString).getAsJsonObject();
        JsonArray exerciseStatuses = exercisesOfTheAssignment.get("exerciseStatuses").getAsJsonArray();
        System.out.println("Exercises of the assignment: " + exerciseStatuses);

        for (JsonElement je : exerciseStatuses) {
            JsonObject exerciseStatus = je.getAsJsonObject();
            ExerciseStatus exerciseStatusObject = new ExerciseStatus();
            exerciseStatusObject.setAssignedExerciseId(checkJsonObjectIsNullInt(exerciseStatus.get("assignedExerciseId")));
            exerciseStatusObject.setExerciseIndex(checkJsonObjectIsNullInt(exerciseStatus.get("exerciseIndex")));
            exerciseStatusObject.setExerciseState(checkJsonObjectIsNull(exerciseStatus.get("exerciseState")));
            currentAssignment.addExerciseStatus(exerciseStatusObject);
        }
        System.out.println("Exercise statuses: " + currentAssignment.getExerciseStatuses());
        System.out.println("Assignment: " + currentAssignment);

        for (int i = 0; i < exerciseStatuses.size(); i++) {
            exercises.add(exerciseStatuses.get(i).getAsJsonObject());
        }

        mainPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel(new BorderLayout());

        JsonObject assignmentJson = JsonParser.parseString(AssignmentTextFromMainForm).getAsJsonObject();
        String assignmentName = assignmentJson.get("assignmentName").getAsString();
        String assignmentDescription = assignmentJson.get("assignmentDescription").getAsString();
        System.out.println("Assignment name: " + assignmentName);
        System.out.println("Assignment description: " + assignmentDescription);

        assignmentNameLabel = new JLabel("  " + assignmentName + "  ");
        topPanel.add(assignmentNameLabel, BorderLayout.WEST);

        scoreOfOpenedExercise = new JLabel("", SwingConstants.CENTER);

        // Custom ProgressBar design
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE); // Szöveg színe
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK); // Szöveg háttérszíne
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        //progressBar.setUI(new CustomProgressBarUI(new Color(63, 81, 181), new Color(63, 81, 181, 12)));
        //progressBar.setUI(new CustomProgressBarUI(new Color(147, 147, 147), new Color(78, 78, 78)));
        progressBar.setUI(new CustomProgressBarUI(new Color(50, 130, 200, 170), new Color(78, 78, 78)));



// Színek beállítása
//        progressBar.setForeground(new Color(0, 0, 0)); // Kék előlap
//        progressBar.setBackground(new Color(220, 220, 220)); // Világos szürke háttér

        JPanel topCenterPanel = new JPanel(new BorderLayout());
        topCenterPanel.add(scoreOfOpenedExercise, BorderLayout.EAST);
        topCenterPanel.add(progressBar, BorderLayout.CENTER);
        topPanel.add(topCenterPanel, BorderLayout.CENTER);

        backToMainButton = new JButton("Vissza a feladatokhoz");
        backToMainButton.addActionListener(e -> showMainForm());
        topPanel.add(backToMainButton, BorderLayout.EAST);

        assignmentDescriptionPane = new JEditorPane();
        assignmentDescriptionPane.setContentType("text/html");
        assignmentDescriptionPane.setText(convertMarkdownToHtml(assignmentDescription + "\n\nA feladatsor elkezdéséhez válaszd ki a kívánt feladat számát fent!"));
        assignmentDescriptionPane.setEditable(false);

        exercisePanel = new JPanel();
        exercisePanel.setLayout(new BoxLayout(exercisePanel, BoxLayout.X_AXIS));

        // Assignment scores
        int wholeScore = 0;
        try {
            wholeScore = wholeAssignmentJson.get("score").getAsInt();
        } catch (Exception e) {
            System.out.println("No score for this assignment.");
        }
        int wholeMaxScore = wholeAssignmentJson.get("maxScore").getAsInt();
        displayInformation(assignmentDescription, wholeScore, wholeMaxScore);

        // INFO BUTTON
        JButton exerciseInformationButton = new JButton("i");
        exerciseInformationButton.setUI(new CustomButtonUI("INFO"));
        int finalWholeScore = wholeScore;       // to use in lambda
        exerciseInformationButton.addActionListener(e -> displayInformation(assignmentDescription, finalWholeScore, wholeMaxScore));
        exerciseInformationButton.setMinimumSize(new Dimension(50, 50));
        exerciseInformationButton.setMaximumSize(new Dimension(50, 50));
        exerciseInformationButton.setPreferredSize(new Dimension(50, 50));
        exercisePanel.add(exerciseInformationButton);

        /*for (int i = 0; i < 1; i++) {
            JButton exerciseButton = new JButton("i");
            exerciseButton.setUI(new CustomButtonUI("INFO"));
            // TESZTELÉS - GOMBOK SZÍNEZÉSE, DIZÁJN ELEMEK MEGJELENÉSE
            if (i % 2 == 0)
                exerciseButton = new JButton("i" +  " ✅");
            else if (i == 3)
                exerciseButton = new JButton("i" +  " ❌");
            else
                exerciseButton = new JButton("i");
            exerciseButton.addActionListener(e -> displayInformation(assignmentDescription));
            exerciseButton.setMinimumSize(new Dimension(50, 50));
            exerciseButton.setMaximumSize(new Dimension(50, 50));
            exerciseButton.setPreferredSize(new Dimension(50, 50));
            exercisePanel.add(exerciseButton);
        }*/

        for (int i = 0; i < exercises.size(); i++) {
            JButton exerciseButton = new JButton("" + (i + 1));
            exerciseButton.setUI(new CustomButtonUI(exercises.get(i).get("exerciseState").getAsString()));
            int finalI = i;
            exerciseButton.addActionListener(e -> {
                displayExercise(finalI);
            });
            exerciseButton.setMinimumSize(new Dimension(50, 50));
            exerciseButton.setMaximumSize(new Dimension(50, 50));
            exerciseButton.setPreferredSize(new Dimension(50, 50));
            exerciseButtons.add(exerciseButton);
            exercisePanel.add(exerciseButton);
        }

        JScrollPane exerciseScrollPane = new JScrollPane(exercisePanel);
        exerciseScrollPane.setMinimumSize(new Dimension(Integer.MAX_VALUE, 65));
        exerciseScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 65));
        topPanel.add(exerciseScrollPane, BorderLayout.SOUTH);
        topPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 100));
        topPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));

        /*Box box = Box.createVerticalBox();
        box.add(exercisePanel);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));*/
//        exerciseScrollPane.setMinimumSize(new Dimension(exerciseScrollPane.getPreferredSize().width, 50));
//        exerciseScrollPane.setMaximumSize(new Dimension(exerciseScrollPane.getPreferredSize().width, 50));
//        exerciseScrollPane.setPreferredSize(new Dimension(exerciseScrollPane.getPreferredSize().width, 50));

//        JScrollPane assignmentDescriptionScrollPane = new JScrollPane(assignmentDescriptionPane);
//        assignmentDescriptionScrollPane.setMinimumSize(new Dimension(assignmentDescriptionScrollPane.getPreferredSize().width, 200));
//        assignmentDescriptionScrollPane.setFont(new Font("Arial", Font.PLAIN, 12));
//        System.out.println("Screen height: " + Toolkit.getDefaultToolkit().getScreenSize().height);
        //what if i have multiple monitors with diffrerent resolutions?

        //TODO: a bottomPanelt még a gombok létrehozása előtt kell létrehozni, és az őket létrehozó for ciklusban kell valahogy hozzáadni a bottomPanelhez a letöltési gombot, mert a gombok létrehozásánál tudom lekérni az adott feladat adatait
        JPanel bottomPanel = new JPanel();
        //bottomPanel.add(new JLabel("Ide fog kerülni a fel- és letöltési lehetőség")); // korábbi teszt kód a bottomPanelhez
        JButton testForDownload = new JButton("Biztosított fájl(ok) letöltése");
        testForDownload.addActionListener(e -> {
            System.out.println("Letöltés gomb megnyomva");
            String assignedExerciseId = "";
//            try {
//                assignedExerciseId = exercises.get(0).get("assignedExerciseId").getAsString();
//            } catch (Exception exception) {
//                System.out.println("No assigned exercise id for this exercise.");
//            }
//            JsonArray starterFiles = exercisesOfTheAssignment.get("starterFiles").getAsJsonArray();
//            System.out.println("Starter files: " + starterFiles);
            String downloadFileUrl = "https://biro3.inf.u-szeged.hu/api/v1/students/exercises/206370/starterfiles/555";
            for (StarterFile starterFile : this.starterFiles) {
                System.err.println("Starter file: " + starterFile.getName());
                try {
                    assignedExerciseId = exercises.get(starterFile.getExerciseId()).get("assignedExerciseId").getAsString();
                } catch (Exception exception) {
                    System.out.println("No assigned exercise id for this exercise.");
                }
                if (starterFile.getExerciseId() == currentExerciseId) {
                    System.err.println("Starter file: " + starterFile.getName() + " - " + starterFile.getId());
                    downloadFileUrl = "https://biro3.inf.u-szeged.hu/api/v1/students/exercises/" + assignedExerciseId + "/starterfiles/" + starterFile.getId();
                    try {
                        UserServices.refreshToken(this.refreshToken);
                        this.accessToken = UserServices.getAccessToken();
                        String downloadedFilePath = FileService.downloadFile(downloadFileUrl/*, "C:\\Users\\Pali\\Downloads"*/, this.accessToken, assignmentName, currentExerciseId);
                        System.err.println("File downloaded: " + downloadFileUrl);

                        SwingUtilities.invokeLater(() -> {
                            VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(downloadedFilePath);
                            if (file != null) {
                                Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                                if (openProjects.length > 0) {
                                    FileEditorManager.getInstance(openProjects[0]).openFile(file, true);
                                }
                            }
                        });
                        if (UserServices.isLogSendingAccepted()) {
                            LogSenderService.sendStatistic("Starter file letöltve: " + currentExercise.getName() + " (" + assignmentName + ")");
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        if (UserServices.isLogSendingAccepted()) {
                            LogSenderService.sendError("Starter file letöltése sikertelen: " + exception.getMessage());
                        }
                    }
                }
            }
        });
        bottomPanel.add(testForDownload);
        bottomPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 100));
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton uploadButton = new JButton("Fájl(ok) feltöltése");
        uploadButton.addActionListener((ActionEvent e) -> {
            String packageNameOfCurrentExercise = FileService.normalizeName(assignmentName);
            if (!packageNameOfCurrentExercise.startsWith("_")) {
                packageNameOfCurrentExercise = "_" + packageNameOfCurrentExercise;
            }
            packageNameOfCurrentExercise += ".feladat_" + String.format("%02d", currentExerciseId + 1);
            System.out.println("Feltöltés gomb megnyomva");
            // Elmenteni az összes változtatást, ha a user elfelejti és nincs autosave bekapcsolva
            FileDocumentManager.getInstance().saveAllDocuments();
            Project[] open = ProjectManager.getInstance().getOpenProjects();
            if (open.length == 0) return;
            Project project = open[0];
            if (project == null) return;

            FileUploadDialog fileUploadDialog = new FileUploadDialog(project, packageNameOfCurrentExercise);
            if (fileUploadDialog.showAndGet()) {
                // Get the selected files from the dialog, if OK button was pressed
                List<VirtualFile> selectedFiles = fileUploadDialog.getSelectedFiles();
                // Process the selected files (upload them)
                System.out.println("Selected files: " + selectedFiles);
                // Upload the files
                if (selectedFiles.isEmpty()) return;

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        // 3a) Feltöltés → submissionId
                        int submissionId = uploadService.submitFiles(
                                currentAssignment.getExerciseStatuses().get(currentExerciseId).getAssignedExerciseId(), "", selectedFiles
                        );

                        // 3b) Polling 1s-kel
                        UploadService.SubmissionStatus status =
                                uploadService.waitUntilEvaluated(submissionId, 1_000);

                        // 3c) Lekérjük a frissített assignmentet és exercise-t
                        JsonObject updatedAssignment = uploadService.fetchAssignment(currentAssignment.getAssignmentAssignedStudentId());
                        JsonObject updatedExercise   = uploadService.fetchExercise(currentAssignment.getExerciseStatuses().get(currentExerciseId).getAssignedExerciseId());
                        System.out.println("Updated assignment: " + updatedAssignment);
                        System.out.println("Updated exercise: " + updatedExercise);

                        currentExercise = loadExercise(updatedExercise);

                        // 4) UI-frissítés EDT-n
                        SwingUtilities.invokeLater(() -> {
                            // például:
                            int index = currentAssignment.getExerciseStatuses().get(currentExerciseId).getExerciseIndex() - 1;
                            updateAssignmentView(updatedAssignment, updatedExercise, index);
                            displayExercise(index);
                            Notifications.Bus.notify(
                                    NotificationGroupManager.getInstance()
                                            .getNotificationGroup("Accepted language levels")
                                            .createNotification(
                                                    "Feltöltés kész",
                                                    "<html>Állapot: " + (status.state.equals("EVALUATED") ? "Sikeres feltöltés" : "Valami még nem jó")
                                                            + (status.score != null
                                                            ? "<br>Pontszám: " + status.score + "/" + status.maxScore
                                                            : "")
                                                            + (status.score != null
                                                            ? ((status.score == status.maxScore)
                                                            ? "<br>Gratulálok, szép munka!"
                                                            : "")
                                                            : "")
                                                            + "<br><a href=\"viewReport\">Riport megtekintése</a></html>",
                                                    NotificationType.INFORMATION
                                            )
                                            .setListener((notification, hyperlinkEvent) -> {
                                                if ("viewReport".equals(hyperlinkEvent.getDescription())) {
                                                    String reportText;
                                                    try {
                                                        reportText = ReportService.fetchReport(
                                                                currentExercise.getSubmissions()
                                                                        .get(currentExercise.getSubmissions().size() - 1)
                                                                        .getEvaluations()
                                                                        .get(currentExercise.getSubmissions()
                                                                                .get(currentExercise.getSubmissions().size() - 1)
                                                                                .getEvaluations()
                                                                                .size() - 1)
                                                                        .getEvaluationId()
                                                        );
                                                    } catch (Exception ex) {
                                                        reportText = "Riport nem érhető el.";
                                                    }
                                                    ReportDisplayDialog dlg = new ReportDisplayDialog(project, reportText);
                                                    dlg.show();
                                                    if (UserServices.isLogSendingAccepted()) {
                                                        LogSenderService.sendStatistic("Riport megnyitva IDE értesítésből: " + currentExercise.getName());
                                                    }
                                                }
                                            }),
                                    project
                            );
                        });
                        if (UserServices.isLogSendingAccepted()) {
                            LogSenderService.sendStatistic("Egy feladat feltöltése megtörtént: " + currentExercise.getName() + " (" + assignmentName + ")");
                        }
                    } catch (Exception ex) {
                        // TODO: Megtudni milyen ERROR jön vissza, ha megoldás közben lejár az idő a beadásra és azt is lekezelni
                        SwingUtilities.invokeLater(() ->
                                Notifications.Bus.notify(
                                        new Notification("Find Problems",
                                                "Hiba a feltöltés során",
                                                (ex.getMessage().contains("Sikertelen beadás: feltöltési limit elérve")
                                                        ? "Sikertelen beadás: feltöltési limit elérve"
                                                        : "Hiba a feltöltés során: " + ex.getMessage()),
                                                NotificationType.ERROR),
                                        project
                                )
                        );
                        if (UserServices.isLogSendingAccepted()) {
                            LogSenderService.sendError("Feladat feltöltése sikertelen: " + ex.getMessage());
                        }
                    }
                });
            }
        });

        bottomPanel.add(uploadButton);

        JButton reportButton = new JButton("Riportok megtekintése");
        reportButton.addActionListener(e -> {
            System.out.println("Riport megtekintése gomb megnyomva");
            // legfrissebb riport lekérése
            /*String reportText;
            System.out.println(currentExercise.toString());
            try {
                reportText = ReportService.fetchReport(
                        currentExercise.getSubmissions()
                                .get(currentExercise.getSubmissions().size() - 1)
                                .getEvaluations()
                                .get(currentExercise.getSubmissions()
                                        .get(currentExercise.getSubmissions().size() - 1)
                                        .getEvaluations()
                                        .size() - 1)
                                .getEvaluationId()
                );
            } catch (Exception ex) {
                reportText = "Riport nem érhető el.";
            }
            // legújabb riport megnyitása
            ReportDisplayDialog dlg = new ReportDisplayDialog(ProjectManager.getInstance().getOpenProjects()[0], reportText);
            dlg.show();*/
            // Korábbi feltöltések panel megnyitása
            if (currentExercise.getSubmissions() == null || currentExercise.getSubmissions().isEmpty()) {
                // send notifications about no riport available
                Notifications.Bus.notify(
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("Attach to Process action")
                                .createNotification(
                                        "Nincs riport",
                                        "Nincs elérhető riport a kiválasztott feladathoz.",
                                        NotificationType.INFORMATION
                                )
                );
            } else {
                PreviousUploadsDialog dlg = new PreviousUploadsDialog(ProjectManager.getInstance().getOpenProjects()[0], currentExercise);
                dlg.show();
            }
            if (UserServices.isLogSendingAccepted()) {
                LogSenderService.sendStatistic("Riportok megtekintése gomb megnyomva: " + currentExercise.getName());
            }
        });

        bottomPanel.add(reportButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(assignmentDescriptionPane), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        if (UserServices.isLogSendingAccepted()) {
            LogSenderService.sendStatistic("Feladatsor megnyitva: " + assignmentName);
        }

    }

    private void updateAssignmentView(JsonObject updatedAssignment, JsonObject updatedExercise, int index) {
        JsonArray statuses = updatedAssignment.getAsJsonArray("exerciseStatuses");
        for (int i = 0; i < exerciseButtons.size(); i++) {
            JsonObject statusObj = statuses.get(i).getAsJsonObject();
            String newState = statusObj.get("exerciseState").getAsString();

            JButton btn = exerciseButtons.get(i);
            btn.setUI(new CustomButtonUI(newState));
        }

        exercisePanel.revalidate();
        exercisePanel.repaint();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void displayInformation(String assignmentDescription, int wholeScore, int wholeMaxScore) {
        assignmentDescriptionPane.setText(convertMarkdownToHtml(assignmentDescription + "\n\nA feladatsor elkezdéséhez válaszd ki a kívánt feladat számát fent!"));
        assignmentDescriptionPane.setEditable(false);
        int wholePercentage = (int) ((double) wholeScore / wholeMaxScore * 100);
        //scoreOfOpenedExercise.setText("█".repeat(Math.max(0, wholePercentage)) + "░".repeat(Math.max(0, 10 - wholePercentage)) + " " + wholeScore + " / " + wholeMaxScore); // progress bar with 10 steps (10% each) and the reached score / max score
        progressBar.setValue(wholePercentage);
        scoreOfOpenedExercise.setText("  " + wholeScore + " / " + wholeMaxScore + "  "); // progress bar and the reached score / max score
        currentExercise = loadExercise(new JsonObject()); // empty exercise to unload the previous exercise
    }

    private void displayExercise(int index) {
        JsonObject exercise = exercises.get(index);

        // törölni a listás, hogy az előző feladatokhoz tartozó fájlok ne maradjanak benne
        this.starterFiles.clear();

        System.out.println("index: " + index);
        System.out.println("Exercise: " + exercise.get("assignedExerciseId").getAsString());
        JsonObject exerciseGetByAPI = JsonParser.parseString(userServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/exercises/" + exercise.get("assignedExerciseId").getAsString())).getAsJsonObject();
        System.out.println("Exercise: " + exerciseGetByAPI.toString());
        currentExercise = loadExercise(exerciseGetByAPI);

        // get the starterfiles
        JsonArray starterFiles = exerciseGetByAPI.get("starterFiles").getAsJsonArray();
        for (JsonElement je : starterFiles) {
            System.err.println("ID: " + je.getAsJsonObject().get("starterFileId").getAsString());
            System.err.println("filename: " + je.getAsJsonObject().get("filename").getAsString());
            StarterFile starterFile = new StarterFile(
                    je.getAsJsonObject().get("starterFileId").getAsInt(),
                    je.getAsJsonObject().get("filename").getAsString(),
                    je.getAsJsonObject().get("viewable").getAsBoolean(),
                    je.getAsJsonObject().get("copyable").getAsBoolean(),
                    je.getAsJsonObject().get("downloadable").getAsBoolean(),
                    index
            );
            this.starterFiles.add(starterFile);
        }

        // set the current exerciseId
        this.currentExerciseId = index;

        //display the reached / max score of the exercise
        int reachedScore = 0;
        try {
            reachedScore = exerciseGetByAPI.get("score").getAsInt();
        } catch (Exception e) {
            System.out.println("No reached score for this exercise.");
        }
        int maxScore = exerciseGetByAPI.get("maxScore").getAsInt();
        int percentage = (int) ((double) reachedScore / maxScore * 100);
        //scoreOfOpenedExercise.setText("Pontszám: " + reachedScore + " / " + maxScore); //  "Pontszám: " elért / összes
        //scoreOfOpenedExercise.setText("Pontszám: " + reachedScore + " / " + maxScore + "█".repeat(Math.max(0, percentage)) + "░".repeat(Math.max(0, 10 - percentage))); //  "Pontszám: " reached score / max score and the progress bar with 10 steps (10% each)
        //scoreOfOpenedExercise.setText("█".repeat(Math.max(0, percentage)) + "░".repeat(Math.max(0, 10 - percentage)) + " " + reachedScore + " / " + maxScore); // progress bar with 10 steps (10% each) and the reached score / max score
        progressBar.setValue(percentage);
        scoreOfOpenedExercise.setText("  " + reachedScore + " / " + maxScore + "  "); // progress bar with 10 steps (10% each) and the reached score / max score

        //display exercise name with max score and difficulty level
        String exerciseNameWithMaxScore = "<h1>" + exerciseGetByAPI.get("indexInTaskList").getAsString() + ". " + exerciseGetByAPI.get("name").getAsString() + " (" + maxScore + " pont)";
        int difficulty = exerciseGetByAPI.get("difficultyLevel").getAsInt();
        exerciseNameWithMaxScore += " <span>" + "⭐".repeat(Math.max(0, difficulty)) + "</span></h1>";

        //wrap story with div to style it
        String exerciseDescription = exerciseGetByAPI.get("description").getAsString();
        System.out.println("Exercise description: " + exerciseDescription);
        exerciseDescription = exerciseDescription.replace("story\">", "story\"><strong>Story:</strong><br><i>"); // style="border: 2px solid orange; margin-bottom: 10px; padding: 5px;"
        exerciseDescription = exerciseDescription.replace("</div>", "</i></div>");

        //wrap img with div to center it
        exerciseDescription = exerciseDescription.replace("<img", "<div style=\"text-align: center;\"><img"); // margin: auto;
        int imgIndex = exerciseDescription.indexOf("<img");
        while (imgIndex != -1) {
            int imgEndIndex = exerciseDescription.indexOf(">", imgIndex);
            exerciseDescription = exerciseDescription.substring(0, imgEndIndex + 1) + "</div>" + exerciseDescription.substring(imgEndIndex + 1);
            imgIndex = exerciseDescription.indexOf("<img", imgEndIndex);
        }

        //exerciseDescription = exerciseDescription.replace("<img", "<img style=\"margin: auto;\" ");

        exerciseDescription = exerciseNameWithMaxScore + "\n\n" + exerciseDescription;
        System.out.println("Exercise description: " + exerciseDescription);
        assignmentDescriptionPane.setText(convertMarkdownToHtml(exerciseDescription));
        assignmentDescriptionPane.setCaretPosition(0);

        if (UserServices.isLogSendingAccepted()) {
            LogSenderService.sendStatistic("Feladat megnyitva: " + currentExercise.getName());
        }
    }

    private String convertMarkdownToHtml(String markdown) {
        /*MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        return renderer.render(parser.parse(markdown));*/
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String cssStyles = "<style>" +
                "body { background-color: #3c3c3c; color: white; font-family: Arial, sans-serif; font-size: 12px; margin: 10px; }" +
                //set h1 span font-family to default to display emojis
                "h1 span { font-family: sans-serif; }" +
                ".story { border: 2px solid orange; padding: 5px; margin-bottom: 10px; background-color: #888; color: purple }" +
                "img { margin: auto; }" +
                //change to italic
                ".hint { margin-top: 15px; font-size: 10px; font-style: italic; display: inline; }" +
                ".hint-span { font-size: 11px; font-style: normal; }" +
                ".example { border: 1px solid #2a881b; color: #2a881b; margin: 10px 0; padding: 10px }" +
                ".example-input { border: 1px solid #2a881b; color: #2a881b; margin: 10px 0; padding: 10px }" +
                ".example-output { border: 1px solid #2a881b; color: #2a881b; margin: 10px 0; padding: 10px }" +
                "code { background-color: #2c2c2c; font-style: italic }" +
                "</style>";

        // html kódba foglalás
        String fullHtml = "<html><head>" + cssStyles + "</head><body>" + renderer.render(document) + "</body></html>";
        // kódrészletek "-el való kiemelése
        fullHtml = fullHtml.replace("<code>", "<code>\"").replace("</code>", "\"</code>");
        fullHtml = fullHtml.replace("<code>\"\"", "<code>\"").replace("\"\"</code>", "\"</code>"); // dupla "-ek eltávolítása

        // sima example kódok formázása
        fullHtml = fullHtml.replace("example\">", "example\"><i><strong>PÉLDA:</strong><br>");
        int exampleIndex = fullHtml.indexOf("example\">");
        while (exampleIndex != -1) {
            int exampleEndIndex = fullHtml.indexOf("</div>", exampleIndex);
            int lastItalicIndex = fullHtml.lastIndexOf("</i>", exampleEndIndex);
            if (lastItalicIndex == -1 || lastItalicIndex < exampleIndex) {
                fullHtml = fullHtml.substring(0, exampleEndIndex) + "</i></div>" + fullHtml.substring(exampleEndIndex);
            }
            exampleIndex = fullHtml.indexOf("example\">", exampleEndIndex);
        }

        // example-input kódok formázása
        fullHtml = fullHtml.replace("example-input\">", "example-input\"><i><strong>PÉLDA INPUT:</strong><br>");
        exampleIndex = fullHtml.indexOf("example-input\">");
        while (exampleIndex != -1) {
            int exampleEndIndex = fullHtml.indexOf("</div>", exampleIndex);
            int lastItalicIndex = fullHtml.lastIndexOf("</i>", exampleEndIndex);
            if (lastItalicIndex == -1 || lastItalicIndex < exampleIndex) {
                fullHtml = fullHtml.substring(0, exampleEndIndex) + "</i></div>" + fullHtml.substring(exampleEndIndex);
            }
            exampleIndex = fullHtml.indexOf("example-input\">", exampleEndIndex);
        }

        // example-output kódok formázása
        fullHtml = fullHtml.replace("example-output\">", "example-output\"><i><strong>PÉLDA OUTPUT:</strong><br>");
        exampleIndex = fullHtml.indexOf("example-output\">");
        while (exampleIndex != -1) {
            int exampleEndIndex = fullHtml.indexOf("</div>", exampleIndex);
            int lastItalicIndex = fullHtml.lastIndexOf("</i>", exampleEndIndex);
            if (lastItalicIndex == -1 || lastItalicIndex < exampleIndex) {
                fullHtml = fullHtml.substring(0, exampleEndIndex) + "</i></div>" + fullHtml.substring(exampleEndIndex);
            }
            exampleIndex = fullHtml.indexOf("example-output\">", exampleEndIndex);
        }

        // hint-ek megjelenítése
        fullHtml = fullHtml.replace("<div class=\"hint\">", "<div class=\"hint\"><span class=\"hint-span\">Hint: </span>");

        // collect all img src links in arraylist
        ArrayList<String> imgSrcLinks = new ArrayList<>();
        ArrayList<String> changedImgSrcLinks = new ArrayList<>();
        int imgIndex = fullHtml.indexOf("<img");
        while (imgIndex != -1) {
            int srcIndex = fullHtml.indexOf("src=\"", imgIndex);
            int srcEndIndex = fullHtml.indexOf("\"", srcIndex + 5);
            imgSrcLinks.add(fullHtml.substring(srcIndex + 5, srcEndIndex));
            imgIndex = fullHtml.indexOf("<img", srcEndIndex);
        }
        System.out.println("Img src links: " + imgSrcLinks);
        changedImgSrcLinks = convert32bitImageTo24Bit(imgSrcLinks);
        System.out.println("Img src links: " + imgSrcLinks);
        System.out.println("Changed img src links after conversion: " + changedImgSrcLinks);

        // replace img src links in html
        for (int i = 0; i < imgSrcLinks.size(); i++) {
            fullHtml = fullHtml.replace(imgSrcLinks.get(i), changedImgSrcLinks.get(i));
        }

        /* ez már a képek kirajzolására van az OOP-nél | egyelőre nem működik, mivel 32-es bitmélységű képeket nem tud megjeleníteni a JEditorPane */
        fullHtml = fullHtml.replace("width=\"50%\"", "");
        if (fullHtml.contains("class='story'")) {
            fullHtml = fullHtml.replace("img class='story'", "img class='story' width='512'");
            //System.err.println("img class='story' found");    // tesztelésre
        } else if (fullHtml.contains("class=\"story\"")) {
            fullHtml = fullHtml.replace("img class=\"story\"", "img class='story' width='512'");
            //System.err.println("img class=\"story\" found");  // tesztelésre
        }
//        //fullHtml = fullHtml.replace("src=\"https://inf.u-szeged.hu/~gmark/biro/prog1/gyak01_madar/img01.png\"", "src=\"file:C:\\Users\\Pali\\Downloads\\img01_24.png\"");
//        //fullHtml = fullHtml.replace("src=\"https://inf.u-szeged.hu/~gmark/biro/prog1/gyak01_madar/img01.png\"", "src=\"https://inf.u-szeged.hu/~gmark/biro/prog1/gyak01_madar/img01.jpg\"");

        System.out.println(fullHtml);
        return fullHtml;
        //return renderer.render(document);
    }

    private ArrayList<String> convert32bitImageTo24Bit(ArrayList<String> changedImgSrcLinks) {
        ArrayList<String> changedImgSrcLinksCopy = new ArrayList<>(changedImgSrcLinks);

        // projekt gyökér és temp mappa előkészítése csak egyszer
        Project[] open = ProjectManager.getInstance().getOpenProjects();
        if (open.length == 0) return new ArrayList<>();
        Project project = open[0];
        String tempDir = project.getBasePath() + File.separator + "biro-temp";
        File tempDirFile = new File(tempDir);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }

        for (int i = 0; i < changedImgSrcLinksCopy.size(); i++) {
            String imageUrl = changedImgSrcLinksCopy.get(i);
            try {
                // letöltés + konvertálás, visszakapott teljes elérési út
                String localPath = PNGConverter.downloadAndConvertImage(imageUrl, tempDir);

                // VFS-frissítés és VirtualFile lekérése
                File ioFile = new File(localPath);
                VirtualFile vf = LocalFileSystem.getInstance()
                        .refreshAndFindFileByIoFile(ioFile);
                if (vf != null) {
                    changedImgSrcLinksCopy.set(i, new File(localPath).toURI().toString().replace("file://", "file:"));//vf.getUrl().replace("file://", "file:"));   //vf IDE ERROR Occured lesz, ha indexing előtt bezárod a dialogot
                } else {
                    // ha nem találja, fallback a manuális file:// URL-re
                    changedImgSrcLinksCopy.set(i, new File(localPath).toURI().toString().replace("file://", "file:"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // hiba esetén az eredeti URL marad
            }
        }

        return changedImgSrcLinksCopy;
    }

    private Exercise loadExercise(JsonObject updatedExercise) {
        Exercise tmp = new Exercise();
        tmp.setAssignedExerciseId(checkJsonObjectIsNullInt(updatedExercise.get("assignedExerciseId")));
        tmp.setIndexInTaskList(checkJsonObjectIsNullInt(updatedExercise.get("indexInTaskList")));
        tmp.setType(checkJsonObjectIsNull(updatedExercise.get("type")));
        tmp.setName(checkJsonObjectIsNull(updatedExercise.get("name")));
        tmp.setDescription(checkJsonObjectIsNull(updatedExercise.get("description")));
        tmp.setDifficultyLevel(checkJsonObjectIsNullInt(updatedExercise.get("difficultyLevel")));
        tmp.setMaxScore(checkJsonObjectIsNullDouble(updatedExercise.get("maxScore")));
        tmp.setMinScore(checkJsonObjectIsNullDouble(updatedExercise.get("minScore")));
        tmp.setUploadLimit(checkJsonObjectIsNullInt(updatedExercise.get("uploadLimit")));
        tmp.setExpectedFileFormat(checkJsonObjectIsNull(updatedExercise.get("expectedFileFormat")));
        tmp.setTimeLimit(checkJsonObjectIsNullDouble(updatedExercise.get("timeLimit")));

        JsonArray starterFiles =
                updatedExercise.has("starterFiles") && !updatedExercise.get("starterFiles").isJsonNull()
                ? updatedExercise.get("starterFiles").getAsJsonArray()
                : new JsonArray();

        for (JsonElement je : starterFiles) {
            StarterFile starterFile = new StarterFile(
                    je.getAsJsonObject().get("starterFileId").getAsInt(),
                    je.getAsJsonObject().get("filename").getAsString(),
                    je.getAsJsonObject().get("viewable").getAsBoolean(),
                    je.getAsJsonObject().get("copyable").getAsBoolean(),
                    je.getAsJsonObject().get("downloadable").getAsBoolean()
            );
            tmp.addStarterFile(starterFile);
        }

        tmp.setScore(checkJsonObjectIsNullDouble(updatedExercise.get("score")));

        JsonArray submissions =
                updatedExercise.has("submissions") && !updatedExercise.get("submissions").isJsonNull()
                ? updatedExercise.get("submissions").getAsJsonArray()
                : new JsonArray();

        for (JsonElement je : submissions) {
            ArrayList<Evaluation> evaluations = new ArrayList<>();
            JsonArray evaluationArray = je.getAsJsonObject().get("evaluations").getAsJsonArray();
            for (JsonElement je2 : evaluationArray) {
                Evaluation evaluation = new Evaluation(
                        je2.getAsJsonObject().get("evaluationId").getAsInt(),
                        je2.getAsJsonObject().get("score").getAsDouble(),
                        je2.getAsJsonObject().get("message").getAsString(),
                        LocalDateTime.parse(je2.getAsJsonObject().get("evaluationTime").getAsString())
                );
                evaluations.add(evaluation);
            }

            Submission submission = new Submission(
                    je.getAsJsonObject().get("submissionId").getAsInt(),
                    je.getAsJsonObject().get("name").getAsString(),
                    je.getAsJsonObject().get("score").getAsDouble(),
                    je.getAsJsonObject().get("status").getAsString(),
                    LocalDateTime.parse(je.getAsJsonObject().get("submissionTime").getAsString()),
                    je.getAsJsonObject().get("ipAddress").getAsString(),
                    evaluations
            );
            tmp.addSubmission(submission);
        }
        return tmp;
    }

    private String checkJsonObjectIsNull(JsonElement je) {
        return (je != null && !je.isJsonNull()) ? je.getAsString() : "null";
    }

    private int checkJsonObjectIsNullInt(JsonElement je) {
        return (je != null && !je.isJsonNull()) ? je.getAsInt() : 0;
    }

    private double checkJsonObjectIsNullDouble(JsonElement je) {
        return (je != null && !je.isJsonNull()) ? je.getAsDouble() : 0.0;
    }

    private void showMainForm() {
        biroUIMainForm = new BiroUIMainForm(accessToken, refreshToken);
        JPanel parentPanel = (JPanel) mainPanel.getParent();
        parentPanel.removeAll();
        parentPanel.add(biroUIMainForm.getMainPanel());
        parentPanel.revalidate();
        parentPanel.repaint();
    }
}

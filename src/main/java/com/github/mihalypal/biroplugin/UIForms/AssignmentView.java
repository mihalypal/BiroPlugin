package com.github.mihalypal.biroplugin.UIForms;

import com.github.mihalypal.biroplugin.Services.FileDownloader;
import com.github.mihalypal.biroplugin.appearanceChanges.CustomButtonUI;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.html.HtmlRenderer;
import com.github.mihalypal.biroplugin.Services.UserServices;

import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

import javax.swing.*;
import java.awt.*;
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
    private String Assignment;
    private UserServices userServices;
    private ArrayList<JsonObject> exercises;
    private BiroUIMainForm biroUIMainForm;
    private JButton backToMainButton;
    private JPanel topPanel;
    private FileDownloader fileDownloader;

    public AssignmentView(String accessToken, String refreshToken, String Assignment) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.Assignment = Assignment;
        this.userServices = new UserServices();
        this.exercises = new ArrayList<>();


        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
        System.out.println("Assignment: " + Assignment);

        // get the concrete assignment
        JsonObject wholeAssignmentJson = JsonParser.parseString(Assignment).getAsJsonObject();
        String assignmentStudentId = wholeAssignmentJson.get("assignmentAssignedStudentId").getAsString();
        String currentAssignment = userServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/assignments/" + assignmentStudentId);
        System.out.println("Current assignment: " + currentAssignment);

        JsonObject exercisesOfTheAssignment = JsonParser.parseString(currentAssignment).getAsJsonObject();
        JsonArray exerciseStatuses = exercisesOfTheAssignment.get("exerciseStatuses").getAsJsonArray();
        System.out.println("Exercises of the assignment: " + exerciseStatuses);

        for (int i = 0; i < exerciseStatuses.size(); i++) {
            exercises.add(exerciseStatuses.get(i).getAsJsonObject());
        }

        mainPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel(new BorderLayout());

        JsonObject assignmentJson = JsonParser.parseString(Assignment).getAsJsonObject();
        String assignmentName = assignmentJson.get("assignmentName").getAsString();
        String assignmentDescription = assignmentJson.get("assignmentDescription").getAsString();
        System.out.println("Assignment name: " + assignmentName);
        System.out.println("Assignment description: " + assignmentDescription);

        assignmentNameLabel = new JLabel("  " + assignmentName + "  ");
        topPanel.add(assignmentNameLabel, BorderLayout.WEST);

        scoreOfOpenedExercise = new JLabel("", SwingConstants.CENTER);
        progressBar = new JProgressBar(0, 100);

        progressBar.setStringPainted(true);
        UIManager.put("ProgressBar.foreground", new Color(0, 255, 0)); // Kék progress sáv
        UIManager.put("ProgressBar.background", new Color(255, 0, 0)); // Világos szürke háttér
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK); // Szöveg színe
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE); // Szöveg háttérszíne


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
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Ide fog kerülni a fel- és letöltési lehetőség"));
        JButton testForDownload = new JButton("Letöltés");
        testForDownload.addActionListener(e -> {
            System.out.println("Letöltés gomb megnyomva");
            String downloadFileUrl = "https://biro3.inf.u-szeged.hu/api/v1/students/exercises/206370/starterfiles/555";
            try {
                UserServices.refreshToken(this.refreshToken);
                this.accessToken = UserServices.getAccessToken();
                fileDownloader.downloadFile(downloadFileUrl/*, "C:\\Users\\Pali\\Downloads"*/, this.accessToken);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        bottomPanel.add(testForDownload);
        bottomPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 200));
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(assignmentDescriptionPane), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);


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
    }

    private void displayExercise(int index) {
        JsonObject exercise = exercises.get(index);
        System.out.println("index: " + index);
        System.out.println("Exercise: " + exercise.get("assignedExerciseId").getAsString());
        JsonObject exerciseGetByAPI = JsonParser.parseString(userServices.callGetApi("https://biro3.inf.u-szeged.hu/api/v1/students/exercises/" + exercise.get("assignedExerciseId").getAsString())).getAsJsonObject();
        System.out.println("Exercise: " + exerciseGetByAPI.toString());

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
                ".hint { margin-top: 10px; font-size: 10px; font-style: italic; }" +
                ".example { border: 1px solid #2a881b; color: #2a881b; margin: 10px 0; padding: 10px }" +
                "code { background-color: #2c2c2c; font-style: italic }" +
                "</style>";

        String fullHtml = "<html><head>" + cssStyles + "</head><body>" + renderer.render(document) + "</body></html>";
        fullHtml = fullHtml.replace("<code>", "<code>\"").replace("</code>", "\"</code>");
        fullHtml = fullHtml.replace("example\">", "example\"><i><strong>Példa:</strong><br>");
        int exampleIndex = fullHtml.indexOf("example\">");
        while (exampleIndex != -1) {
            int exampleEndIndex = fullHtml.indexOf("</div>", exampleIndex);
            fullHtml = fullHtml.substring(0, exampleEndIndex) + "</i></div>" + fullHtml.substring(exampleEndIndex);
            exampleIndex = fullHtml.indexOf("example\">", exampleEndIndex);
        }

        System.out.println(fullHtml);
        return fullHtml;
        //return renderer.render(document);
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

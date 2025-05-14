package com.github.mihalypal.biroplugin.Dialog;

import com.github.mihalypal.biroplugin.Model.Exercise;
import com.github.mihalypal.biroplugin.Model.Submission;
import com.github.mihalypal.biroplugin.Services.ReportService;
import com.github.mihalypal.biroplugin.UIForms.AssignmentView;
import com.github.mihalypal.biroplugin.appearanceChanges.CustomPanelEditor;
import com.github.mihalypal.biroplugin.appearanceChanges.CustomPanelRenderer;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PreviousUploadsDialog extends DialogWrapper {
    private final ArrayList<Submission> submissions = AssignmentView.currentExercise.getSubmissions();
    private final Project project;
    private final Exercise exercise = AssignmentView.currentExercise;

    public PreviousUploadsDialog(@Nullable Project project, Exercise exercise) {
        super(project, null, false, IdeModalityType.MODELESS);
        this.project = project;
        init();
        setTitle("Korábbi Feltöltések (" + submissions.size() + " / " + exercise.getUploadLimit() + ") [" + exercise.getName() + "]");
        setSize(650, 410);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        String[] columnNames = {"Index", "Idő", "Név", "Pontszám", "Megoldás"};

        // 1) DefaultTableModel helyett override-olt osztály
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return col == 4 ? Submission.class : super.getColumnClass(col);
            }
        };

        // 2) Adatok feltöltése
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < submissions.size(); i++) {
            Submission s = submissions.get(i);
            // ugyanazt a panelt hozd létre itt, ahogy korábban
            /*JPanel solutionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton eyeButton = new JButton(AllIcons.Actions.Preview);
            JButton documentButton = new JButton(AllIcons.FileTypes.Any_type);
            // actionListenerek…
            solutionPanel.add(eyeButton);
            solutionPanel.add(documentButton);*/

            tableModel.addRow(new Object[]{
                    i + 1,
                    s.getSubmissionTime().format(fmt),
                    s.getName(),
                    s.getScore(),
                    s
            });
        }

        // 3) JTable és sor-magasság
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);

        // 4) Oszlopok beállítása
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        //table.getColumnModel().getColumn(4).setPreferredWidth(250);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(150);
        table.getColumnModel().getColumn(2).setMaxWidth(90);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        //table.getColumnModel().getColumn(4).setMaxWidth(250);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMinWidth(150);
        table.getColumnModel().getColumn(2).setMinWidth(90);
        table.getColumnModel().getColumn(3).setMinWidth(80);
        //table.getColumnModel().getColumn(4).setMinWidth(250);

        // 4) Renderer + Editor beállítása az 4. oszlopra (index 4)
        table.getColumnModel().getColumn(4)
                .setCellRenderer(new CustomPanelRenderer());
        table.getColumnModel().getColumn(4)
                .setCellEditor(new CustomPanelEditor(project));

        // 5) Végül scrollpane és kész
        return new JScrollPane(table);
    }

    @Override
    protected @Nullable JComponent createSouthPanel() {
        return null; // No additional buttons in the south panel
    }

    private String fetchSolution(int submissionId) {
        // Implement the HTTP request to fetch the solution text
        // Example: return SolutionService.fetchSolution(submissionId);
        return "Solution content for submission ID: " + submissionId; // Placeholder
    }
}
package com.github.mihalypal.biroplugin.appearanceChanges;

import com.github.mihalypal.biroplugin.Dialog.ReportDisplayDialog;
import com.github.mihalypal.biroplugin.Model.Exercise;
import com.github.mihalypal.biroplugin.Model.Submission;
import com.github.mihalypal.biroplugin.Services.LogSenderService;
import com.github.mihalypal.biroplugin.Services.ReportService;
import com.github.mihalypal.biroplugin.Services.UserServices;
import com.github.mihalypal.biroplugin.UIForms.AssignmentView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class CustomPanelEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel;
    private final JButton eyeBtn, docBtn;
    private Submission current;
    private final Project project;
    private final Exercise exercise = AssignmentView.currentExercise;

    public CustomPanelEditor(Project project) {
        this.project = project;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        eyeBtn = new JButton(AllIcons.Actions.Preview);
        docBtn = new JButton(AllIcons.FileTypes.Text);
        eyeBtn.setText("Megoldás");
        docBtn.setText("Riport");

        eyeBtn.addActionListener(e -> {
            String sol = ReportService.fetchSolution(current.getSubmissionId());
            new ReportDisplayDialog(project, sol, exercise.getName(), current.getName(), "Feltöltött Megoldás").show(); // TODO: jelezni a dialognak ha több fájl van
            if (UserServices.isLogSendingAccepted()) {
                if (sol != null && !sol.isEmpty()) {
                    LogSenderService.sendStatistic("Megoldás megtekintve: " + current.getName() + " / " + exercise.getSubmissions().size());
                } else {
                    LogSenderService.sendStatistic("[ÜRES MEGOLDÁS] Megoldás megtekintve: " + current.getName() + " / " + exercise.getSubmissions().size());
                }
            }
        });
        docBtn.addActionListener(e -> {
            int evalId = current.getEvaluations()
                    .get(current.getEvaluations().size()-1)
                    .getEvaluationId();
            String report = ReportService.fetchReport(evalId);
            if (UserServices.isLogSendingAccepted()) {
                if (report != null && !report.isEmpty()) {
                    LogSenderService.sendStatistic("Riport megtekintve: " + current.getName() + " / " + exercise.getSubmissions().size());
                } else {
                    LogSenderService.sendError("[ÜRES RIPORT] Riport megtekintve: " + current.getName() + " / " + exercise.getSubmissions().size());
                    report = "A riport megtekintése sikertelen."
                            + "\nNyisd meg újra a feladatot!"
                            + "\nEhhez elég csak a sorszámára kattintani és nyisd meg újra a korábbi feltöltések panelt."
                            + "\n\n\nA hiba megoldásán jelenleg is dolgozunk! Javítva lesz.";
                }
            }
            new ReportDisplayDialog(project, report, exercise.getName(), current.getName()).show();
        });

        panel.add(eyeBtn);
        panel.add(docBtn);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        // value itt már Submission
        this.current = (Submission) value;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return current;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;  // egy kattintásra is aktiválódjon
    }
}



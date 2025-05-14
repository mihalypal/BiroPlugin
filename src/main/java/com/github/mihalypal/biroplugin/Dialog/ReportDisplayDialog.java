package com.github.mihalypal.biroplugin.Dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ReportDisplayDialog extends DialogWrapper {
    private final String report;
    private JTextArea textArea;

    public ReportDisplayDialog(@Nullable Project project, String... report) {
        super(project, null, false, IdeModalityType.MODELESS);
        this.report = report[0];
        init();
        String title = "Értékelési Riport";
        String exerciseName = "";
        String uploadNumber = "";
        if (report.length > 2) {
            exerciseName = (report[1] != null) ? " [" + report[1] : "";
            uploadNumber = (report[2] != null) ? ": " + report[2] + "]" : "";
            if (report.length > 3) title = (report[3] != null) ? report[3] : "";
        }
        setTitle(title + exerciseName + uploadNumber);
        setSize(600, 400);
        //createCenterPanel();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea = new JTextArea(report);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 14));
        return new JScrollPane(textArea);
    }

    @Override
    protected @Nullable JComponent createSouthPanel() {
        // opcionálisan pakolhatsz ide Mentés gombot
        return null;
    }
}

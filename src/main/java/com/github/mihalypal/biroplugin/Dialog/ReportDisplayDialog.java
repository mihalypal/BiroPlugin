package com.github.mihalypal.biroplugin.Dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReportDisplayDialog extends DialogWrapper {
    private final String report;
    private JTextArea textArea;

    public ReportDisplayDialog(@Nullable Project project, String report) {
        super(project, null, false, IdeModalityType.MODELESS);
        this.report = report;
        init();
        setTitle("Értékelési riport");
        createCenterPanel();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea = new JTextArea(report);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return new JScrollPane(textArea);
    }

    @Override
    protected @Nullable JComponent createSouthPanel() {
        // opcionálisan pakolhatsz ide Mentés gombot
        return null;
    }
}

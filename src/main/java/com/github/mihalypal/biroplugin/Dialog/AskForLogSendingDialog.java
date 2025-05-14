package com.github.mihalypal.biroplugin.Dialog;

import com.github.mihalypal.biroplugin.Services.UserServices;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AskForLogSendingDialog extends DialogWrapper {
    private JCheckBox checkbox1;
    private JCheckBox checkbox2;

    public AskForLogSendingDialog(@Nullable Project project) {
        super(project);
        setTitle("Sikeres Bejelentkezés");
        setOKButtonText("OK");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        checkbox1 = new JCheckBox("Szeretném segíteni a fejlesztést a LOG-ok küldésével");
        checkbox2 = new JCheckBox("Szeretném, ha a LOG-ok küldése NEM anonim módon történne");

        panel.add(checkbox1);
        panel.add(checkbox2);

        return panel;
    }

    @Override
    protected void doOKAction() {
        // Update variables in the other class
        UserServices.setLogSendingAccepted(checkbox1.isSelected());
        UserServices.setLogSendingWithIdentifier(checkbox2.isSelected());
        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        // Optionally handle cancel action if needed
        super.doCancelAction();
    }

    public static void showDialog(@Nullable Project project) {
        AskForLogSendingDialog dialog = new AskForLogSendingDialog(project);
        dialog.show();
    }
}
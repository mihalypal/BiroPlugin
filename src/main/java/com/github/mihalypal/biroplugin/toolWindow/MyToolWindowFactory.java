package com.github.mihalypal.biroplugin.toolWindow;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class MyToolWindowFactory implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(MyToolWindowFactory.class);

    public MyToolWindowFactory() {
        LOG.warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.");
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        MyToolWindowUI myToolWindowUI = new MyToolWindowUI();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(myToolWindowUI.getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);

        // Set ToolWindows anchor to right
        //toolWindow.setTitle("Bíró Plugin");
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.getToolWindow("Bíró").setAnchor(com.intellij.openapi.wm.ToolWindowAnchor.RIGHT, null);
    }

    @Override
    public boolean shouldBeAvailable(Project project) {
        return true;
    }
}
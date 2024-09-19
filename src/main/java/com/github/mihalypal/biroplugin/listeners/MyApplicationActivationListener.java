package com.github.mihalypal.biroplugin.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;

public class MyApplicationActivationListener implements ApplicationActivationListener {

    private static final Logger logger = Logger.getInstance(MyApplicationActivationListener.class);

    @Override
    public void applicationActivated(IdeFrame ideFrame) {
        logger.warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.");
    }
}
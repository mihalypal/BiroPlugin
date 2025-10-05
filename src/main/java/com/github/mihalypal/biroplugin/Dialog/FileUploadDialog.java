package com.github.mihalypal.biroplugin.Dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FileUploadDialog extends DialogWrapper {
//    private final Project project;
//    private final CheckBoxList<VirtualFile> fileList;
//    private final List<VirtualFile> allFiles = new ArrayList<>();
//
//    public FileUploadDialog(@NotNull Project project) {
//        super(project);
//        this.project = project;
//        this.fileList = new CheckBoxList<>();       // com.intellij.ui.CheckBoxList
//        setTitle("Fájlok kiválasztása feltöltésre");
//        init();                                     // DialogWrapper életciklus indítása
//        loadProjectFiles();
//    }

//    private void loadProjectFiles() {
//        VirtualFile baseDir = project.getBaseDir();
//        VfsUtilCore.visitChildrenRecursively(baseDir, new VirtualFileVisitor<>() {
//            @Override
//            public boolean visitFile(@NotNull VirtualFile file) {
//                if (!file.isDirectory()
//                        && (file.getName().endsWith(".java") || file.getName().endsWith(".kt"))) {
//                    allFiles.add(file);
//                }
//                return true;
//            }
//        });
//
//        // items + címkék beállítása JetBrains Function-nel
//        fileList.setItems(allFiles, new Function<VirtualFile, String>() {
//            @Override
//            public String fun(VirtualFile file) {
//                return file.getName();
//            }
//        });
//    }

//    private void loadProjectFiles() {
//        VirtualFile baseDir = project.getBaseDir();
//        VfsUtilCore.visitChildrenRecursively(baseDir, new VirtualFileVisitor<>() {
//            @Override
//            public boolean visitFile(@NotNull VirtualFile file) {
//                if (!file.isDirectory()
//                        && (file.getName().endsWith(".java") || file.getName().endsWith(".kt"))) {
//                    allFiles.add(file);
//                }
//                return true;
//            }
//        });
//
//        // most már a csomagnev+fájlnév a label
//        fileList.setItems(allFiles, new Function<>() {
//            @Override
//            public String fun(VirtualFile file) {
//                return qualifiedName(file);
//            }
//        });
//    }
//
//    /** visszaadja: [package.]FileName */
//    private String qualifiedName(VirtualFile file) {
//        var psi = PsiManager.getInstance(project).findFile(file);
//        if (psi instanceof PsiJavaFile) {
//            String pkg = ((PsiJavaFile)psi).getPackageName();
//            return pkg.isEmpty()
//                    ? file.getName()
//                    : pkg + "." + file.getName();
//        }
//        // ha .kt, vagy nem Java-fájl, csak név
//        return file.getName();
//    }
//
//    @Nullable
//    @Override
//    protected JComponent createCenterPanel() {
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        panel.add(new JLabel("Válaszd ki a feltöltendő fájlokat:"), BorderLayout.NORTH);
//
//        JBScrollPane scrollPane = new JBScrollPane(fileList);
//        scrollPane.setPreferredSize(new Dimension(450, 300));
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        return panel;
//    }
//
//    /** Visszaadja a felhasználó által kipipált fájlokat */
//    @NotNull
//    public List<VirtualFile> getSelectedFiles() {
//        List<VirtualFile> selected = new ArrayList<>();
//        for (int i = 0; i < allFiles.size(); i++) {
//            if (fileList.isItemSelected(i)) {
//                selected.add(allFiles.get(i));
//            }
//        }
//        return selected;
//    }

    private final Project project;
    private final CheckboxTree tree;
    private final CheckedTreeNode rootNode;

    public FileUploadDialog(@NotNull Project project, String packageName) {
        super(project);
        this.project = project;
        setTitle("Fájlok Kiválasztása Feltöltésre");

        // 1) Root node
        rootNode = new CheckedTreeNode("root");

        // 2) Feltöltjük a csomag–fájl struktúrát
        Map<String, List<VirtualFile>> byPackage = collectFilesByPackage();
        for (String pkg : new TreeSet<>(byPackage.keySet())) {
            CheckedTreeNode pkgNode = new CheckedTreeNode(pkg.isEmpty() ? "<default>" : pkg);
            for (VirtualFile file : byPackage.get(pkg)) {
                pkgNode.add(new CheckedTreeNode(file));
            }
            rootNode.add(pkgNode);
        }

        // 3) CheckboxTree létrehozása
        tree = new CheckboxTree(new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(@NotNull JTree tree,
                                          Object value,
                                          boolean selected,
                                          boolean expanded,
                                          boolean leaf,
                                          int row,
                                          boolean hasFocus) {
                CheckedTreeNode node = (CheckedTreeNode) value;
                Object obj = node.getUserObject();
//                String text = obj instanceof VirtualFile
//                        ? ((VirtualFile) obj).getName()
//                        : obj.toString();
//                // a checkbox textjét így állítjuk be, nincs külön paraméter
//                getCheckbox().setText(text);
                getCheckbox().setText(obj instanceof VirtualFile
                        ? ((VirtualFile) obj).getName()
                        : obj.toString());
            }
        }, rootNode);

        // 4) **Minden nód alapértelmezésben „unchecked”** →
//        for (Enumeration<TreeNode> en = rootNode.breadthFirstEnumeration(); en.hasMoreElements();) {
//            CheckedTreeNode node = (CheckedTreeNode) en.nextElement();
//            node.setChecked(false);
//        }
        for (Enumeration<TreeNode> it = rootNode.breadthFirstEnumeration(); it.hasMoreElements(); ) {
            ((CheckedTreeNode) it.nextElement()).setChecked(false);
        }

        checkPackageNode(packageName);

        init();            // DialogWrapper init
    }

    // pipálja az aktuális package-et
    public void checkPackageNode(String packageName) {
        for (Enumeration<TreeNode> it = rootNode.breadthFirstEnumeration(); it.hasMoreElements(); ) {
            CheckedTreeNode node = (CheckedTreeNode) it.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof String && userObject.equals(packageName)) {
                node.setChecked(true);
                break; // Stop after finding the matching package
            }
        }
    }

    /** Visszaadja a package→fájlok map-et */
//    private Map<String, List<VirtualFile>> collectFilesByPackage() {
//        Map<String,List<VirtualFile>> map = new HashMap<>();
//        VfsUtilCore.visitChildrenRecursively(project.getBaseDir(), new VirtualFileVisitor<>() {
//            @Override
//            public boolean visitFile(@NotNull VirtualFile file) {
//                System.out.println("File: " + file.getName());
//                if (!file.isDirectory()
//                        && (file.getName().endsWith(".java") || file.getName().endsWith(".kt") || file.getName().endsWith(".zip"))) {
//                    // csomagnev lekérése Java-fájl esetén
//                    String pkg = "";
//                    var psi = PsiManager.getInstance(project).findFile(file);
//                    if (psi instanceof PsiJavaFile) {
//                        pkg = ((PsiJavaFile) psi).getPackageName();
//                    }
//                    map.computeIfAbsent(pkg, __ -> new ArrayList<>()).add(file);
//                }
//                return true;
//            }
//        });
//        return map;
//    }

    /** File-ok összegyűjtése a project gyökerétől, package és zip kezeléssel */
    private Map<String, List<VirtualFile>> collectFilesByPackage() {
        Map<String, List<VirtualFile>> map = new HashMap<>();
        ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile baseDir = project.getBaseDir();

        // skippelt mappák
        Set<String> skipDirs = Set.of("out", "target",".get", ".idea");

        VfsUtilCore.visitChildrenRecursively(baseDir, new VirtualFileVisitor<>() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                if (file.isDirectory()) {
                    return !skipDirs.contains(file.getName());
                }
                String name = file.getName();
                // Csak .java, .kt és .zip fájlok
                if (!(name.endsWith(".java") || name.endsWith(".kt") || name.endsWith(".zip"))) {
                    return true;
                }
                String pkg = "";
                if (name.endsWith(".java") || name.endsWith(".kt")) {
                    var psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof PsiJavaFile) {
                        pkg = ((PsiJavaFile) psiFile).getPackageName();
                    }
                } else {
                    // .zip esetén: keressük a source root-ot (vagy resources root-ot)
                    VirtualFile srcRoot = index.getSourceRootForFile(file);
                    if (srcRoot == null) {
                        srcRoot = index.getContentRootForFile(file);
                    }
                    // ha találtunk root-ot, abból számoljuk a relatív útvonalat
                    if (srcRoot != null) {
                        String rel = VfsUtilCore.getRelativePath(file.getParent(), srcRoot, '/');
                        if (rel != null) {
                            pkg = rel.replace('/', '.');
                        }
                    }
                }
                map.computeIfAbsent(pkg, k -> new ArrayList<>()).add(file);
                return true;
            }
        });
        return map;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.add(new JLabel("Válaszd ki a feltölteni kívánt fájlokat:"), BorderLayout.NORTH);
        JBScrollPane scroll = new JBScrollPane(tree);
        scroll.setPreferredSize(new Dimension(500, 350));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** A user által kipipált VirtualFile-ok lekérése */
    @NotNull
    public List<VirtualFile> getSelectedFiles() {
        // CheckboxTree.getCheckedNodes(osztály) adja vissza a userObject-eket
        VirtualFile[] checked = tree.getCheckedNodes(VirtualFile.class, null);
        return Arrays.asList(checked);
    }
}

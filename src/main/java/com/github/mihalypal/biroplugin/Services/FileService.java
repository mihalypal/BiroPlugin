//package com.github.mihalypal.biroplugin.Services;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class FileDownloader {
//
//    public static void downloadFile(String fileURL, String saveDir, String accessToken, String fileName) throws IOException {
//        URL url = new URL(fileURL);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
//        httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
//        httpConn.setRequestProperty("Accept-Language", "hu");
//        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
//        httpConn.setRequestProperty("Connection", "keep-alive");
//        int responseCode = httpConn.getResponseCode();
//
//        // Check HTTP response code
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            // Open input stream from the HTTP connection
//            try (InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
//                 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveDir + File.separator + fileName))) {
//
//                int bytesRead;
//                byte[] buffer = new byte[4096];
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//
//                System.out.println("File downloaded: " + saveDir + File.separator + fileName);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw e;
//            }
//        } else {
//            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
//        }
//        httpConn.disconnect();
//        System.out.println("Disconnected from server.");
//    }
//}

//package com.github.mihalypal.biroplugin.Services;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Base64;
//
//public class FileDownloader {
//
//    public static void downloadFile(String fileURL, String saveDir, String accessToken) throws IOException {
//
//        URL url = new URL(fileURL);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
//        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
//        httpConn.setRequestMethod("GET");
//
//        int responseCode = httpConn.getResponseCode();
//
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            // JSON feldolgozása
//            String jsonResponse = response.toString();
//            System.out.println("Received JSON: " + jsonResponse);
//
//            // Fájl nevének kinyerése
//            String fileName = extractJsonValue(jsonResponse, "filename");
//            String fileContentBase64 = extractJsonValue(jsonResponse, "content");
//
//            if (fileName == null || fileContentBase64 == null) {
//                System.out.println("Error: JSON response is missing required fields.");
//                return;
//            }
//
//            // Fájl dekódolása és mentése
//            byte[] fileData = Base64.getDecoder().decode(fileContentBase64);
//            File outputFile = new File(saveDir, fileName);
//
//            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
//                fos.write(fileData);
//            }
//
//            System.out.println("File saved successfully: " + outputFile.getAbsolutePath());
//        } else {
//            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
//        }
//        httpConn.disconnect();
//        System.out.println("Disconnected from server.");
//    }
//
//    // JSON-ból adott kulcs értékének kinyerése egyszerű regex segítségével
//    private static String extractJsonValue(String json, String key) {
//        String pattern = "\"" + key + "\"\\s*:\\s*\"(.*?)\"";
//        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
//        java.util.regex.Matcher matcher = regex.matcher(json);
//        return matcher.find() ? matcher.group(1) : null;
//    }
//}

package com.github.mihalypal.biroplugin.Services;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileService {

    /**
     * Ez a metódus végzi el a paraméterben kapott fájlok feltöltését a Bíró3 kiértékelőjébe.
     *
     * @param files
     * @param accessToken
     * @throws IOException
     */
    public static void uploadFile(String[] files, String accessToken) throws IOException {
        // TODO: egyelőre mindenképpen void, később meglátjuk kell-e valamit visszaadni
    }

    /**
     * Letölt egy fájlt az API-ról és menti a projekt `src` mappájába vagy ha az nincs, akkor a `biro_files` mappába.
     *
     * @param fileURL     A fájl URL-je.
     * @param accessToken Az API hozzáférési token.
     * @throws IOException Ha a letöltés vagy mentés közben hiba lép fel.
     */
    public static void downloadFile(String fileURL, String accessToken, String assignmentName, int exerciseIndex) throws IOException {
        /* // ez a modul generálós kód megoldása, viszont ez még nem működik megfelelően teljesen, marad kommentben, később ebből ki lehet indulni
        Project project = ProjectManager.getInstance().getOpenProjects()[0]; // az első megnyitott projekt
        if (project == null || project.getBasePath() == null) {
            System.out.println("No open project found.");
            return;
        }

        String projectBasePath = project.getBasePath();
        String moduleName = normalizeModuleName(assignmentName, exerciseIndex); // _7_az_elet_faja_feladat_01

        // Modul mappa
        File moduleDir = new File(projectBasePath, moduleName);

        // Modul létrehozása, ha még nincs
        if (!moduleDir.exists()) {
            createModuleForExercise(project, assignmentName, exerciseIndex);
        }

        // src mappa a modulban
        File srcDir = new File(moduleDir, "src");
        if (!srcDir.exists()) {
            boolean created = srcDir.mkdirs();
            if (!created) {
                System.err.println("Nem sikerült létrehozni a src mappát: " + srcDir.getAbsolutePath());
                return;
            }
        }

        // Fájl lekérése
        String jsonResponse = fetchFileData(fileURL, accessToken);
        if (jsonResponse == null) {
            System.out.println("Failed to fetch file from server.");
            return;
        }

        String fileName = extractJsonValue(jsonResponse, "filename");
        String fileContentBase64 = extractJsonValue(jsonResponse, "content");
        if (fileName == null || fileContentBase64 == null) {
            System.out.println("Invalid JSON received.");
            return;
        }

        File outputFile = getUniqueFileName(srcDir, fileName);

        byte[] decodedBytes = Base64.getDecoder().decode(fileContentBase64);

        // Most NEM adunk hozzá package deklarációt!
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(decodedBytes);
        }

        // Projekt struktúra frissítés
        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputFile);
        if (vf != null) {
            vf.refresh(false, false);
        }

        System.out.println("File saved to module: " + moduleName + " → " + outputFile.getName());*/

        // Package-be rendezős megoldás
        // Lekérjük az aktuális projektet
        Project[] open = ProjectManager.getInstance().getOpenProjects();
        if (open.length == 0) return;
        Project project = open[0];
        if (project == null) {
            System.out.println("No open project found.");
            return;
        }

        // A projekt gyökérmappájának lekérése
        String projectBasePath = project.getBasePath();
        if (projectBasePath == null) {
            System.out.println("Project base path not found.");
            return;
        }

        // feladatsor és feladat mappák nevének generálása
        String assignmentFolder = normalizeName(assignmentName);
        if (!assignmentFolder.startsWith("_")) {
            assignmentFolder = "_" + assignmentFolder;
        }
        String exerciseFolder = "feladat_" + String.format("%02d", exerciseIndex + 1);

        // Mappa: src/_assignment/feladat_xx
        File targetDir = new File(projectBasePath + File.separator + "src", assignmentFolder + File.separator + exerciseFolder);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            System.err.println("Nem sikerült létrehozni a mappát: " + targetDir.getAbsolutePath());
            return;
        }

        // Fájl lekérése
        String jsonResponse = fetchFileData(fileURL, accessToken);
        if (jsonResponse == null) {
            System.out.println("Failed to fetch file.");
            return;
        }

        String fileName = extractJsonValue(jsonResponse, "filename");
        String fileContentBase64 = extractJsonValue(jsonResponse, "content");
        if (fileName == null || fileContentBase64 == null) {
            System.out.println("Invalid JSON data.");
            return;
        }

        // Fájlnév ellenőrzés, egyediség
        File outputFile = getUniqueFileName(targetDir, fileName);

        // Tartalom dekódolása és package sor beszúrása
        byte[] decodedBytes = Base64.getDecoder().decode(fileContentBase64);
        String fileContent = new String(decodedBytes, StandardCharsets.UTF_8);

        if (fileName.endsWith(".java")) {
            String pluginSpecificThingsStart = """
                    // Generated by BiroPlugin
                    // Do not edit this section manually!
                    // This section is automatically generated and removed during upload.
                    """;
            String pluginSpecificThingsEnd = """
                    // This section should be removed if you upload the solution directly to the Bíró3 website.
                    // End of generated section
                    
                    """;
            String packageLine = "package " + assignmentFolder + "." + exerciseFolder + ";\n";
            fileContent = pluginSpecificThingsStart + packageLine + pluginSpecificThingsEnd + fileContent;
        }

        // Fájl kiírása
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(fileContent.getBytes(StandardCharsets.UTF_8));
        }

        // projekt struktúra frissítés, hogy megjelenjenek a letöltött fájlok ---- Erre IDE Error Occured lesz
//        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputFile);
//        if (vf != null) {
//            vf.refresh(false, false); // vagy true,true ha mappa
//        }
        LocalFileSystem lfs = LocalFileSystem.getInstance();
        // ez visszaadja, vagy létrehozza a VirtualFile-t, de NEM schedule-ol UI-t
        VirtualFile vf2 = lfs.refreshAndFindFileByIoFile(outputFile);
        if (vf2 != null) {
            // synchron módon dirt-eljük és refresh-eljük, explicit false-as async paraméterrel
            VfsUtil.markDirtyAndRefresh(
                    false,   // async = false → nem invokeLater
                    false,   // recursive = false → csak ezt a fájlt
                    false,   // reloadChildren (nem könyvtár), false
                    vf2
            );
        }

        System.out.println("File saved: " + outputFile.getAbsolutePath());
/*
        // A `src` mappa elérési útja
        File srcDir = new File(projectBasePath, "src");
        if (!srcDir.exists()) {

            // Ha nincs src mappa, akkor valószínűleg nem egy Java projekt, szóval biro_files mappába mentjük
            srcDir = new File(projectBasePath, "biro_files");

            boolean created = true;
            if (!srcDir.exists())
                created = srcDir.mkdirs(); // Ha nem létezik, létrehozzuk
            if (!created) {
                System.out.println("Failed to create src directory.");
                return;
            }
        }

        // Letöltés HTTP-n keresztül
        String jsonResponse = fetchFileData(fileURL, accessToken);
        if (jsonResponse == null) {
            System.out.println("Failed to fetch file data.");
            return;
        }

        // JSON feldolgozása
        String fileName = extractJsonValue(jsonResponse, "filename");
        String fileContentBase64 = extractJsonValue(jsonResponse, "content");

        if (fileName == null || fileContentBase64 == null) {
            System.out.println("Invalid JSON response.");
            return;
        }

        // Egyedi fájlnév generálása, ha már létezik
        File outputFile = getUniqueFileName(srcDir, fileName);

        // Fájl dekódolása és mentése a `src` mappába
        byte[] fileData = Base64.getDecoder().decode(fileContentBase64);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(fileData);
        }

        System.out.println("File saved successfully to: " + outputFile.getAbsolutePath());*/
    }

    // Segédfüggvény JSON letöltésére az API-ról
    private static String fetchFileData(String fileURL, String accessToken) throws IOException {
        java.net.URL url = new java.net.URL(fileURL);
        java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Accept", "application/json, text/plain, */*");
        httpConn.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpConn.setRequestMethod("GET");

        int responseCode = httpConn.getResponseCode();
        if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
            System.out.println("Failed to download file. HTTP Code: " + responseCode);
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        httpConn.disconnect();

        return response.toString();
    }

    // JSON-ból adott kulcs értékének kinyerése
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"(.*?)\"";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    // Ha a fájl már létezik, generál egy új nevet sorszámozással
    private static File getUniqueFileName(File directory, String originalFileName) {
        File file = new File(directory, originalFileName);
        System.out.println("File: " + file.getAbsolutePath());
        if (!file.exists()) {
            System.out.println("File: " + directory + " : " + originalFileName);
            return file; // Ha nincs ilyen fájl, akkor ezt használjuk
        }

        // Fájlnév kiterjesztés kezelése
        String name = originalFileName;
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            name = originalFileName.substring(0, dotIndex);
            extension = originalFileName.substring(dotIndex);
        }

        // Keressük meg az első szabad sorszámot
        int counter = 1;
        while (file.exists()) {
            file = new File(directory, name + " (" + counter + ")" + extension);
            counter++;
        }
        return file;
    }

    /**
     * Normalizálja a fájlnevet, eltávolítja a pontokat, szóközöket és ékezetes karaktereket.
     *
     * @param name A fájl neve.
     * @return A normalizált fájlnév.
     */
    private static String normalizeName(String name) {
        String base = name.toLowerCase()
                .replace(".", "")     // pont eltávolítása
                .replace(" ", "_")
                .replace("-", "_");
        String normalized = java.text.Normalizer.normalize(base, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "").replaceAll("[^a-z0-9_]", "");
    }

    private static String normalizeModuleName(String assignmentName, int exerciseIndex) {
        String base = assignmentName.toLowerCase()
                .replace(".", "")
                .replace(" ", "_")
                .replace("-", "_");
        String normalized = java.text.Normalizer.normalize(base, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-z0-9_]", "");
        return "_" + normalized + "_feladat_" + String.format("%02d", exerciseIndex + 1);
    }

    public static void createModuleForExercise(Project project, String assignmentName, int exerciseIndex) {
        String projectBasePath = project.getBasePath();
        if (projectBasePath == null) {
            System.err.println("No project base path found.");
            return;
        }

        String moduleName = normalizeModuleName(assignmentName, exerciseIndex);
        File moduleDir = new File(projectBasePath, moduleName);

        if (!moduleDir.exists()) {
            boolean created = moduleDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create module directory: " + moduleDir.getAbsolutePath());
                return;
            }
        }

        File imlFile = new File(moduleDir, moduleName + ".iml");
        if (!imlFile.exists()) {
            try (FileWriter writer = new FileWriter(imlFile)) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<module type=\"JAVA_MODULE\" version=\"4\">\n" +
                        "  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">\n" +
                        "    <exclude-output />\n" +
                        "    <content url=\"file://$MODULE_DIR$\">\n" +
                        "      <sourceFolder url=\"file://$MODULE_DIR$/src\" isTestSource=\"false\" />\n" +
                        "    </content>\n" +
                        "    <orderEntry type=\"inheritedJdk\" />\n" +
                        "    <orderEntry type=\"sourceFolder\" forTests=\"false\" />\n" +
                        "  </component>\n" +
                        "</module>");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create .iml file manually: " + imlFile.getAbsolutePath());
            }
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                // Modul létrehozása
                ModuleManager moduleManager = ModuleManager.getInstance(project);
                ModifiableModuleModel moduleModel = moduleManager.getModifiableModel();
                Module module = moduleModel.newModule(
                        moduleDir.getAbsolutePath() + File.separator + moduleName + ".iml",
                        JavaModuleType.getModuleType().getId()
                );
                moduleModel.commit();

                // src mappa létrehozása
                File srcDir = new File(moduleDir, "src");
                if (!srcDir.exists()) {
                    boolean srcCreated = srcDir.mkdirs();
                    if (!srcCreated) {
                        System.err.println("Failed to create src directory: " + srcDir.getAbsolutePath());
                        return;
                    }
                }

                // Source root beállítása
                VirtualFile virtualSrcDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(srcDir);
                if (virtualSrcDir != null) {
                    ModuleRootModificationUtil.updateModel(module, model -> {
                        model.addContentEntry(virtualSrcDir).addSourceFolder(virtualSrcDir, false);
                    });
                } else {
                    System.err.println("Failed to find virtual file for src directory: " + srcDir.getAbsolutePath());
                }

                System.out.println("Module created and src set as source root: " + moduleName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}

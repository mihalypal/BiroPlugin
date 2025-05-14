package com.github.mihalypal.biroplugin.Services;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.batik.transcoder.TranscoderInput;

import static org.apache.batik.transcoder.image.ImageTranscoder.KEY_BACKGROUND_COLOR;

public class PNGConverter {

    /**
     * Letölt egy PNG képet URL-ről, konvertálja 24 bites formátumba és elmenti a megadott helyre.
     *
     * @param imageUrl       A kép URL-je.
     * @param outputDirPath Az elmentett kép elérési útja.
     * @throws IOException Ha a letöltés vagy mentés közben hiba lép fel.
     */
    public static String downloadAndConvertImage(String imageUrl, String outputDirPath) throws IOException {
        // letöltjük a kép nevét URL-ből
        String[] urlParts = new URL(imageUrl).getPath().split("/");
        String fileName = urlParts[urlParts.length - 1];
        String baseName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;

        // létrehozzuk a célkönyvtárat, ha még nincs
        File outDir = new File(outputDirPath);
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new IOException("Nem sikerült létrehozni a könyvtárat: " + outputDirPath);
        }

        // beállítjuk a teljes kimeneti fájlnevet .jpg-re
        /*String outputFilePath = outputDirPath
                + File.separator
                + baseName
                + ".jpg";*/
        String outputFilePath = outputDirPath + File.separator + fileName;
        File outFile = new File(outputFilePath);

        // gif-ek
        if (imageUrl.toLowerCase().endsWith(".gif")) {
            // Save the .gif file directly
            try (InputStream inputStream = new URL(imageUrl).openStream()) {
                java.nio.file.Files.copy(inputStream, outFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return outFile.getAbsolutePath();
        }

        // beolvassuk az eredeti képet (PNG, JPG, stb.)
        BufferedImage original = null;
        if (imageUrl.toLowerCase().endsWith(".svg")) {
            // SVG esetén Batik-kód
            try (InputStream svgStream = new URL(imageUrl).openStream()) {
                String svg = new String(svgStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replaceAll("(?i)fill\\s*=\\s*\"transparent\"", "fill=\"none\"");
                TranscoderInput input = new TranscoderInput(new StringReader(svg));
                BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
                transcoder.addTranscodingHint(KEY_BACKGROUND_COLOR, Color.WHITE);
                transcoder.transcode(input, null);
                original = transcoder.getBufferedImage();
            } catch (Exception e) {
                throw new IOException("SVG feldolgozása sikertelen: " + imageUrl, e);
            }
        } else {
            original = ImageIO.read(new URL(imageUrl));
        }

        if (original == null) {
            throw new IOException("A kép nem tölthető be: " + imageUrl);
        }

        // átalakítás 24-bites RGB-re, fehér háttérrel
        BufferedImage converted = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = converted.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, converted.getWidth(), converted.getHeight());
        g.drawImage(original, 0, 0, null);
        g.dispose();

        // JPG mentése
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outFile)) {
            if (!ImageIO.write(converted, "jpg", ios)) {
                throw new IOException("A kép mentése sikertelen: " + outputFilePath);
            }
        }

        return outFile.getAbsolutePath();
    }


//    public static void main(String[] args) {
//        String imageUrl = "https://example.com/path/to/your/image.png";
//        String outputFilePath = "converted_image.png";
//
//        try {
//            downloadAndConvertImage(imageUrl, outputFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}


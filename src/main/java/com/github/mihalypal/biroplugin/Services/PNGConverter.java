package com.github.mihalypal.biroplugin.Services;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.intellij.ui.JBColor;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import com.github.mihalypal.biroplugin.Services.BufferedImageTranscoder;

import static org.apache.batik.transcoder.image.ImageTranscoder.KEY_BACKGROUND_COLOR;

public class PNGConverter {

    /**
     * Letölt egy PNG képet URL-ről, konvertálja 24 bites formátumba és elmenti a megadott helyre.
     *
     * @param imageUrl       A kép URL-je.
     * @param outputFilePath Az elmentett kép elérési útja.
     * @throws IOException Ha a letöltés vagy mentés közben hiba lép fel.
     */
    public static String downloadAndConvertImage(String imageUrl, String outputFilePath) throws IOException {
        //System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
        String[] imageUrlParts = imageUrl.split("/");
        String imageName = imageUrlParts[imageUrlParts.length - 1];
        imageUrlParts = imageName.split("\\.");   // Kép kiterjesztésének leválasztása
        outputFilePath = outputFilePath + "\\" + imageUrlParts[0] + ".jpg"; // Kép elérési útja és neve

        //BufferedImage originalImage;
        if (imageUrl.endsWith(".svg")) {
            // Handle SVG conversion
            try {
                // SVG tartalom beolvasása stringként
                InputStream urlStream = new URL(imageUrl).openStream();
                String svgText = new String(urlStream.readAllBytes(), StandardCharsets.UTF_8);
                urlStream.close();

                // "transparent" -> "none" csere || Erre azért van szükség, mert a validálásnál az SVG kódja hibás
                svgText = svgText.replaceAll("(?i)fill\\s*=\\s*\"transparent\"", "fill=\"none\"");

                // Batik transzkódolás string inputból
                TranscoderInput input = new TranscoderInput(new StringReader(svgText));
                BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
                transcoder.addTranscodingHint(KEY_BACKGROUND_COLOR, Color.WHITE);
                transcoder.transcode(input, null);
                originalImage = transcoder.getBufferedImage();
            } catch (Exception e) {
                throw new IOException("Failed to process SVG image: " + imageUrl, e);
            }
        } else {
            // Handle other image formats
            originalImage = ImageIO.read(new URL(imageUrl));
        }

        if (originalImage == null) {
            throw new IOException("A kép nem tölthető be a megadott URL-ről: " + imageUrl);
        }

        // 24 bites RGB képre konvertálás (az átlátszóság eltávolítása)
        BufferedImage convertedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = convertedImage.createGraphics();
        g.setColor(Color.WHITE); // Fehér háttér, hogy az átlátszó részek ne legyenek feketék
        g.fillRect(0, 0, convertedImage.getWidth(), convertedImage.getHeight());
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // Kép mentése 24 bites JPG formátumban
        boolean success = ImageIO.write(convertedImage, "jpg", new File(outputFilePath));
        if (!success) {
            throw new IOException("A kép mentése sikertelen: " + outputFilePath);
        }

        System.out.println("Kép sikeresen elmentve: " + outputFilePath);

        return outputFilePath;
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


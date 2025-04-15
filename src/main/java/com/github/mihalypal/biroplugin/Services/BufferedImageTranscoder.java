package com.github.mihalypal.biroplugin.Services;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.TranscoderOutput;
import java.awt.image.BufferedImage;

import javax.xml.parsers.SAXParserFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.transcoder.TranscoderInput;

public class BufferedImageTranscoder extends ImageTranscoder {
    private BufferedImage bufferedImage;

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) {
        this.bufferedImage = img;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    static {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
    }
}

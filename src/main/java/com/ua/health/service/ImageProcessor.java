package com.ua.health.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;


public class ImageProcessor {
    public static void main(String[] args) {
        ImageProcessor imageProcessor = new ImageProcessor();
        try {
            imageProcessor.processImage("src/main/resources/IMG_0446.jpg");
            System.out.println();
            imageProcessor.processImage("src/main/resources/IMG_0445.jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static {
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
    }
    /**
     * Process an image at the specified path.
     *
     * @param  imagePath	the path to the image file
     * @throws IOException	if an I/O error occurs
     */

    public void processImage(String imagePath) throws IOException {
        File f = new File(imagePath);
        BufferedImage ipimage = ImageIO.read(f);
        double d = ipimage.getRGB(ipimage.getTileWidth() / 2, ipimage.getTileHeight() / 2);

        float scaleFactor = getScaleFactor(d);
        float offset = getOffset(d);

        BufferedImage processedImage = process(ipimage, scaleFactor, offset);
        String result = null;
        try {
            result = extractText(processedImage);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }


        System.out.println(result);
    }
    /**
     * Process the input image with a given scale factor and offset.
     *
     * @param  ipimage      the input BufferedImage
     * @param  scaleFactor  the scale factor for image processing
     * @param  offset       the offset for image processing
     * @return             the processed BufferedImage
     */
    private BufferedImage process(BufferedImage ipimage, float scaleFactor, float offset) {
        BufferedImage opimage = new BufferedImage(1500, 1500, ipimage.getType());
        if (ipimage.getHeight() < 1500 && ipimage.getWidth() < 1500) {
            opimage = new BufferedImage(ipimage.getWidth(), ipimage.getHeight(), ipimage.getType());
        }
        Graphics2D graphic = opimage.createGraphics();
        graphic.drawImage(ipimage, 0, 0, ipimage.getWidth(), ipimage.getHeight(), null);
        graphic.dispose();

        RescaleOp rescale = new RescaleOp(scaleFactor, offset, null);
        return rescale.filter(opimage, null);
    }
    /**
     * A method to extract text from a BufferedImage using Tesseract OCR.
     *
     * @param  image   the BufferedImage from which text will be extracted
     * @return         the extracted and processed text
     */
    private String extractText(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("ukr");
        tesseract.setDatapath("/Users/mac/Downloads/Tess4J/tessdata");
        String str = null;
        try {
            str = tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
        String strip = str.strip();
        return strip.replaceAll("\\n{2,}", "\n");
    }
    /**
     * Returns the scale factor based on the input double value.
     *
     * @param  d  the input double value
     * @return     the scale factor based on the input value
     */
    private float getScaleFactor(double d) {
        return switch ((int) d) {
            case -1 -> 1f;
            case -257 -> 1.19f;
            case -1907998 -> 1.35f;
            case -7254228 -> 1.455f;
            case -14211511 -> 3f;
            default -> 1f;
        };
    }
    /**
     * Returns the offset based on the input double value.
     *
     * @param  d  the input double value
     * @return    the offset value based on the input
     */

    private float getOffset(double d) {
        return switch ((int) d) {
            case -1 -> 0.35f;
            case -257, -1907998 -> 0.5f;
            case -7254228 -> -47f;
            case -14211511 -> -10f;
            default -> 0.5f;
        };
    }
}

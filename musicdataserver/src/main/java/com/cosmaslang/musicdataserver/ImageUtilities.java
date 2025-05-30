package com.cosmaslang.musicdataserver;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.*;

public class ImageUtilities {

    public static final int MAX_WIDTH = 240;
    public static final String OUTPUT_IMAGE_FORMAT = "gif";

    /**
     * Generates a scaled thumbnail image from the given input (byte array or file).
     * Returns null if scaling is not necessary.
     *
     * @param input The image input (byte array or file).
     * @return Scaled image as byte[] or null if no scaling is needed.
     */
    public static byte @Nullable [] buildThumbnail(Object input) throws IOException, ImagingOpException {
        BufferedImage originalImage;

        if (input instanceof byte[]) {
            originalImage = readImageFromStream(new ByteArrayInputStream((byte[]) input));
        } else if (input instanceof File) {
            originalImage = readImageFromStream(new FileInputStream((File) input));
        } else {
            throw new IllegalArgumentException("Unsupported input type. Must be byte[] or File.");
        }

        return createScaledImage(originalImage);
    }

    private static BufferedImage readImageFromStream(InputStream inputStream) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
        return ImageIO.read(imageInputStream);
    }

    /**
     * Creates a scaled image if necessary, returning it as a byte array.
     *
     * @param originalImage The original image to scale.
     * @return Scaled image as byte[], or null if scaling is not needed.
     */
    private static byte @Nullable [] createScaledImage(BufferedImage originalImage) throws IOException, ImagingOpException {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int maxDimension = Math.max(originalWidth, originalHeight);

        if (maxDimension > MAX_WIDTH) {
            double scaleFactor = (double) MAX_WIDTH / maxDimension;
            BufferedImage scaledImage = scaleImage(originalImage, scaleFactor);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(scaledImage, OUTPUT_IMAGE_FORMAT, byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
        }
        return null;
    }

    /**
     * Scales the given image by the specified scale factor.
     *
     * @param image       The image to scale.
     * @param scaleFactor The scaling factor.
     * @return The scaled BufferedImage.
     */
    private static BufferedImage scaleImage(BufferedImage image, double scaleFactor) throws ImagingOpException {
        AffineTransform transform = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
        AffineTransformOp scaleOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(image, null);
    }
}
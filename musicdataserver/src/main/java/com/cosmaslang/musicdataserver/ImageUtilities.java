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

    /**
     * Liefert ein skaliertes Image als byte[] zurück, oder null falls nicht skaliert werden muss
     */
    public static byte @Nullable[] buildThumbnail(byte[] imageContent) throws IOException, ImagingOpException {
        BufferedImage originalImage = getImageFromStream(new ByteArrayInputStream(imageContent));
        return createScaledImage(originalImage);
    }

    /**
     * Liefert ein skaliertes Image als byte[] zurück, oder null falls nicht skaliert werden muss
     */
    public static byte @Nullable[] buildThumbnail(File imageFile) throws IOException, ImagingOpException {
        BufferedImage originalImage = getImageFromStream(new FileInputStream(imageFile));
        return createScaledImage(originalImage);
    }

    private static BufferedImage getImageFromStream(InputStream inputStream) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
        return ImageIO.read(imageInputStream);
    }

    private static byte @Nullable[] createScaledImage(BufferedImage originalImage) throws IOException, ImagingOpException {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        int max = Math.max(w,h);
        if (max > MAX_WIDTH) {
            double scaleFactor = (double) MAX_WIDTH / max;
            //BufferedImage scaledImage = new BufferedImage(w, h,
            //        BufferedImage.TYPE_INT_ARGB);
            AffineTransform transform = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
            AffineTransformOp scaleOp =
                    new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage scaledImage = scaleOp.filter(originalImage, null);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "gif", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }
}

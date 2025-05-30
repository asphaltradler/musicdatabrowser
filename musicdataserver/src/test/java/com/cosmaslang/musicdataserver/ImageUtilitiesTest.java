package com.cosmaslang.musicdataserver;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilitiesTest {

    /**
     * Tests for the {@link ImageUtilities#buildThumbnail(Object)} method.
     * <p>
     * This method generates a thumbnail from an image provided either as a byte array or a File object.
     * If the image needs scaling (when the max dimension exceeds {@link ImageUtilities#MAX_WIDTH}),
     * a resized GIF thumbnail is returned. Otherwise, {@code null} is returned.
     */

    @Test
    void testBuildThumbnailWithValidByteInputAndNeedsScaling() throws IOException {
        // Arrange
        BufferedImage inputImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(inputImage, "png", byteArrayOutputStream);
        byte[] inputBytes = byteArrayOutputStream.toByteArray();

        // Act
        byte[] thumbnailBytes = ImageUtilities.buildThumbnail(inputBytes);

        // Assert
        assertNotNull(thumbnailBytes);
        BufferedImage thumbnailImage = ImageIO.read(new ByteArrayInputStream(thumbnailBytes));
        assertNotNull(thumbnailImage);
        assertEquals(ImageUtilities.MAX_WIDTH, thumbnailImage.getWidth());
    }

    @Test
    void testBuildThumbnailWithValidByteInputButNoScalingNeeded() throws IOException {
        // Arrange
        BufferedImage inputImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(inputImage, "png", byteArrayOutputStream);
        byte[] inputBytes = byteArrayOutputStream.toByteArray();

        // Act
        byte[] thumbnailBytes = ImageUtilities.buildThumbnail(inputBytes);

        // Assert
        assertNull(thumbnailBytes);
    }

    @Test
    void testBuildThumbnailWithValidFileInputAndNeedsScaling() throws IOException {
        // Arrange
        BufferedImage inputImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("test", ".png");
        tempFile.deleteOnExit();
        ImageIO.write(inputImage, "png", tempFile);

        // Act
        byte[] thumbnailBytes = ImageUtilities.buildThumbnail(tempFile);

        // Assert
        assertNotNull(thumbnailBytes);
        BufferedImage thumbnailImage = ImageIO.read(new ByteArrayInputStream(thumbnailBytes));
        assertNotNull(thumbnailImage);
        assertEquals(ImageUtilities.MAX_WIDTH, thumbnailImage.getWidth());
    }

    @Test
    void testBuildThumbnailWithValidFileInputButNoScalingNeeded() throws IOException {
        // Arrange
        BufferedImage inputImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("test", ".png");
        tempFile.deleteOnExit();
        ImageIO.write(inputImage, "png", tempFile);

        // Act
        byte[] thumbnailBytes = ImageUtilities.buildThumbnail(tempFile);

        // Assert
        assertNull(thumbnailBytes);
    }

    @Test
    void testBuildThumbnailWithUnsupportedInput() {
        // Arrange
        Object unsupportedInput = Mockito.mock(Object.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ImageUtilities.buildThumbnail(unsupportedInput));
        assertEquals("Unsupported input type. Must be byte[] or File.", exception.getMessage());
    }

    @Test
    void testBuildThumbnailHandlesIOExceptionGracefully() {
        // Arrange
        File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.exists()).thenReturn(true);
        Mockito.when(mockFile.isFile()).thenReturn(true);
        Mockito.when(mockFile.canRead()).thenReturn(true);
        Mockito.when(mockFile.getPath()).thenReturn("path/to/file");

        try {
            Mockito.when(mockFile.getCanonicalPath()).thenThrow(new IOException("IO exception during file handling."));
        } catch (IOException e) {
            fail("Mockito configuration failed.");
        }

        // Act & Assert
        assertThrows(IOException.class, () -> ImageUtilities.buildThumbnail(mockFile));
    }
}
package pl.catchex.tray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper responsible for loading tray images and producing a fallback image
 * when the resource cannot be read.
 */
public final class TrayImageProvider {

    private static final Logger logger = LoggerFactory.getLogger(TrayImageProvider.class);

    private TrayImageProvider() {
        // utility
    }

    /**
     * Attempts to load an image resource from the provided ClassLoader.
     * If the resource is missing or cannot be read, a generated fallback image
     * is returned. This method never returns null.
     *
     * @param classLoader  class loader used to load the resource
     * @param resourcePath path to the image resource (e.g. "icon.png")
     * @return loaded Image or a generated fallback Image when loading fails
     */
    public static Image createImage(ClassLoader classLoader, String resourcePath) {
        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.info("Icon resource not found: {}. Using generated fallback.", resourcePath);
                return createFallbackImage();
            }

            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                logger.warn("ImageIO.read returned null for resource: {}. Using fallback.", resourcePath);
                return createFallbackImage();
            }
            return img;
        } catch (IOException e) {
            // Log the exception object to preserve stack trace for diagnostics
            logger.warn("Error loading image resource: {}. Using fallback.", resourcePath, e);
            return createFallbackImage();
        }
    }

    public static Image createFallbackImage() {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            // Transparent background
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, size, size);
            g.setComposite(AlphaComposite.SrcOver);

            // Rounded rectangle and a simple glyph (e.g., letter T)
            g.setColor(new Color(0x2E86AB)); // blue
            g.fillRoundRect(0, 0, size, size, 4, 4);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Dialog", Font.BOLD, 10));
            g.drawString("T", 4, 12);
        } finally {
            g.dispose();
        }
        return img;
    }
}

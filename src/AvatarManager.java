import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * Handles loading, scaling, and caching of avatar images.
 */
public class AvatarManager {
    private static final String BASE_PATH = "Resources/"; 
    private static final int WIDTH  = 100;
    private static final int HEIGHT = 100;

    private final Map<String, ImageIcon> cache = new HashMap<>();

    /**
     * Returns a scaled avatar icon for the given emotion.
     * @param emotion key like "happy", "sad", "celebrate"
     */
    public ImageIcon get(String emotion) {
        return cache.computeIfAbsent(emotion, this::loadAndScale);
    }

    private ImageIcon loadAndScale(String emotion) {
        String path = BASE_PATH + "avatar_" + emotion + ".png";
        ImageIcon orig = new ImageIcon(path);
        Image img = orig.getImage()
                        .getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}

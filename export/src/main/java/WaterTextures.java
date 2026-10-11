import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes what the GL toolkit's water effects draw with, beside the textures: the sixteen frames of
 * rippling noise the fixed function and turbulent water effects lay over the water, the client's
 * own {@code GlRippleNoiseTexture}, and the sixteen frames of flowing noise the flowing water
 * effect lays over a waterfall, its {@code GlFlowNoiseTexture}. Each is 128 by 128 texels of
 * luminance and alpha, written as one strip of sixteen frames down a PNG with the luminance in
 * each colour channel.
 */
public final class WaterTextures {

    private static final int WIDTH_TEXELS = 128;
    private static final int HEIGHT_TEXELS = 128;
    private static final int FRAMES = 16;
    private static final int BYTES_PER_TEXEL = 2;

    public static void write(Path directory) throws IOException {
        var water = directory.resolve("water");
        Files.createDirectories(water);
        ImageIO.write(strip(new GlRippleNoiseTexture().generate()), "png", water.resolve("ripple.png").toFile());
        ImageIO.write(strip(new GlFlowNoiseTexture().generate()), "png", water.resolve("flow.png").toFile());
        Files.deleteIfExists(water.resolve("turbulence.json"));
    }

    private static BufferedImage strip(byte[] texels) {
        var image = new BufferedImage(WIDTH_TEXELS, HEIGHT_TEXELS * FRAMES, BufferedImage.TYPE_INT_ARGB);
        for (var frame = 0; frame < FRAMES; frame++) {
            for (var y = 0; y < HEIGHT_TEXELS; y++) {
                for (var x = 0; x < WIDTH_TEXELS; x++) {
                    var at = ((frame * HEIGHT_TEXELS + y) * WIDTH_TEXELS + x) * BYTES_PER_TEXEL;
                    var luminance = texels[at] & 0xFF;
                    var alpha = texels[at + 1] & 0xFF;
                    image.setRGB(x, frame * HEIGHT_TEXELS + y, alpha << 24 | luminance << 16 | luminance << 8 | luminance);
                }
            }
        }
        return image;
    }


    private WaterTextures() {
        /* empty */
    }
}

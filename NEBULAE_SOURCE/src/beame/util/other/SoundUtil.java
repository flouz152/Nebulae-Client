package beame.util.other;

import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static beame.util.IMinecraft.mc;

public class SoundUtil {
// leaked by itskekoff; discord.gg/sk3d VYMjHkjd
    private static Clip currentClip = null;

    public static void playSound(String sound, float value, boolean nonstop) {
        AtomicReference<Clip> clipRef = new AtomicReference<>();

        try {
            Clip clip = AudioSystem.getClip();
            clipRef.set(clip);
            InputStream is = mc.getResourceManager().getResource(new ResourceLocation("night/sounds/" + sound)).getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);

            if (audioInputStream == null) {
                System.out.println("Sound not found!");
                return;
            }

            AudioFormat baseFormat = audioInputStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            
            try {
                clip.open(convertedStream);
            } catch (LineUnavailableException e) {
                System.out.println("Звуковой формат не поддерживается: " + e.getMessage());
                return;
            }
            
            clip.start();

            FloatControl floatControl = null;
            try {
                floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } catch (Exception e) {
                System.out.println("Не удалось получить контроль громкости: " + e.getMessage());
                return;
            }
            
            float min2 = floatControl.getMinimum();
            float max2 = floatControl.getMaximum();
            float volumeInDecibels = (float) (min2 * (1.0 - value / 100.0) + max2 * (value / 100.0));
            floatControl.setValue(volumeInDecibels);

            if (nonstop) {
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                });
            }

        } catch (LineUnavailableException e) {
            System.out.println("Звуковой формат не поддерживается системой: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при воспроизведении звука: " + e.getMessage());
        }
    }

}



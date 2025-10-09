package net.minecraft.client.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public interface IAudioStream extends Closeable
{
// leaked by itskekoff; discord.gg/sk3d h7rfgwlw
    AudioFormat getAudioFormat();

    ByteBuffer readOggSoundWithCapacity(int size) throws IOException;
}

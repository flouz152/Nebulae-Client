package beame.util.music;

import lombok.Getter;

public class MusicUtil {
// leaked by itskekoff; discord.gg/sk3d 2Gohqmn1

    @Getter
    public final String title;

    @Getter
    public final String author;

    @Getter
    public final int duration;

    public MusicUtil(String title, String author, int duration) {
        this.title = title;
        this.author = author;
        this.duration = duration;
    }
}

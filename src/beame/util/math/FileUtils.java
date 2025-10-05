package beame.util.math;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtils {
// leaked by itskekoff; discord.gg/sk3d epYkxJah

    @SneakyThrows
    public String readInputStream(InputStream inputStream) {
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().collect(Collectors.joining("\n"));
    }
}

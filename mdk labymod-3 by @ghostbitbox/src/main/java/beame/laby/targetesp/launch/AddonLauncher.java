package beame.laby.targetesp.launch;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;

public final class AddonLauncher {

  private AddonLauncher() {
  }

  public static void main(String[] args) {
    Map<String, String> arguments = new HashMap<>();
    hackNatives();

    arguments.put("version", "LabyMod-1.16.5");
    arguments.put("accessToken", "LabyMod");
    arguments.put("userProperties", "{}");
    arguments.put("username", "DevUser");
    arguments.put("tweak", "net.labymod.vanilla.LabyModTweaker");

    String assetIndex = System.getenv("assetIndex");
    if (isNotEmpty(assetIndex)) {
      arguments.put("assetIndex", assetIndex);
    }

    String assetDirectory = System.getenv("assetDirectory");
    if (isNotEmpty(assetDirectory)) {
      arguments.put("assetsDir", assetDirectory);
    } else {
      arguments.put("assetsDir", "assets");
    }

    List<String> argumentList = new ArrayList<>();
    arguments.forEach((key, value) -> {
      if (isNotEmpty(value)) {
        argumentList.add("--" + key);
        argumentList.add(value);
      }
    });

    Launch.main(argumentList.toArray(new String[0]));
  }

  private static void hackNatives() {
    String paths = System.getProperty("java.library.path");
    String nativesDir = System.getenv("nativesDirectory");

    if (isNotEmpty(nativesDir)) {
      paths = isNotEmpty(paths) ? paths + File.pathSeparator + nativesDir : nativesDir;
      System.setProperty("java.library.path", paths);
    }

    try {
      Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
      sysPathsField.setAccessible(true);
      sysPathsField.set(null, null);
    } catch (Throwable ignored) {
    }
  }

  private static boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }
}

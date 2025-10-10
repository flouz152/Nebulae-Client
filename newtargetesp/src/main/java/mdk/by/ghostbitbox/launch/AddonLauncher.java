package mdk.by.ghostbitbox.launch;

import com.google.common.base.Strings;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;

public class AddonLauncher {

    public static void main(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        hackNatives();

        arguments.put("version", "LabyMod-1.16.5");
        arguments.put("accessToken", "LabyMod");
        arguments.put("userProperties", "{}");
        arguments.put("username", "DevUser");
        arguments.put("tweak", "net.labymod.vanilla.LabyModTweaker");

        String assetIndex = System.getenv("assetIndex");
        if (assetIndex != null && !assetIndex.isEmpty()) {
            arguments.put("assetIndex", assetIndex);
        }

        String assetDirectory = System.getenv("assetDirectory");
        if (assetDirectory != null && !assetDirectory.isEmpty()) {
            arguments.put("assetsDir", assetDirectory);
        } else {
            arguments.put("assetsDir", "assets");
        }

        List<String> argumentList = new ArrayList<>();
        arguments.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                argumentList.add("--" + k);
                argumentList.add(v);
            }
        });

        Launch.main(argumentList.toArray(new String[0]));
    }

    private static void hackNatives() {
        String paths = System.getProperty("java.library.path");
        String nativesDir = System.getenv().get("nativesDirectory");

        if (nativesDir != null) {
            paths = Strings.isNullOrEmpty(paths) ? nativesDir : paths + File.pathSeparator + nativesDir;
            System.setProperty("java.library.path", paths);
        }

        try {
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (Throwable ignored) {
        }
    }
}
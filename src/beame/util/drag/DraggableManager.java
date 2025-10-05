package beame.util.drag;

import beame.Essence;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class DraggableManager {
// leaked by itskekoff; discord.gg/sk3d v9xN7JkC
    public static HashMap<String, Dragging> draggables = new HashMap();
    public static final File DRAG_DATA = new File(Essence.getHandler().getClientDir() + "\\elements.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public static void save() {
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
        }
        try {
         //   System.out.println(233424);
            Files.writeString(DRAG_DATA.toPath(), GSON.toJson(draggables.values()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void load() {
        Dragging[] draggings;
        if (!DRAG_DATA.exists()) {
            DRAG_DATA.getParentFile().mkdirs();
            return;
        }
        try {

            byte[] buf = Files.readAllBytes(DRAG_DATA.toPath());

            if (buf[0] == 0 && buf[1] == 0) {
                System.out.println("draggable data has empty");
            } else {
                draggings = GSON.fromJson(Files.readString(DRAG_DATA.toPath()), Dragging[].class);

                for (Dragging dragging : draggings) {
                    if (dragging == null) {
                        return;
                    }
                    Dragging currentDrag = draggables.get(dragging.getName());
                    if (currentDrag == null) continue;
                    currentDrag.setX(dragging.getX());
                    currentDrag.setY(dragging.getY());
                    draggables.put(dragging.getName(), currentDrag);
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

    }
}

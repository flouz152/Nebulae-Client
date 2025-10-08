package beame.managers.staff;

import beame.Nebulae;
import beame.util.IMinecraft;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class StaffManager implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d OLqRzHrp

    @Getter
    private final Set<String> staffs = new HashSet<>();
    public static final File file = new File(Nebulae.getHandler().getClientDir() + "\\staff.json");

    public void init() throws IOException {
        if (file.exists()) {
            loadStaffs();
        } else {
            createNewFile();
        }

        registerShutdownHook();
    }

    private void loadStaffs() throws IOException {
        try {
            staffs.addAll(Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            System.err.println("StaffManager: Ошибка при загрузке списка персонала " + e.getMessage());
            throw e;
        }
    }

    private void createNewFile() throws IOException {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("StaffManager: Не удалось создать директорию для файла персонала.");
            }
        }
        if (!file.createNewFile()) {
            throw new IOException("StaffManager: Не удалось создать файл персонала.");
        }
    }

    public void add(String name) {
        staffs.add(name);
        saveSafely();
    }

    public void remove(String name) {
        staffs.remove(name);
        saveSafely();
    }

    public void clear() {
        staffs.clear();
        saveSafely();
    }

    public boolean isStaff(String name) {
        return staffs.contains(name);
    }

    private void save() throws IOException {
        Files.write(file.toPath(), staffs);
    }

    private void saveSafely() {
        try {
            save();
        } catch (IOException e) {
            System.err.println("StaffManager: Ошибка при сохранении списка модераторов: " + e.getMessage());
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                save();
            } catch (IOException e) {
                System.err.println("StaffManager: Ошибка при сохранении списка модераторов при выходе: " + e.getMessage());
            }
        }));
    }
}

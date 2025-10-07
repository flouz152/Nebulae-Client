package beame.managers.alts;

import beame.Essence;
import net.minecraft.util.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static beame.util.IMinecraft.mc;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class AltManager {
// leaked by itskekoff; discord.gg/sk3d acEez1Wy
    public AltManager() { }

    public List<AltAccount> accounts = new ArrayList<>();
    public String selectedNickname = null;
    private static final Gson GSON = new Gson();

    public boolean isCreated(String username) {
        for(AltAccount friend : accounts){
            if(Objects.equals(friend.nickname, username))
                return true;
        }
        return false;
    }
    public void add(String username) {
        if(isCreated(username)) return;
        AltAccount newAcc = new AltAccount(username);
        accounts.add(newAcc);
        newAcc.authIn();
        saveFile();
    }
    public void rem(String username) {
        if(!isCreated(username)) return;
        accounts.removeIf(acc -> Objects.equals(acc.nickname, username));
        // Если удаляем выбранный ник, сбрасываем выбор
        if (Objects.equals(selectedNickname, username)) {
            selectedNickname = null;
        }
        saveFile();
    }

    public void setSelectedNickname(String nickname) {
        this.selectedNickname = nickname;
        saveFile();
    }

    public String getSelectedNickname() {
        return selectedNickname;
    }

    public static class AltAccount {
        public String nickname;
        public AltAccount(String nickname) {
            this.nickname = nickname;
        }

        public void authIn() {
            if (nickname.length() >= 3 && nickname.length() <= 16) {
                String uuid = UUID.randomUUID().toString();
                mc.session = new Session(nickname, uuid, "", "mojang");
            }
        }
    }

    public void saveFile() {
        File configFile = new File(Essence.getHandler().getClientDir() + "/alts.json");
        JsonObject obj = new JsonObject();
        obj.add("accounts", GSON.toJsonTree(accounts.stream().map(a -> a.nickname).toArray(String[]::new)));
        if (selectedNickname != null) {
            obj.addProperty("selected", selectedNickname);
        }
        try (FileWriter myWriter = new FileWriter(configFile)) {
            myWriter.write(GSON.toJson(obj));
        } catch (Exception ignored) { }
    }

    public void loadFile() {
        String filePath = Essence.getHandler().getClientDir() + "/alts.json";
        File file = new File(filePath);
        if(file.exists()) {
            try (FileReader reader = new FileReader(filePath)) {
                char[] buf = new char[(int) file.length()];
                reader.read(buf);
                String content = new String(buf).trim();
                if (content.startsWith("{")) {
                    // Новый формат (JSON)
                    JsonObject obj = GSON.fromJson(content, JsonObject.class);
                    accounts.clear();
                    if (obj.has("accounts")) {
                        List<String> nicks = GSON.fromJson(obj.get("accounts"), new TypeToken<List<String>>(){}.getType());
                        for (String c : nicks) {
                            if(Objects.equals(c, "") || Objects.equals(c, " ")) continue;
                            accounts.add(new AltAccount(c));
                        }
                    }
                    if (obj.has("selected")) {
                        selectedNickname = obj.get("selected").getAsString();
                    }
                } else {
                    // Старый формат (строка с |)
                String[] con = content.split("\\|");
                    accounts.clear();
                for(String c : con) {
                    if(Objects.equals(c, "") || Objects.equals(c, " ")) continue;
                    accounts.add(new AltAccount(c));
                }
                    selectedNickname = null;
            }
            } catch (Exception ex) { }
        }
    }
}

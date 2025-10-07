package beame.feature.ui;

import beame.Nebulae;
import beame.feature.notify.NotificationManager;
import beame.util.ClientHelper;
import beame.util.IMinecraft;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import events.impl.player.EventUpdate;
import events.impl.render.Render2DEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StaffListDraw implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d UN4SLsje
    public Dragging drag = Nebulae.getHandler().createDraggable("StaffListDraw", 220, 40);
    private float animW = 0, animH = 0;
    private float animation = 0;

    private final List<Staff> staffPlayers = new ArrayList<>();
    private final List<String> previousStaffPlayers = new ArrayList<>();
    private boolean isFirstUpdate = true;
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|мод|стаж|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд).*", Pattern.CASE_INSENSITIVE);

    private final List<String> staffNames = List.of(
            "cartem_pawlow", "yaBIG_PIG", "leon4ik1265", "M0Rell", "ItsLaMeNo228",
            "_XrABroStb_", "_Wasted_", "Rnzai101", "d3s1rr", "Xinero", "shift1K",
            "catwithcider", "ferdiguns", "elvawix", "pakucon", "Dangerwan", "AaseKasse",
            "kotwithcookie", "5ivkola", "qartem_pawlow", "qqlone", "yaPankYaLegenda",
            "killaura46", "HedRed2008", "sovipis213", "Kuratev", "DisNaLuk", "Xvostik333",
            "_PoJlToH_v_KeDaX", "GigabyteTop001", "stasikpid228", "Prepo_Tar", "DestroyeBoy",
            "ShulkerMe", "Sinex87", "catwithwine", "FrostyKKKKKKk", "Riflane", "axcu32",
            "Koni", "Yukkich_", "FeelingHollow_", "freylik", "ToP1_SiGmA120fps", "hi1kz0",
            "Jaba2282222", "Alisa_Lisna", "Slaker_TT", "_Vorobey_", "ObKOM", "Servi_fan",
            "ItsQwemli", "123Nouneim", "Mr_PoPiK", "top123321_3", "FaniMim", "bananachel1x",
            "xVa1s", "Rubi_by", "Smith_Wesson", "Remeis", "Glebik_Viz", "vallerryyy",
            "kogogot", "_FAKEMIR_", "Bakkap", "gas1", "lastocha_zxc", "t9soon", "seregoron",
            "itstik66", "Soft_Pubg2", "Insultdeda1", "MaJI4ik_Wolf", "furiys", "D0HATER",
            "manadger", "R333NJYRO", "furrylove", "Wq1rtz", "Homechok", "HaVaLbHblY_Lexa",
            "bone1nis", "thelostclown", "_XD_B_R_A_T_I_K_", "chfifi", "stalin_vozhd",
            "_n_E_y_Z_i_", "Q1ezzyy", "KATYAN", "ckyka0_0", "teletema", "Your_Waifu31",
            "HeIFa", "calacrad", "Pusse4ki", "NSAI", "JordaNNN1337", "egor4ik9131",
            "denomination", "Fan1x", "Fly_mYsun", "Lexaa2000", "Raudov_", "Bokit",
            "Derter666", "Psyth", "RokinProPvP", "KaPMa__", "SulgA", "_Holgan_",
            "fonix_one_love", "Keldif", "su4ek", "GoDoKeR", "stasananas338", "Fun_Wolf",
            "kiselb312123", "Guaeshe", "DEliti", "_Forev3r_", "Adski_Snusoed", "L1TeNeRgY",
            "miracle3", "Erusik_Games", "Flazyzxc", "mrkotia_pro", "_SoleWalk_", "YesVokeQQ",
            "Arxangel1551", "kapatuzz", "Yangeto", "Esk1zzz_Ezzz", "Derfon_x", "celdou666",
            "Tub1s", "pasha_www", "ThunderShadow", "nevepsycho", "_Fl1ppy_", "ccurlyy",
            "Fen1x_b1b0", "_Drase_", "NonikStudioo", "TheJustice", "BakenskyMSH",
            "Press_F_ot_menya", "topstop1221", "gtnsdlg", "DalCanRan", "Relinker",
            "lol4ikopYT", "Fr1znt", "KeepSB", "rabai", "pls_daite_deneg1", "TBo9I_PaDoSTb_",
            "OsnovaZenon", "SabeSaser", "Vomplekin", "Kirieshka_Bomba", "Ded_shahed131",
            "reallyhelp", "akashi_seirin", "220voltz", "XauroHunt", "qveezyyy_", "1kotosamurai1",
            "AlShad", "Botik_884", "sienduk123", "laki_samurai", "imdeleted", "MoonLightxxx",
            "B1mb0sik", "MickyFlex", "Pimply_roughage0", "Qikit", "Calacrad", "Da1mon_666",
            "Ivk_A", "_Zigguratik_", "DanceLandaner24", "ilia3422", "neker97", "Sonamorqo0506",
            "JollyVa1s", "Xsaizekk", "_LunM1ce_", "Lama___Craft", "_Reym_", "Zahar1302",
            "pypsik_team", "milkimas", "vodochkin2", "MarionetkaFnaf", "pon_p0n_gay",
            "Ip0Th2", "homechok", "YARIK2024", "Satoru_Godjo29", "Rassvetovv", "_Bedmak_",
            "neverpsycho", "ferty0905", "eblanchik69", "Tocno_Ne_Coft", "DEERTVINK",
            "vlad396", "teurgyi", "Homyakafarma", "mrksbgg", "_TBo9I_PaDoSTb", "ArtCombo",
            "AHAHACUK_4CB", "i_prishelizrust", "f0x1kk", "Impality", "Ceremont", "BarneyQdlll",
            "olegun", "S3XYK0IIIAK", "hot_4yrka__", "Granny228666", "bedolaga1212",
            "Zxc_Banan1", "IngenBro", "FLoTHeR3", "Cocoin_User", "platohaBhop", "_4y4kek_",
            "Ben_Sub", "Holgan", "Kaneki_YT_King", "stalin_kut", "Be1ny", "aliiiibaaaa",
            "480FPS_CKOPOCTb", "sipwize", "YaNeChiter228D", "dimawqqqqqq", "azeret",
            "993x1000", "Legenda_Dotki", "YaanaCisst", "_OneTape_", "SwifFox", "Neodecvati997",
            "Zeliboba_0", "SideAn07", "xSoulSoon", "Ne_SoFtEr_ZXC", "MANDA_R1NKA",
            "tequesty", "cam213", "mrCAM", "_Rape_seRies", "syuns4921", "DenisOdessa",
            "SexyBoyAmir", "Sweety4ka", "zuko", "arlekinU3", "Mishka_001", "kimyraa",
            "Tinkers", "Lozz2x", "dikolong", "BIGcatFlame", "bb_Jay42", "Eshize",
            "Ya_He_power", "pampersnigi1338", "ImSoLegit_", "ZxcsorgexHAHAH", "HuKung",
            "UpSerd", "the_jamal", "AB0h", "krap1wka_ZXC", "kastroomXD", "xerinai",
            "Der1_", "Jeson", "Nesquik213", "Gouzer8_", "KreesTees", "bilcipl", "Lil_Yayo2",
            "Uliosh", "Mika_Mikis", "dimanxam", "_Gesichtslos_", "___OHO___", "Rex_Rol",
            "krap1wka_zxc", "Tosterw", "yoimm", "andrei2662", "Ragnarokezz", "CMEX_HAHAHAH",
            "UcKPeHHoCtb__", "faquesty", "mlak", "Inosuke_453", "MiraIIWeeN_", "Mika_M",
            "Vovanchik_2022", "TheEzKv", "UZI", "QwentiX1234", "Kaifarov", "bublikbesplatno",
            "rVrenUKilaiZ", "CO3HAHue", "Claxxick_", "KrutiPedali1337", "WaUwU",
            "Sveety_aq", "Papina_Cy4e4ka", "lilll", "_MoonM1ce_", "fongai00",
            "LeymoooVM", "nikita200404", "Nefolmi", "PVEplayer23", "DAHR2023",
            "FainterCart2943", "Apollons", "YungeRr", "NonameAnime", "fesfdwqqq",
            "AmiraldikFN", "_Sun_x_Ban_", "Leymooo", "Vapex", "YaAbsoluti", "shredr225",
            "moro7z", "qNoKeRp", "Samz1ro_one", "fesfdwqqqa", "weadyy", "nikaron4ik",
            "AbraFail223", "rip_keka", "zen1z", "MercyIzReal", "Silwebr", "HEIIyJle4ka",
            "3AIIPABKA", "unus3", "LORKAAA", "Underrfeeed", "Liptonxdfa", "sodaMEOW",
            "kratosik15", "JlIm0nAdIk", "1DefreezeDeade1", "1MilaGro1", "5chrdk",
            "WhyWhitePriden", "Leaderorkk", "zxcpipiska12", "1ZzkLooDizZ1", "bewitch_a6qV",
            "ShedKill", "zozzy__", "barbaris_88", "steac01", "Dehuk1", "its_senno",
            "pa3BleKaTelb", "Foksi4ek", "Uruma1488", "xsSpudi", "ecslipse", "sanapro913913",
            "FatherlyLynx331", "_MysterySharck_", "callo1", "MrKvik", "akvi4", "Xleb_Metx",
            "bqrka", "zy1ma", "qz3llas", "LYTIY_TOPER_", "Afakpro", "Egort7r7",
            "Krokss_twink", "Nezuko_4an", "Georgiy2311", "kreqs345", "Lord_Vodki",
            "kirillkils2", "trofifi2009", "DOOM22", "tariquso", "_bomper_", "antstri",
            "Glavstroy_only1", "Romeo0o0o", "aquanick", "Mishagame", "progiarbys3939",
            "May3ep_", "llBenzopilall", "YALAMA", "POKOR1TEL", "__pups__", "_FalleN_AnGeL__",
            "TheBantiKg", "sigma_pro2222209", "UNICTAJITEL", "Xitogami", "Xl0ne",
            "Biznesman20", "n3koglai_223", "IIuoneR", "1SARDO1", "mililka", "melaugas",
            "Shadow_Divine", "Savlani", "BeenAxis", "kittywithcookie", "fyfyshka1",
            "TBoU_CTPaX2", "forsh1", "kn4zi_kal", "Yayloyan", "shok3r", "b0mb1kb0mb1k",
            "PrOzItOcHa", "Gaia10", "SayMeName", "ethylecgonine", "klamiy", "_dezephype",
            "_artemis_12", "1338blessed", "HEPB", "gidralox123", "TheRubikYT",
            "ValerysikPupsik", "Cringery1", "WinZy72", "whitecatwithbeer", "KolkeR",
            "Benuwka", "Equality", "122Sue_Ridge", "siinta", "ucTePuka", "Black_Star_Girl",
            "exhaustedy", "vipnosok", "blue_stuff", "Jada1553", "ez_BARON_soso", "bimut",
            "LyNn4ik", "SuperSnusik", "Stringers", "qwertyrrr", "Portwein", "AKSTSK1",
            "Chu4ilo", "paranoia13", "hot_chick_", "Garazhick", "ZiPpeRokTL", "caibator",
            "Oshinqq", "suslik666", "_Sq1zyx_", "ilovebigl1ps", "psychowhore", "PL1648836930",
            "SoursDays", "Andreyka228_338", "beautifulliesss", "KoniBot", "YourSoPretty"
    );

    @Data
    @AllArgsConstructor
    public static class Staff {
        private final ITextComponent prefix;
        private final String name;
        private final boolean vanish;
        private final Status status;
    }

    public enum Status {
        VANISHED("", ColorUtils.rgb(255, 0, 0)),
        ONLINE("", ColorUtils.rgb(0, 255, 0)),
        NEAR("Рядом", ColorUtils.rgb(255, 255, 0));

        String string;
        int color;

        Status(String string, int color) {
            this.string = string;
            this.color = color;
        }
    }

    public void render(Render2DEvent event) {
        animation = AnimationMath.fast(animation, mc.currentScreen instanceof ChatScreen || !staffPlayers.isEmpty() ? 1 : 0, 12);
        if (animation == 0) return;

        float x = drag.getX(), y = drag.getY();
        CustomFont font = Fonts.SUISSEINTL.get(13);
        float padX = 4.5f, padY = 7, spacing = 4;
        float minW = 80;

        int maxW = font.getStringWidth("Staff List");
        for (Staff s : staffPlayers) {
            String shortPrefix = s.prefix != null ? shortenPrefix(s.prefix.getString()) + " " : "";
            String disp = shortPrefix + s.name;
            maxW = Math.max(maxW, font.getStringWidth(disp)) + 16;
        }

        float targetW = Math.max(minW, maxW + padX * 1f);
        animW = AnimationMath.fast(animW, targetW, 8);

        float headerH = font.getHeight() * 2.1f - 1;
        float entryH = font.getHeight() + 4;
        float rawListH = staffPlayers.isEmpty() ? 0 : staffPlayers.size() * entryH;
        float targetH = headerH + (staffPlayers.isEmpty() ? 0 : spacing + rawListH);
        animH = AnimationMath.fast(animH, targetH, 8);

        int textColor = ColorUtils.setAlpha(-1, (int)(255 * animation));
        int bgColor = ColorUtils.setAlpha(ColorUtils.rgba(0, 0, 0, 120), (int)(120 * animation));

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation(x + (animW / 2), y + (animH / 2), animation);

        ClientHandler.drawSexyRect(x, y, animW, headerH, Nebulae.getHandler().getModuleList().getHud().rounding.get(), true);

        float titleY = y + padY;
        font.drawString("Staff List", x + padX, titleY, textColor);

        String icon = "o";
        float iconW = Fonts.ESSENCE_ICONS.get(15).getWidth(icon);
        int iconColor = ColorUtils.setAlpha(Nebulae.getHandler().themeManager.getColor(0), (int)(255 * animation));
        Fonts.ESSENCE_ICONS.get(15).drawString(icon, x + animW - padX - iconW - 4, titleY, iconColor);

        if (!staffPlayers.isEmpty()) {
            float listY = y + headerH + spacing;
            ClientHandler.drawSexyRect(x, listY, animW, animH - headerH, Nebulae.getHandler().getModuleList().getHud().rounding.get(), false);

            Scissor.push();
            Scissor.setFromComponentCoordinates(x, listY, animW, animH - headerH);
            for (int i = 0; i < staffPlayers.size(); i++) {
                Staff s = staffPlayers.get(i);
                float entryY = listY + padY + i * entryH;

                float currentX = x + padX - 2;
                if (s.prefix != null) {
                    String shortPrefix = shortenPrefix(s.prefix.getString()) + " ";
                    font.drawString(shortPrefix, currentX, entryY, ColorUtils.setAlpha(ColorUtils.rgba(120, 120, 120, 255), (int)(255 * animation)));
                    currentX += font.getStringWidth(shortPrefix) + 3;
                }

                String name = s.name;
                font.drawString(name, currentX, entryY, ColorUtils.setAlpha(-1, (int)(255 * animation)));

                String stat = switch (s.status) {
                    case ONLINE -> "v";
                    case VANISHED -> "v";
                    case NEAR -> "!";
                };
                float stW = Fonts.ESSENCE_ICONS.get(13).getWidth(stat);
                int statusColor = ColorUtils.setAlpha(s.status.color, (int)(255 * animation));
                Fonts.ESSENCE_ICONS.get(13).drawString(stat, x + animW - padX - stW - 4, entryY + (float) (font.getHeight() - Fonts.ESSENCE_ICONS.get(13).getHeight()) / 2, statusColor);
            }
            Scissor.unset();
            Scissor.pop();
        }

        RenderSystem.popMatrix();

        drag.setWidth(animW);
        drag.setHeight(animH);
    }

    private String shortenPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return "";

        prefix = prefix.replace("⚡", "").trim();

        if (!prefix.isEmpty()) {
            return String.valueOf(prefix.charAt(0)).toUpperCase();
        }

        return "";
    }

    public void update(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        List<String> currentStaffOnline = new ArrayList<>();
        staffPlayers.clear();

        for (String staffName : staffNames) {
            boolean isOnline = false;
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(staffName)) {
                    Staff staff = new Staff(null, staffName, false, Status.ONLINE);
                    staffPlayers.add(staff);
                    currentStaffOnline.add(staffName);
                    isOnline = true;

                    if (!previousStaffPlayers.contains(staffName) && !isFirstUpdate) {
                        Nebulae.getHandler().notificationManager.pushNotify(
                                staffName + " §fзашел на сервер!",
                                NotificationManager.Type.Staff
                        );
                    }
                    break;
                }
            }

            if (!isOnline && previousStaffPlayers.contains(staffName)) {
                Nebulae.getHandler().notificationManager.pushNotify(
                        staffName + " §fвышел с сервера/зашел в спек!",
                        NotificationManager.Type.Staff
                );
            }
        }

        for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList()) {
            String name = team.getMembershipCollection().toString().replaceAll("[\\[\\]]", "");
            if (!namePattern.matcher(name).matches() || name.equals(mc.player.getName().getString())) continue;

            boolean vanish = true;
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(name)) {
                    vanish = false;
                    break;
                }
            }

            ITextComponent prefix = ClientHelper.isFuntime() ? null : team.getPrefix();
            boolean isStaff = prefix != null && prefixMatches.matcher(prefix.getString().toLowerCase(Locale.ROOT)).matches()
                    || Nebulae.getHandler().getStaffManager().isStaff(name);

            if (isStaff) {
                Status status = vanish ? Status.VANISHED : Status.ONLINE;
                if (!vanish && isPlayerNear(mc.player, name)) {
                    status = Status.NEAR;
                }

                Staff staff = new Staff(prefix, name, vanish, status);
                staffPlayers.add(staff);
                currentStaffOnline.add(name);

                if (!previousStaffPlayers.contains(name) && !isFirstUpdate && !hasAdminPrefix(name)) {
                    Nebulae.getHandler().notificationManager.pushNotify(
                            name + " §fзашел на сервер!",
                            NotificationManager.Type.Staff
                    );
                }
            }
        }

        for (String name : previousStaffPlayers) {
            if (!currentStaffOnline.contains(name) && !isFirstUpdate && !hasAdminPrefix(name)) {
                Nebulae.getHandler().notificationManager.pushNotify(
                        name + " §fвышел с сервера/зашел в спек!",
                        NotificationManager.Type.Staff
                );
            }
        }

        previousStaffPlayers.clear();
        previousStaffPlayers.addAll(currentStaffOnline);
        isFirstUpdate = false;

        staffPlayers.removeIf(staff -> staffPlayers.stream().filter(s -> s.name.equals(staff.name)).count() > 1);
    }

    private boolean isPlayerNear(PlayerEntity player, String staffName) {
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equals(staffName)) {
                PlayerEntity staffPlayer = mc.world.getPlayerByUuid(info.getGameProfile().getId());
                if (staffPlayer != null) {
                    double distance = player.getDistance(staffPlayer);
                    return distance <= 30.0;
                }
            }
        }
        return false;
    }

    private boolean hasAdminPrefix(String staffName) {
        return prefixMatches.matcher(staffName.toLowerCase(Locale.ROOT)).find();
    }
}
package beame.util.other;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import beame.components.command.AbstractCommand;

public class PushUtils {
// leaked by itskekoff; discord.gg/sk3d BK8M07ta
    public static boolean sendPush(String title, String message) {
        try {

            String script =
                    "$AppId = 'Nebulae.Client'\n" +
                            "\n" +
                            "# Регистрируем приложение для уведомлений\n" +
                            "$regPath = 'HKCU:\\SOFTWARE\\Classes\\AppUserModelId\\' + $AppId\n" +
                            "if (!(Test-Path $regPath)) {\n" +
                            "    New-Item -Path $regPath -Force | Out-Null\n" +
                            "    New-ItemProperty -Path $regPath -Name 'DisplayName' -Value 'NebulaeClient' -PropertyType String -Force | Out-Null\n" +
                            "    New-ItemProperty -Path $regPath -Name 'ShowInSettings' -Value 1 -PropertyType DWord -Force | Out-Null\n" +
                            "}\n" +
                            "\n" +
                            "# Создаем и отправляем уведомление\n" +
                            "$XmlText = @\"\n" +
                            "<toast scenario='reminder'>\n" +
                            "    <visual>\n" +
                            "        <binding template='ToastGeneric'>\n" +
                            "            <text>$($args[0])</text>\n" +
                            "            <text>$($args[1])</text>\n" +
                            "        </binding>\n" +
                            "    </visual>\n" +
                            "    <audio src='ms-winsoundevent:Notification.Default'/>\n" +
                            "</toast>\n" +
                            "\"@\n" +
                            "\n" +
                            "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null\n" +
                            "[Windows.Data.Xml.Dom.XmlDocument, Windows.Data.Xml.Dom.XmlDocument, ContentType = WindowsRuntime] | Out-Null\n" +
                            "\n" +
                            "$XmlDoc = [Windows.Data.Xml.Dom.XmlDocument]::new()\n" +
                            "$XmlDoc.LoadXml($XmlText)\n" +
                            "\n" +
                            "$Toast = [Windows.UI.Notifications.ToastNotification]::new($XmlDoc)\n" +
                            "$Toast.Tag = 'NebulaeClient'\n" +
                            "$Toast.Group = 'NebulaeClient'\n" +
                            "\n" +
                            "try {\n" +
                            "    $Notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier($AppId)\n" +
                            "    $Notifier.Show($Toast)\n" +
                            "    exit 0\n" +
                            "} catch {\n" +
                            "    Write-Error $_.Exception.Message\n" +
                            "    exit 1\n" +
                            "}\n";


            String tempDir = System.getProperty("java.io.tmpdir");
            File psFile = new File(tempDir, "notification.ps1");
            Files.write(psFile.toPath(), script.getBytes(StandardCharsets.UTF_8));


            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-File", psFile.getAbsolutePath(),
                    title.replace("\"", "`\""),
                    message.replace("\"", "`\"")
            );


            Process process = pb.start();
            int exitCode = process.waitFor();

            psFile.delete();

            if (exitCode == 0) {

                return true;
            } else {
                AbstractCommand.addMessage("Ошибка отправки уведомления");
                return false;
            }

        } catch (Exception e) {
            AbstractCommand.addMessage("§7[§bPush§7] §cОшибка: " + e.getMessage());
            return false;
        }
    }
}
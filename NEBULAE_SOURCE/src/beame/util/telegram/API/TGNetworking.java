package beame.util.telegram.API;

import beame.Nebulae;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class TGNetworking {
// leaked by itskekoff; discord.gg/sk3d vH3M9Km1
    public static boolean createRequest(String content) throws Exception {
        if (content.length() > 1024) {
            return false;
        }
        String finish_url = Nebulae.getHandler().telegram.url + content;
        URL request = new URL(finish_url);
        request.openStream();
        return true;
    }

    class Post {
        private static HttpsURLConnection getHttpsClient(String url) throws Exception {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection client = (HttpsURLConnection) new URL(url).openConnection();
            client.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            return client;
        }

        public static boolean createRequest(String type, TGUtils.requestProperty... properties) throws Exception {
            URL url = new URL(Nebulae.getHandler().telegram.url + type);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String param1 = properties[0].value;
            String param2 = properties[1].value;
            String parameters = String.format("chat_id=%s&text=%s", URLEncoder.encode(param1, StandardCharsets.UTF_8), URLEncoder.encode(param2, StandardCharsets.UTF_8));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(parameters);
            outputStream.flush();
            outputStream.close();
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return true;
        }
    }
}
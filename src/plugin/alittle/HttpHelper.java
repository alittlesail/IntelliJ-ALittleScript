package plugin.alittle;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpHelper {
    private static String ID = null;

    public static String Get(@NotNull String urlContent) {
        List<String> resultList = null;
        try
        {
            URL url = new URL(urlContent);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            resultList = new ArrayList<>();
            String current;
            while((current = in.readLine()) != null)
                resultList.add(current);
        }
        catch(IOException e)
        {
        }

        if (resultList == null) return null;
        return String.join("\n", resultList);
    }
    public static String Post(@NotNull String urlContent, @NotNull String content) {

        List<String> resultList = null;
        try
        {
            URL url = new URL(urlContent);
            URLConnection urlConnection = url.openConnection();
            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            out.write(content);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            resultList = new ArrayList<>();
            String current;
            while((current = in.readLine()) != null)
                resultList.add(current);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        if (resultList == null) return null;
        return String.join("\n", resultList);
    }

    public static String GetID()
    {
        if (ID != null) return ID;

        List<String> mac_list = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces
                        .nextElement();
                String name = networkInterface.getDisplayName();
                if (name.contains("Adapter") || name.contains("Virtual") || name.contains("VMnet") || name.contains("#")) {
                    continue;
                }
                if (networkInterface.isVirtual() || !networkInterface.isUp() || !networkInterface.supportsMulticast()) {
                    continue;
                }
                byte[] macBuf = networkInterface.getHardwareAddress();
                if (macBuf == null) continue;
                Formatter formatter = new Formatter();
                String sMAC = "";
                for (int i = 0; i < macBuf.length; i++) {
                    sMAC = formatter.format(Locale.getDefault(), "%02X%s",
                            macBuf[i], (i < macBuf.length - 1) ? "-" : "")
                            .toString();
                }
                formatter.close();
                mac_list.add(sMAC);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Collections.sort(mac_list);
        if (mac_list.isEmpty()) return null;
        ID = mac_list.get(0);
        return ID;
    }
}

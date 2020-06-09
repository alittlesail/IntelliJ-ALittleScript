package plugin.alittle;

import org.jetbrains.annotations.NotNull;

public class SendLogRunnable implements Runnable {
    private final String m_content;

    public SendLogRunnable(@NotNull String content) {
        m_content = content;
    }

    @Override
    public void run() {
        String id = HttpHelper.GetID();
        if (id == null) return;

        String json = "{";
        json += "\"id\":\"" + id + "\",";
        json += "\"content\":\"" + m_content + "\"";
        json += "}";

        HttpHelper.Post("http://139.159.176.119/SendLog", json);
    }


    public static void SendLog(@NotNull String content) {
        // new Thread(new SendLogRunnable(content)).start();
    }
}

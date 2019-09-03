package xyz.joesorensen.starbot2.listeners;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class TwitchPing extends TimerTask {

    public static boolean live = false;
    private Stream data;

    public void run() {
        Logger log = LoggerFactory.getLogger("Twitch Ping");
        Twitch twitch = new Twitch();
        twitch.setClientId(TwitchListener.id);

        twitch.streams().get(TwitchListener.loginName, new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream) {
                data = stream;
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

        if (data == null) {
            if (live)
                TwitchEventManager.offline();
            live = false;
        } else {
            if (!live)
                TwitchEventManager.live(data);
            live = true;
        }
    }
}

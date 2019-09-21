package xyz.joesorensen.twitchutil;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.TimerTask;

public class TwitchPing extends TimerTask {

    public static boolean live = false;
    private HashMap<String, String> map;
    private Stream data;
    private Channel user;

    public void run() {
        Logger log = LoggerFactory.getLogger("Twitch Ping");
        log.info("Pinging...");
        Twitch twitch = new Twitch();
        twitch.setClientId(TwitchListener.id);

        twitch.streams().get(TwitchListener.loginName, new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream) {
                if(stream != null)
                    map = (HashMap<String, String>) stream.getAdditionalProperties().get("stream");
                data = stream;
            }

            @Override
            public void onFailure(int i, String s, String s1) {
                log.info("failed");
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("failed");
            }
        });

        twitch.channels().get(TwitchListener.loginName, new ChannelResponseHandler() {
            @Override
            public void onSuccess(Channel channel) {
                user = channel;
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

        while(data == null) {}

        if (data == null || map == null) {
            if (live)
                TwitchEventManager.offline();
            live = false;
        } else if (map.get("viewers") != null) {
            if (!live)
                TwitchEventManager.live(user, map);
            live = true;
        }

        TwitchListener.timer.schedule(TwitchListener.ping, 5000);
    }
}

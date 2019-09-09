package xyz.joesorensen.starbot2.listeners;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

public class TwitchPing extends TimerTask {

    public static boolean live = false;
    TwitchClient twitch;

    public void run() {
        Logger log = LoggerFactory.getLogger("Twitch Ping");
        twitch = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();
        Stream stream = null;
        StreamList raw = twitch.getHelix().getStreams(TwitchListener.id, "", null, 1, null, null, null, null, new ArrayList<>(Arrays.asList(TwitchListener.loginName))).execute();
        for(Stream data : raw.getStreams()) {
            stream = data;
        }

        if (stream == null) {
            if (live)
                TwitchEventManager.offline();
            live = false;
        } else if (stream.getViewerCount() != null) {
            if (!live)
                TwitchEventManager.live(stream);
            live = true;
        }
    }
}

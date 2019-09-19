package xyz.joesorensen.twitchutil;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
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
                .withClientId(TwitchListener.id)
                .build();
        Stream stream = null;
        StreamList raw = twitch.getHelix().getStreams(null, null, null, 1, null, null, null, null, Arrays.asList(TwitchListener.loginName)).execute();
        for(Stream data : raw.getStreams()) {
            stream = data;
        }

        if (stream == null) {
            if (live)
                TwitchEventManager.offline();
            live = false;
        } else if (stream.getViewerCount() != null) {
            if (!live) {
                UserList search = twitch.getHelix().getUsers(null, null, Arrays.asList(TwitchListener.loginName)).execute();
                User user;
                for(User data : search.getUsers())
                    user = data;
                TwitchEventManager.live(stream, user);
            }
            live = true;
        }
    }
}

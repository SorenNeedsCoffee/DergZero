package xyz.joesorensen.twitchutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

/**
 * -=TwitchUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class TwitchListener {
    static TwitchPing ping;
    private Timer timer = new Timer();
    static String id;
    static String loginName;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");

    public TwitchListener(String clientID) {
        id = clientID;
        ping = new TwitchPing();
    }

    public void track(String loginName) {
        TwitchListener.loginName = loginName;

        timer.scheduleAtFixedRate(ping, 0, 30000);
    }

    public static void ping() {
        ping.run();
    }

    public void trackVideos(String loginName) {
        if (loginName == null)
            TwitchListener.loginName = loginName;
    }
}

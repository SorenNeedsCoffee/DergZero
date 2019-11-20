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
    static String id;
    private static TwitchPing ping;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");
    private Timer timer = new Timer();

    public TwitchListener(String clientID) {
        id = clientID;
    }

    public static void ping() {
        ping.run();
    }

    public void track(String loginName) {
        ping = new TwitchPing(loginName);
        timer.scheduleAtFixedRate(ping, 0, 30000);
    }

    public void trackVideos(String loginName) {

    }
}

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
    public static TwitchPing ping = new TwitchPing();
    public static Timer timer = new Timer();
    static String id;
    static String loginName;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");

    public TwitchListener(String clientID) {
        id = clientID;
    }

    public void track(String loginName) {
        TwitchListener.loginName = loginName;

        timer.scheduleAtFixedRate(ping, 0, 30000);
    }

    public void trackVideos(String loginName) {
        if (loginName == null)
            TwitchListener.loginName = loginName;
    }
}

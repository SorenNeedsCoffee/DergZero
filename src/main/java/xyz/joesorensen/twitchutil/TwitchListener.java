package xyz.joesorensen.twitchutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class TwitchListener {
    static String id;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");
    static String loginName;
    public static TwitchPing ping = new TwitchPing();
    public static Timer timer = new Timer();

    public TwitchListener(String clientID) {
        id = clientID;
    }

    public void track(String loginName) {
        TwitchListener.loginName = loginName;

        timer.schedule(ping, 0);
    }
}
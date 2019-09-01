package xyz.joesorensen.starbot2.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class TwitchListener {
    static String id;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");
    static String loginName;

    public TwitchListener(String clientID) {
        id = clientID;
    }

    public void track(String loginName) {
        TwitchListener.loginName = loginName;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TwitchPing(), 0, 30000);
    }
}

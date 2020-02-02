package fyi.sorenneedscoffee.twitchutil;

import java.util.Timer;

/**
 * -=TwitchUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
@SuppressWarnings("EmptyMethod")
public class TwitchListener {
    static String id;
    private static TwitchPing ping;
    private final Timer timer = new Timer();

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

}

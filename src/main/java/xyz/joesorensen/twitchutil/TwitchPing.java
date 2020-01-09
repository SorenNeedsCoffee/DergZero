package xyz.joesorensen.twitchutil;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;

import java.util.HashMap;
import java.util.TimerTask;

/**
 * -=TwitchUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
@SuppressWarnings("unchecked")
class TwitchPing extends TimerTask {

    private static boolean live = false;
    private final Twitch twitch = new Twitch();
    private HashMap<String, String> map;
    private final String loginName;
    private Stream data;
    private Channel user;

    TwitchPing(String loginName) {
        twitch.setClientId(TwitchListener.id);
        this.loginName = loginName;
    }

    public void run() {
        map = null;
        data = null;
        user = null;

        twitch.streams().get(loginName, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) {
                if (stream != null)
                    map = (HashMap<String, String>) stream.getAdditionalProperties().get("stream");
                data = stream;
                twitch.channels().get(loginName, new ChannelResponseHandler() {
                    @Override
                    public void onSuccess(Channel channel) {
                        user = channel;
                        if (data == null || map == null) {
                            if (live)
                                TwitchEventManager.offline();
                            live = false;
                        } else if (map.get("viewers") != null) {
                            if (!live)
                                TwitchEventManager.live(user, map);
                            live = true;
                        }
                    }

                    @Override
                    public void onFailure(int i, String s, String s1) {
                        //empty due to no need for logic
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        //empty due to no need for logic
                    }
                });
            }

            @Override
            public void onFailure(int i, String s, String s1) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }
}

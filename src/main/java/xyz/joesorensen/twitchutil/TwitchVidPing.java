package xyz.joesorensen.twitchutil;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.VideoResponseHandler;
import com.mb3364.twitch.api.handlers.VideosResponseHandler;
import com.mb3364.twitch.api.models.Video;

import java.util.List;
import java.util.TimerTask;

/**
 * -=TwitchUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
class TwitchVidPing extends TimerTask {
    private int videoSize = 0;
    private final Twitch twitch = new Twitch();
    private final String loginName;

    TwitchVidPing(String loginName) {
        twitch.setClientId(TwitchListener.id);
        this.loginName = loginName;

        twitch.channels().getVideos(loginName, new VideosResponseHandler() {
            @Override
            public void onSuccess(int i, List<Video> list) {
                videoSize = list.size();
            }

            @Override
            public void onFailure(int i, String s, String s1) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    @Override
    public void run() {

        twitch.channels().getVideos(loginName, new VideosResponseHandler() {
            @Override
            public void onSuccess(int i, List<Video> list) {
                if (list.size() > videoSize) {
                    twitch.videos().get(list.get(0).getId(), new VideoResponseHandler() {
                        @Override
                        public void onSuccess(Video video) {

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

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }
}

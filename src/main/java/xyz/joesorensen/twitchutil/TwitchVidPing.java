package xyz.joesorensen.twitchutil;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.VideoResponseHandler;
import com.mb3364.twitch.api.handlers.VideosResponseHandler;
import com.mb3364.twitch.api.models.Video;

import java.util.List;
import java.util.TimerTask;

public class TwitchVidPing extends TimerTask {
    private int videoSize = 0;
    private Twitch twitch;

    TwitchVidPing() {
        twitch = new Twitch();
        twitch.setClientId(TwitchListener.id);

        twitch.channels().getVideos(TwitchListener.loginName, new VideosResponseHandler() {
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

        twitch.channels().getVideos(TwitchListener.loginName, new VideosResponseHandler() {
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
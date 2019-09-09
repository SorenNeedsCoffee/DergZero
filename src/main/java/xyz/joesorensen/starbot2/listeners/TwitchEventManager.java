package xyz.joesorensen.starbot2.listeners;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwitchEventManager {
    private static Listener listener;
    private static TwitchClient twitch;

    public TwitchEventManager() {
        twitch = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();
    }

    public static void setListener(Listener listener) {
        TwitchEventManager.listener = listener;
    }

    public static void live(Stream stream) {
        HashMap<String, String> streamData = new HashMap<>();
        streamData.put("streamsGame", getGame(stream.getGameId()));
        streamData.put("streamsViewers", String.valueOf(stream.getViewerCount()));
        streamData.put("channelDisplayName", stream.get);
        streamData.put("channelLanguage", stream.getLanguage());
        streamData.put("channelName", getChannel(stream.getUserId()).getDisplayName());
        streamData.put("channelLogo", getChannel(stream.getUserId()).getProfileImageUrl());
        streamData.put("channelProfileBanner", getChannel(stream.getUserId()).get);
        streamData.put("channelUrl", stream.getChannel().getUrl());
        streamData.put("channelViews", String.valueOf(stream.getChannel().getViews()));
        streamData.put("channelFollowers", String.valueOf(stream.getChannel().getFollowers()));

        listener.onLive(buildEmbed(streamData));
    }

    public static void offline() {
        listener.onOffline();
    }

    private static synchronized Message buildEmbed(Map<String, String> streamData) {
        String displayName = streamData.get("channelDisplayName");
        String streamTitle = streamData.get("channelStatus");
        String url = streamData.get("channelUrl");
        String logo = streamData.get("channelLogo");
        String profileBanner = streamData.get("channelProfileBanner");
        String game = streamData.get("streamsGame");
        String followers = streamData.get("channelFollowers");
        String views = streamData.get("channelViews");

        EmbedBuilder eBuilder = new EmbedBuilder();
        MessageBuilder mBuilder = new MessageBuilder();

        float[] rgb;

        rgb = Color.RGBtoHSB(100, 65, 165, null);
        eBuilder.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        eBuilder.setAuthor(displayName + " is now streaming!", url, "https://joesorensen.github.io/StarBot2/icon.png");


        eBuilder.setTitle(url);

        eBuilder.addField("Now Playing", game, false);
        eBuilder.addField("Stream Title", streamTitle, false);

        if (logo != null) {
            eBuilder.setThumbnail(logo);
        }


        eBuilder.setImage(profileBanner);
        eBuilder.addField("Followers", followers, true);
        eBuilder.addField("Total Views", views, true);


        MessageEmbed embed = eBuilder.build();

        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }

    private static String getGame(Long gameID) {
        GameList raw = twitch.getHelix().getGames(Arrays.asList(gameID.toString()), null).execute();
        Game data = null;
        for(Game game : raw.getGames()) {
            data = game;
        }
        if(data == null)
            return "";
        return data.getName();
    }

    private static User getChannel(Long userID) {
        UserList raw = twitch.getHelix().getUsers(TwitchListener.id, Arrays.asList(userID), null).execute();
        for(User user : raw.getUsers()) {
            return user;
        }
    }
}

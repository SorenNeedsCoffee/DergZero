package xyz.joesorensen.starbot2.listeners;

import com.mb3364.twitch.api.models.Stream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TwitchEventManager {
    private static Listener listener;

    public static void setListener(Listener listener) {
        TwitchEventManager.listener = listener;
    }

    public static void live(Stream stream) {
        HashMap<String, String> streamData = new HashMap<>();
        streamData.put("streamsGame", stream.getGame());
        streamData.put("streamsViewers", String.valueOf(stream.getViewers()));
        streamData.put("channelStatus", stream.getChannel().getStatus());
        streamData.put("channelDisplayName", stream.getChannel().getDisplayName());
        streamData.put("channelLanguage", stream.getChannel().getBroadcasterLanguage());
        streamData.put("channelId", String.valueOf(stream.getChannel().getId()));
        streamData.put("channelName", stream.getChannel().getName());
        streamData.put("channelLogo", stream.getChannel().getLogo());
        streamData.put("channelProfileBanner", stream.getChannel().getProfileBanner());
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

        eBuilder.setAuthor(displayName + " is now streaming!", url, "http://cdn.joesorensen.xyz/icon.png");


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
}

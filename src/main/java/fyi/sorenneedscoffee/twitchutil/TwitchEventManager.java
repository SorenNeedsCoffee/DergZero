package fyi.sorenneedscoffee.twitchutil;

import com.mb3364.twitch.api.models.Channel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import fyi.sorenneedscoffee.derg_zero.listeners.Listener;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * -=TwitchUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class TwitchEventManager {
    private static Listener listener;

    public static void setListener(Listener listener) {
        TwitchEventManager.listener = listener;
    }

    public static void live(Channel channel, HashMap<String, String> map) {
        HashMap<String, String> streamData = new HashMap<>();
        streamData.put("streamsGame", map.get("game"));
        streamData.put("streamsViewers", String.valueOf(map.get("viewers")));
        streamData.put("channelStatus", channel.getStatus());
        streamData.put("channelDisplayName", channel.getDisplayName());
        streamData.put("channelLanguage", channel.getBroadcasterLanguage());
        streamData.put("channelId", String.valueOf(channel.getId()));
        streamData.put("channelName", channel.getName());
        streamData.put("channelLogo", channel.getLogo());
        streamData.put("channelProfileBanner", channel.getProfileBanner());
        streamData.put("channelUrl", channel.getUrl());
        streamData.put("channelViews", String.valueOf(channel.getViews()));
        streamData.put("channelFollowers", String.valueOf(channel.getFollowers()));

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

        eBuilder.setAuthor(displayName + " is now streaming!", url, "https://joesorensen.xyz/cdn/icon.png");

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

        mBuilder.setContent("@everyone");
        mBuilder.setEmbed(embed);

        return mBuilder.build();
    }
}

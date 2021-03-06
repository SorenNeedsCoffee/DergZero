package fyi.sorenneedscoffee.garbagecan.xp.messages;

import fyi.sorenneedscoffee.garbagecan.xp.data.models.CacheKey;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class MessageListenerAdapter extends ListenerAdapter {
    static final HashMap<CacheKey, TextChannel> cache = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        cache.put(new CacheKey(event.getAuthor(), event.getGuild()), event.getChannel());
    }
}

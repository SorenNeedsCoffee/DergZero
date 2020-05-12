package fyi.sorenneedscoffee.derg_zero.xp;

import fyi.sorenneedscoffee.xputil.events.message.MessageEvent;
import fyi.sorenneedscoffee.xputil.events.user.JoinEvent;
import fyi.sorenneedscoffee.xputil.events.user.RemoveEvent;
import fyi.sorenneedscoffee.xputil.handler.EventHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class HandlerEvents extends ListenerAdapter {
    private final EventHandler handler;

    public HandlerEvents(EventHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if(event.getUser().isBot() || event.getUser().isFake())
            return;

        JoinEvent test = new JoinEvent(event.getUser().getId(), event.getGuild().getId());
        handler.onJoin(test);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if(event.getMessage().getContentDisplay().startsWith("!>") ||
                event.getMessage().getContentDisplay().isEmpty() ||
                event.getChannel().getId().equals("442555652359979009") ||
                event.getChannel().getId().equals("506503200866697226"))
            return;

        MessageEvent test = new MessageEvent(event.getAuthor().getId(), event.getGuild().getId(), event.getMessage().getContentRaw());
        handler.onMessage(test);
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        if(event.getUser().isBot() || event.getUser().isFake())
            return;

        RemoveEvent test = new RemoveEvent(event.getUser().getId(), event.getGuild().getId());
        handler.onRemove(test);
    }
}

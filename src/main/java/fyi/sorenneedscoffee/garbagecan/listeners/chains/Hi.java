package fyi.sorenneedscoffee.garbagecan.listeners.chains;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class Hi extends ListenerAdapter {
    private JDA jda;

    @Override
    public void onReady(ReadyEvent event) {
        this.jda = event.getJDA();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // do NOT remove this
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (event.getChannel() == jda.getTextChannelById("506503200866697226") && !event.getMessage().getContentDisplay().equalsIgnoreCase("hi"))
            event.getMessage().delete().queue();
    }
}

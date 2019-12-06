package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.temporal.ChronoUnit;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class PingCmd extends Command {

    public PingCmd() {
        this.name = "ping";
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Pong!");
            embed.addField("Ping", ping + "ms", true);
            embed.addBlankField(true);
            embed.addField("Websocket", event.getJDA().getGatewayPing() + "ms", true);
            m.editMessage(new MessageBuilder().setContent("​").setEmbed(embed.build()).build()).queue();
        });
    }

}

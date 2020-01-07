package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import xyz.joesorensen.starbot2.StarBot2;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class AboutCmd extends Command {

    public AboutCmd() {
        this.name = "about";
        this.help = "shows info about the bot";
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        if (StarBot2.version != null)
            embed.setTitle("StarBot2 | v" + StarBot2.version);
        else
            embed.setTitle("StarBot2 | DEVELOPMENT MODE");

        embed.setDescription(MarkdownUtil.italics("StarBot, but better!") + " Built with Java, JDA, and JDA-Utilities. Use " + MarkdownUtil.monospace("!>help") +  " to view commands.");
    }
}

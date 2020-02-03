package fyi.sorenneedscoffee.derg_zero.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.DergZero;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

/**
 * -=DergZero=-
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

        if (DergZero.version != null)
            embed.setTitle("DergZero | v" + DergZero.version);
        else
            embed.setTitle("DergZero | DEVELOPMENT MODE");

        embed.setDescription(MarkdownUtil.italics("StarBot, but better!") + " Built with Java, JDA, and JDA-Utilities. Use " + MarkdownUtil.monospace("!>help") + " to view commands.");
    }
}

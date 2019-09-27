package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.UserManager;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class SaveCmd extends AdminCommand {

    public SaveCmd() {
        this.name = "save";
        this.help = "save member data";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getJDA()
                .getGuildById(event.getGuild().getId())
                .getTextChannelById(event.getChannel().getId())
                .sendTyping()
                .queue();
        UserManager.saveFile();
        event.reactSuccess();
    }
}

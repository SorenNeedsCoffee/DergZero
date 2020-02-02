package fyi.sorenneedscoffee.starbot2.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.starbot2.StarBot2;
import fyi.sorenneedscoffee.starbot2.commands.OwnerCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class ShutdownCmd extends OwnerCommand {
    public ShutdownCmd() {
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.aliases = new String[]{"off"};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        UserManager.saveFile();
        event.getChannel().sendMessage("\u26A0 Shutting down...").complete();
        StarBot2.shutdown();
    }
}

package fyi.sorenneedscoffee.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.starbot2.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class BackupCmd extends AdminCommand {

    public BackupCmd() {
        this.name = "backup";
        this.help = "backup member data";
    }

    @Override
    protected void execute(CommandEvent event) {
        UserManager.saveFile();
        event.reactSuccess();
    }
}

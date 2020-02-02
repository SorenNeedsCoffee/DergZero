package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;

/**
 * -=DergZero=-
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

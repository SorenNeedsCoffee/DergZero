package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.util.UserManager;

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

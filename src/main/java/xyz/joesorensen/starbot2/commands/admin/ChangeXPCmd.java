package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.User;
import xyz.joesorensen.xputil.UserManager;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class ChangeXPCmd extends AdminCommand {

    public ChangeXPCmd() {
        this.name = "chxp";
        this.help = "change xp of any given user id";
        this.arguments = "<UserID | new xp>";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().equals("")) {
            event.replyError("Args cannot be blank!");
            return;
        }
        String[] args = event.getArgs().split(" ");

        User update = UserManager.getUser(args[0]);
        update.setXp(Double.parseDouble(args[1]));
        UserManager.updateUser(update);
    }
}

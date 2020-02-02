package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.User;
import fyi.sorenneedscoffee.xputil.util.UserManager;

import java.util.Objects;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class ChangeXPCmd extends AdminCommand {

    public ChangeXPCmd() {
        this.name = "chxp";
        this.help = "change xp of any given user id";
        this.arguments = "UserID | new xp";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().equals("")) {
            event.replyError("Args cannot be blank!");
            return;
        }
        String[] args = event.getArgs().split(" ");

        User update = UserManager.getUser(args[0]);
        Objects.requireNonNull(update).setXp(Double.parseDouble(args[1]));
        UserManager.updateUser(update);
    }
}

package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.lib.LvlRoleIDs;
import fyi.sorenneedscoffee.xputil.lib.XpInfo;
import fyi.sorenneedscoffee.xputil.util.User;
import fyi.sorenneedscoffee.xputil.util.UserManager;
import fyi.sorenneedscoffee.xputil.util.XpListener;

import java.util.Objects;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class ChangeLvlCmd extends AdminCommand {

    public ChangeLvlCmd() {
        this.name = "chlvl";
        this.help = "change level of any given user id";
        this.arguments = "UserID | new level";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().equals("")) {
            event.replyError("Args cannot be blank!");
            return;
        }
        String[] args = event.getArgs().split(" ");

        User update = UserManager.getUser(args[0]);
        Objects.requireNonNull(update).setLvl(Integer.parseInt(args[1]));
        update.setXp(XpInfo.lvlXpRequirementTotal(update.getLvl() - 1) + 10);
        XpListener.replaceRole(event.getGuild(), event.getGuild().getMemberById(args[0]), LvlRoleIDs.getLvlRole(Objects.requireNonNull(UserManager.getUser(args[0])).getLvl()), LvlRoleIDs.getLvlRole(update.getLvl()));
        UserManager.updateUser(update);
    }
}

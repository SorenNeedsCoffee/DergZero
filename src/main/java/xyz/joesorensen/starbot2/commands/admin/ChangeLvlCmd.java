package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.lib.LvlRoleIDs;
import xyz.joesorensen.xputil.lib.XpInfo;
import xyz.joesorensen.xputil.util.User;
import xyz.joesorensen.xputil.util.UserManager;
import xyz.joesorensen.xputil.util.XpListener;

/**
 * -=StarBot2=-
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
        update.setLvl(Integer.parseInt(args[1]));
        update.setXp(XpInfo.lvlXpRequirementTotal(update.getLvl()-1)+10);
        XpListener.replaceRole(event.getGuild(), event.getGuild().getMemberById(args[0]), LvlRoleIDs.getLvlRole(UserManager.getUser(args[0]).getLvl()), LvlRoleIDs.getLvlRole(update.getLvl()));
        UserManager.updateUser(update);
    }
}

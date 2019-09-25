package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.LvlRoleIDs;
import xyz.joesorensen.xputil.User;
import xyz.joesorensen.xputil.UserManager;
import xyz.joesorensen.xputil.XpListener;

public class ChangeLvlCmd extends AdminCommand {

    public ChangeLvlCmd() {
        this.name = "chlvl";
        this.help = "change level of any given user id";
        this.arguments = "<UserID | new level>";
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
        update.setXp(update.getLvl() * 250);
        XpListener.replaceRole(event.getGuild().getMemberById(args[0]), LvlRoleIDs.getLvlRole(UserManager.getUser(args[0]).getLvl()), LvlRoleIDs.getLvlRole(update.getLvl()));
        UserManager.updateUser(update);
    }
}

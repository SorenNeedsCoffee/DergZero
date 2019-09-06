package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.starbot2.models.User;
import xyz.joesorensen.starbot2.models.UserManager;

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
        UserManager.updateUser(update);
    }
}

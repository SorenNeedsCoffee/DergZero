package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.xputil.util.UserManager;

public class GetMembersJSONCmd extends AdminCommand {

    public GetMembersJSONCmd() {
        this.name = "json";
        this.help = "get current member data";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyInDm(UserManager.getJSON().toString());
    }
}

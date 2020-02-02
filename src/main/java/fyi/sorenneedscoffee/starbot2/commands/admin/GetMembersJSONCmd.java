package fyi.sorenneedscoffee.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import fyi.sorenneedscoffee.starbot2.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;

public class GetMembersJSONCmd extends AdminCommand {

    public GetMembersJSONCmd() {
        this.name = "json";
        this.help = "get current member data";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyInDm(MarkdownUtil.codeblock("json", UserManager.getJSON().toString()));
    }
}

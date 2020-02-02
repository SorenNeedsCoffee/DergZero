package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
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
package fyi.sorenneedscoffee.derg_zero.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.Warning;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.WarningUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class ViewWarningCmd extends Command {

    public ViewWarningCmd() {
        this.name = "viewwarning";
        this.aliases = new String[]{"vw"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        int targetId;
        try {
            targetId = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            event.replyError("Invalid id provided.");
            return;
        }

        Warning warning = WarningUtil.getWarning(targetId);
        if(warning == null) {
            event.replyError("Something went wrong, is the ID correct?");
            return;
        }

        if(event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.replyInDm(MarkdownUtil.bold("Warning #" + warning.getId()) + "\n" +
                    "\n" +
                    "Issued to " + event.getJDA().getUserById(warning.getuId()).getAsTag() + "\n" +
                    "\n" +
                    warning.toString());
        } else {
            if(!warning.getuId().equals(event.getAuthor().getId())) {
                event.replyError("You can't view that warning");
                return;
            }

            event.replyInDm(warning.toString(), success -> event.reactSuccess(), failure -> event.replyError("You must have DMs enabled to use this command."));
        }
    }
}

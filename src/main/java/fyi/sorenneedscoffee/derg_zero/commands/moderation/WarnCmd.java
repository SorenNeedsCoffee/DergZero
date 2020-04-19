package fyi.sorenneedscoffee.derg_zero.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.ModCommand;
import fyi.sorenneedscoffee.derg_zero.moderation.util.ModUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.OffenseType;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.WarningResult;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.WarningUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class WarnCmd extends ModCommand {

    public WarnCmd() {
        this.name = "warn";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");

        User target = ModUtil.getTarget(args[0]);
        int offenseType;
        try {
            offenseType = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            event.replyError("Invalid offense type.");
            return;
        }
        String comments = "";
        if(args.length > 2) {
            StringBuilder builder = new StringBuilder();
            for(int i = 2; i < args.length-1; i++) {
                builder.append(args[i])
                        .append(" ");
            }
            builder.append(args[args.length-1]);
            comments = builder.toString();
        }


        if(target == null) {
            event.replyError("That user doesn't exist.");
            return;
        }

        if(OffenseType.getTypeById(offenseType) == null) {
            event.replyError("Invalid offense type.");
            return;
        }

        WarningResult result = WarningUtil.addWarning(target.getId(), offenseType, comments);

        switch (result) {
            case NO_ACTION:
                ModUtil.sendNotification(target, event.getGuild().getTextChannelById("442555652359979009"), WarningUtil.generateWarningMessage(result.getWarning(), result.previouslyKicked),
                        target.getAsMention() + ", you have received a warning but we couldn't contact you. Please enable DMs and use " + MarkdownUtil.monospace("!>vw " + result.getWarning().getId()) + " to view your warning.");
                break;
            case KICK_ACTION:
                target.openPrivateChannel().complete().sendMessage(WarningUtil.generateActionMessage(result)).complete();
                event.getGuild().kick(target.getId(), "Automated Kick Event").queue();
                break;
            case BAN_ACTION:
                target.openPrivateChannel().complete().sendMessage(WarningUtil.generateActionMessage(result)).complete();
                event.getGuild().ban(target, 0, "Automated Ban Event").queue();
                break;
            case ERROR:
                event.replyError("There was a problem");
                break;
        }
    }
}

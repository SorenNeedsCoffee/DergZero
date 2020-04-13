package fyi.sorenneedscoffee.derg_zero.commands.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.ModCommand;
import fyi.sorenneedscoffee.derg_zero.moderation.ModUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.OffenseType;
import fyi.sorenneedscoffee.derg_zero.moderation.WarningResult;
import fyi.sorenneedscoffee.derg_zero.moderation.WarningUtil;
import net.dv8tion.jda.api.entities.PrivateChannel;
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
        int offenseType = Integer.parseInt(args[1]);
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
                break;
            case KICK_ACTION:
                break;
            case BAN_ACTION:
                break;
            case ERROR:
                event.replyError("There was a problem");
                break;
        }
    }
}

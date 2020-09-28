package fyi.sorenneedscoffee.garbagecan.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.commands.ModCommand;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.util.WarningUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.OffenseType;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.WarningResult;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.time.Duration;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class WarnCmd extends ModCommand {

    public WarnCmd() {
        super();
        this.name = "warn";
        this.help = "warn a user";
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().complete();

        if (event.getArgs().isEmpty()) {
            event.replyError("Invalid arguments.");
            return;
        }

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
        if (args.length > 2) {
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length - 1; i++) {
                builder.append(args[i])
                        .append(" ");
            }
            builder.append(args[args.length - 1]);
            comments = builder.toString();
        }


        if (target == null) {
            event.replyError("That user doesn't exist.");
            return;
        }

        if (OffenseType.getTypeById(offenseType) == null) {
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
                target.openPrivateChannel()
                        .flatMap(c -> c.sendMessage(WarningUtil.generateActionMessage(result)))
                        .delay(Duration.ofSeconds(1))
                        .flatMap((message -> event.getGuild().kick(target.getId(), "Automated Kick Event")))
                        .queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
                break;
            case BAN_ACTION:
                target.openPrivateChannel()
                        .flatMap(c -> c.sendMessage(WarningUtil.generateActionMessage(result)))
                        .delay(Duration.ofSeconds(1))
                        .flatMap((message -> event.getGuild().ban(target, 0, "Automated Ban Event")))
                        .queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
                break;
            case ERROR:
                event.replyError("There was a problem");
                return;
        }

        event.reply("A warning with id " + result.getWarning().getId() + " has been issued.");
    }
}

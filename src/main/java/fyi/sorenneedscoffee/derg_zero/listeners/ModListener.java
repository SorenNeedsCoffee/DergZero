package fyi.sorenneedscoffee.derg_zero.listeners;

import fyi.sorenneedscoffee.derg_zero.moderation.util.ModUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.WarningResult;
import fyi.sorenneedscoffee.derg_zero.moderation.util.WarningUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import static fyi.sorenneedscoffee.derg_zero.moderation.warnings.WarningResult.*;

public class ModListener extends ListenerAdapter {
    private Guild guild;

    @Override
    public void onReady(ReadyEvent event) {
        ModUtil.setJda(event.getJDA());
        guild = event.getJDA().getGuildById("442552203694047232");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User target = ModUtil.getTarget(event.getAuthor().getId());

    }

    private void warn(User target, int offenseType, String additionalComments) {
        WarningResult result = WarningUtil.addWarning(target.getId(), offenseType, additionalComments);

        switch (result) {
            case NO_ACTION:
                ModUtil.sendNotification(target, guild.getTextChannelById("442555652359979009"), WarningUtil.generateWarningMessage(result.getWarning(), result.previouslyKicked),
                        target.getAsMention() + ", you have received a warning but we couldn't contact you. Please enable DMs and use " + MarkdownUtil.monospace("!>vw " + result.getWarning().getId()) + " to view your warning.");
                break;
            case KICK_ACTION:
                target.openPrivateChannel().complete().sendMessage(WarningUtil.generateActionMessage(result)).complete();
                guild.kick(target.getId(), "Automated Kick Event").queue();
                break;
            case BAN_ACTION:
                target.openPrivateChannel().complete().sendMessage(WarningUtil.generateActionMessage(result)).complete();
                guild.ban(target, 0, "Automated Ban Event").queue();
                break;
            case ERROR:

                break;
        }
    }
}

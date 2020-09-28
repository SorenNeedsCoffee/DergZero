package fyi.sorenneedscoffee.garbagecan.listeners;

import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.util.WarningUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.WarningResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Set;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ModListener extends ListenerAdapter {
    private Guild guild;

    @Override
    public void onReady(ReadyEvent event) {
        guild = event.getJDA().getGuildById("442552203694047232");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMessage().getContentDisplay().startsWith(Main.config.prefix))
            return;

        User target = event.getAuthor();
        String message = event.getMessage().getContentStripped();

        Set<String> nonoWords = ModUtil.context.findNoNoWords(message);

        if(!nonoWords.isEmpty()) {
            event.getMessage().delete().queue();
            if(nonoWords.size() == 1)
                warn(target, 0, "Automated warning due to the presence of a disallowed word: " + nonoWords.toString());
            else
                warn(target, 0, "Automated warning due to the presence of disallowed words: " + nonoWords.toString());
        }
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
        }
    }
}

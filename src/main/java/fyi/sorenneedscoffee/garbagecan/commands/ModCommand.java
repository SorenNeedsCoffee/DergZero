package fyi.sorenneedscoffee.garbagecan.commands;

import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.Permission;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public abstract class ModCommand extends Command {
    protected ModCommand() {
        this.category = new Category("Moderator", event ->
        {
            if (event.getAuthor().getId().equals(event.getClient().getOwnerId()))
                return true;
            if (event.getGuild() == null)
                return true;
            return event.getMember().hasPermission(Permission.MESSAGE_MANAGE);
        });
        this.guildOnly = true;
    }
}

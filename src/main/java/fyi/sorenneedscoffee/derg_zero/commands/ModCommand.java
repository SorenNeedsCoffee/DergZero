package fyi.sorenneedscoffee.derg_zero.commands;

import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.Permission;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
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

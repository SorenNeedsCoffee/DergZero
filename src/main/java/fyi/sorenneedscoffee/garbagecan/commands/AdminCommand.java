package fyi.sorenneedscoffee.garbagecan.commands;

import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.Permission;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public abstract class AdminCommand extends Command {
    protected AdminCommand() {
        this.category = new Category("Admin", event ->
        {
            if (event.getAuthor().getId().equals(event.getClient().getOwnerId()))
                return true;
            if (event.getGuild() == null)
                return true;
            return event.getMember().hasPermission(Permission.MANAGE_SERVER);
        });
        this.guildOnly = true;
    }
}

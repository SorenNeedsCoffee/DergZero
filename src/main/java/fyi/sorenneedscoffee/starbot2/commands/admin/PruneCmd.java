package fyi.sorenneedscoffee.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fyi.sorenneedscoffee.starbot2.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class PruneCmd extends AdminCommand {

    public PruneCmd() {
        this.name = "prune";
        this.help = "prunes bot's user db";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("XPUtil");
        log.info(event.getAuthor().getName() + "Started a sb prune process.");
        UserManager.pruneUsers(event.getGuild());
    }
}

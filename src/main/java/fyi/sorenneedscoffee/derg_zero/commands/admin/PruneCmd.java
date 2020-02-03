package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
import fyi.sorenneedscoffee.xputil.util.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * -=DergZero=-
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

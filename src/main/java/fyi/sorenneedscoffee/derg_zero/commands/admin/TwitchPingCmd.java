package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.twitchutil.TwitchListener;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class TwitchPingCmd extends AdminCommand {

    public TwitchPingCmd() {
        this.name = "twitchping";
        this.help = "manually check if channel is live";
        this.aliases = new String[]{
                "tping"
        };
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        TwitchListener.ping();
    }

}

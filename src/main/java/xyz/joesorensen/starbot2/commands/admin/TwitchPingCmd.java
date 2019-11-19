package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mb3364.twitch.api.models.Stream;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.twitchutil.TwitchListener;
import xyz.joesorensen.twitchutil.TwitchPing;

/**
 * -=StarBot2=-
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

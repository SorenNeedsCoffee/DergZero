package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.twitchutil.TwitchEventManager;
import xyz.joesorensen.twitchutil.TwitchListener;
import xyz.joesorensen.twitchutil.TwitchPing;

public class TwitchPingCmd extends AdminCommand {
    private String id;
    private boolean live = TwitchPing.live;
    private Stream data;

    public TwitchPingCmd(String clientID) {
        this.name = "twitchping";
        this.help = "manually check if channel is live";
        this.aliases = new String[]{
                "tping"
        };
        this.guildOnly = true;
        id = clientID;
    }

    @Override
    protected void execute(CommandEvent event) {
        TwitchListener.ping.run();
    }

}

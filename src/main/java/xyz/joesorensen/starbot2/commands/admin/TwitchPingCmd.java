package xyz.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.AdminCommand;
import xyz.joesorensen.starbot2.listeners.TwitchEventManager;
import xyz.joesorensen.starbot2.listeners.TwitchPing;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;

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
        Twitch twitch = new Twitch();
        twitch.setClientId(this.id);

        twitch.streams().get("JoeSorensen", new StreamResponseHandler() {

            @Override
            public void onSuccess(Stream stream) {
                data = stream;
            }

            @Override
            public void onFailure(int i, String s, String s1) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

        if(data == null) {
            if (live)
                TwitchEventManager.offline();
            live = false;
            TwitchPing.live = false;
        } else {
            if (!live)
                TwitchEventManager.live(data);
            live = true;
            TwitchPing.live = true;
        }
    }

}

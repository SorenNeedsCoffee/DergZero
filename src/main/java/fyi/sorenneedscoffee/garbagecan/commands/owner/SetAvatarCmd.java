package fyi.sorenneedscoffee.garbagecan.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class SetAvatarCmd extends OwnerCommand {
    public SetAvatarCmd() {
        super();
        this.name = "setavatar";
        this.help = "sets the avatar of the bot";
        this.arguments = "<url>";
        this.guildOnly = false;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String url;
        if (event.getArgs().isEmpty())
            if (!event.getMessage().getAttachments().isEmpty() && event.getMessage().getAttachments().get(0).isImage())
                url = event.getMessage().getAttachments().get(0).getUrl();
            else
                url = null;
        else
            url = event.getArgs();
        InputStream s = imageFromUrl(url);
        if (s == null) {
            event.reply(event.getClient().getError() + " Invalid or missing URL");
        } else {
            try {
                event.getSelfUser().getManager().setAvatar(Icon.from(s)).queue(
                        v -> event.reply(event.getClient().getSuccess() + " Successfully changed avatar."),
                        t -> event.reply(event.getClient().getError() + " Failed to set avatar."));
            } catch (IOException e) {
                event.reply(event.getClient().getError() + " Could not load from provided URL.");
            }
        }
    }

    private InputStream imageFromUrl(String url) {
        if (url == null)
            return null;
        try {
            URL u = new URL(url);
            URLConnection urlConnection = u.openConnection();
            urlConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            return urlConnection.getInputStream();
        } catch (IOException | IllegalArgumentException ignore) {
        }
        return null;
    }
}

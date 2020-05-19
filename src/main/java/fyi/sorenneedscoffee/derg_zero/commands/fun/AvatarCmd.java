package fyi.sorenneedscoffee.derg_zero.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.FunCommand;
import fyi.sorenneedscoffee.derg_zero.moderation.util.ModUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class AvatarCmd extends FunCommand {

    public AvatarCmd() {
        this.name = "avatar";
        this.help = "Gets the avatar of a user";
        this.arguments = "< (leave blank for self)>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();

        String url;

        if ("".equals(args))
            url = event.getAuthor().getAvatarUrl();
        else
            url = ModUtil.getTarget(args).getAvatarUrl();

        if(url == null) {
            event.replyError("That user doesn't exist");
            return;
        }

        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            event.getChannel().sendFile(os.toByteArray(), "avatar.png").queue();
        } catch (IOException e) {
            event.replyError("There was a problem");
        }
    }
}

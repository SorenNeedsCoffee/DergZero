package xyz.joesorensen.starbot2.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.joesorensen.starbot2.commands.UserCommand;
import xyz.joesorensen.starbot2.models.User;
import xyz.joesorensen.starbot2.models.UserManager;

import java.awt.*;
import java.text.DecimalFormat;

public class LvlCmd extends UserCommand {

    public LvlCmd() {
        this.name = "lvl";
        this.help = "display user xp and level.";
        this.aliases = new String[]{
                "rank",
                "xp",
                "level"
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        User user = UserManager.getUser(event.getAuthor().getId());
        EmbedBuilder embed = new EmbedBuilder();
        float[] rgb;
        embed.setDescription("Level for "+event.getAuthor().getName());
        embed.setAuthor("User Level", null, event.getAuthor().getAvatarUrl());
        embed.addField("Level", Integer.toString(user.getLvl()), true);
        embed.addField("XP", new DecimalFormat("#.##").format(user.getXp()), false);
        embed.addField("Progress to next level",
                "```css\n"+
                        progress(user.getXp()/(user.getLvl()*250))+
                        " ("+new DecimalFormat("#.##").format(user.getXp())+"/"+(user.getLvl()*250)+")"+
                        "\n```",
                false);
        rgb = Color.RGBtoHSB(204, 255, 94, null);
        embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        event.reply(embed.build());
    }

    private static String progress(double progressPercentage) {
        final int width = 15;
        StringBuilder result = new StringBuilder();

        result.append("[");
        int i = 0;
        for (; i <= (int)(progressPercentage*width); i++) {
            result.append("█");
        }
        for (; i < width; i++) {
            result.append(" ");
        }
        result.append("]");

        return result.toString();
    }
}

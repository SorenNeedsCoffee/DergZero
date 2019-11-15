package xyz.joesorensen.xputil.commands.xp;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.joesorensen.xputil.User;
import xyz.joesorensen.xputil.UserManager;
import xyz.joesorensen.xputil.commands.XpCommand;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class LvlCmd extends XpCommand {

    public LvlCmd() {
        this.name = "lvl";
        this.help = "display user xp and level.";
        this.aliases = new String[]{
                "rank",
                "xp",
                "level"
        };
    }

    private static String progress(double progressPercentage) {
        final int width = 15;
        StringBuilder result = new StringBuilder();

        result.append("[");
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {
            result.append("#");
        }
        for (; i < width; i++) {
            result.append(" ");
        }
        result.append("]");

        return result.toString();
    }

    @Override
    protected void execute(CommandEvent event) {
        List<User> users = UserManager.getUsers();
        users.sort(Collections.reverseOrder());
        if (event.getArgs().equals("")) {
            User user = UserManager.getUser(event.getAuthor().getId());
            int placement = users.indexOf(user) + 1;
            EmbedBuilder embed = new EmbedBuilder();
            float[] rgb;
            if (event.getMember().getNickname() != null) {
                embed.setDescription("Level for " + event.getMember().getNickname());
            } else {
                embed.setDescription("Level for " + event.getMember().getUser().getName());
            }
            embed.setAuthor("User Level", null, event.getAuthor().getAvatarUrl());
            embed.addField("Level", Integer.toString(user.getLvl()), true);
            embed.addField("XP", new DecimalFormat("#.##").format(user.getXp()) + " | Placement: " + placement, false);
            embed.addField("Progress to next level",
                    "```java\n" +
                            progress((user.getXp() - ((user.getLvl() * 250) - 250)) / ((user.getLvl() * 250))) +
                            " (" + new DecimalFormat("#.##").format(user.getXp()) + "/" + (user.getLvl() * 250) + ")" +
                            "\n```",
                    false);
            rgb = Color.RGBtoHSB(204, 255, 94, null);
            embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

            event.reply(embed.build());
        } else {
            User user = UserManager.getUser(event.getMessage().getMentionedMembers().get(0).getUser().getId());
            int placement = users.indexOf(user) + 1;
            EmbedBuilder embed = new EmbedBuilder();
            float[] rgb;
            if (event.getMessage().getMentionedMembers().get(0).getNickname() != null) {
                embed.setDescription("Level for " + event.getMessage().getMentionedMembers().get(0).getNickname());
            } else {
                embed.setDescription("Level for " + event.getMessage().getMentionedMembers().get(0).getUser().getName());
            }
            embed.setAuthor("User Level", null, event.getMessage().getMentionedMembers().get(0).getUser().getAvatarUrl());
            embed.addField("Level", Integer.toString(user.getLvl()), true);
            embed.addField("XP", new DecimalFormat("#.##").format(user.getXp()) + " | Placement: " + placement, false);
            embed.addField("Progress to next level",
                    "```java\n" +
                            progress((user.getXp() - ((user.getLvl() * 250) - 250)) / ((user.getLvl() * 250))) +
                            " (" + new DecimalFormat("#.##").format(user.getXp()) + "/" + (user.getLvl() * 250) + ")" +
                            "\n```",
                    false);
            rgb = Color.RGBtoHSB(204, 255, 94, null);
            embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

            event.reply(embed.build());
        }
    }
}

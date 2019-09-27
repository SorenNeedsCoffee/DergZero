package xyz.joesorensen.starbot2.commands.xp;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.joesorensen.starbot2.commands.XpCommand;
import xyz.joesorensen.xputil.User;
import xyz.joesorensen.xputil.UserManager;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class TopCmd extends XpCommand {

    public TopCmd() {
        this.name = "top";
        this.help = "display users with top xp values";
    }

    @Override
    protected void execute(CommandEvent event) {
        List<User> users = UserManager.getUsers();
        Collections.sort(users, Collections.reverseOrder());
        EmbedBuilder embed = new EmbedBuilder();
        float[] rgb;
        embed.setDescription("Top Users");
        //embed.setAuthor("User Level", null, event.getAuthor().getAvatarUrl());
        embed.addField("",
                "```\n" +
                        "----------------------\n" +
                        list(users, event) +
                        "\n\n----------------------\n" +
                        "```",

                true
        );

        rgb = Color.RGBtoHSB(204, 255, 94, null);
        embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        event.reply(embed.build());
    }

    private static String list(List<User> users, CommandEvent event) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            User user = users.get(i);
            result.append("\n\n");
            if (event.getGuild().getMemberById(user.getId()).getNickname() != null) {
                result.append(i + 1).
                        append(". ").
                        append(event.getGuild().getMemberById(user.getId()).getNickname());
            } else {
                result.append(i + 1).
                        append(". ").
                        append(event.getJDA().getUserById(user.getId()).getName());
            }
            result.append(" (XP: " + new DecimalFormat("#.##").format(user.getXp()) + ")");
            result.append("\n");
            result.append("    Level: " + user.getLvl());
        }
        return result.toString();
    }
}

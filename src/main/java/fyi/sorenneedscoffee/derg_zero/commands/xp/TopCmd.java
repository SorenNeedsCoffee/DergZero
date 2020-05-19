package fyi.sorenneedscoffee.derg_zero.commands.xp;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.DergZero;
import fyi.sorenneedscoffee.derg_zero.commands.XpCommand;
import fyi.sorenneedscoffee.xputil.data.models.User;
import fyi.sorenneedscoffee.xputil.data.requests.RetrieveGroupRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class TopCmd extends XpCommand {

    public TopCmd() {
        this.name = "top";
        this.help = "display users with top xp values";
    }

    @SuppressWarnings("ConstantConditions")
    private static String list(List<User> users, CommandEvent event) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            User user = users.get(i);
            Member test = event.getGuild().getMemberById(user.getUserId());
            result.append("\n\n");
            result.append(i + 1).
                    append(". ").
                    append(test.getEffectiveName());
            result.append(" (XP: ").append(new DecimalFormat("#.##").format(user.getXp())).append(")");
            result.append("\n");
            result.append("    Level: ").append(user.getLevel());
        }
        return result.toString();
    }

    @Override
    protected void execute(CommandEvent event) {
        List<User> users = new java.util.ArrayList<>(DergZero.context.
                retrieveGroup(new RetrieveGroupRequest(event.getGuild().getId())).membersAsList());
        Objects.requireNonNull(users).sort(Collections.reverseOrder());
        EmbedBuilder embed = new EmbedBuilder();
        float[] rgb;
        embed.setTitle("Top Users");
        //embed.setAuthor("User Level", null, event.getAuthor().getAvatarUrl());
        embed.addField("",
                "```java\n" +
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
}

package fyi.sorenneedscoffee.derg_zero.commands.xp;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.DergZero;
import fyi.sorenneedscoffee.derg_zero.commands.XpCommand;
import fyi.sorenneedscoffee.xputil.data.models.User;
import fyi.sorenneedscoffee.xputil.data.requests.RetrieveGroupRequest;
import fyi.sorenneedscoffee.xputil.data.requests.RetrieveMemberRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        MessageEmbed msg;
        if (event.getArgs().equals("")) {
            msg = genEmbed(event.getMember());
        } else {
            msg = genEmbed(event.getMessage().getMentionedMembers().get(0));
        }
        event.reply(msg);
    }

    private MessageEmbed genEmbed(Member member) {
        List<User> users = new java.util.ArrayList<>(DergZero.context.
                retrieveGroup(new RetrieveGroupRequest(member.getGuild().getId())).membersAsList());
        users.sort(Collections.reverseOrder());
        User user = DergZero.context.retrieveMember(new RetrieveMemberRequest(member.getUser().getId(), member.getGuild().getId()));

        int placement = users.indexOf(user) + 1;
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(member.getEffectiveName(), null, member.getUser().getAvatarUrl());
        embed.setTitle("User Rank");
        embed.addField("Level", Integer.toString(Objects.requireNonNull(user).getLevel()), true);
        embed.addField("XP", new DecimalFormat("#,###,###.##").format(user.getXp()) + " | Placement: " + placement, false);
        embed.addField("",
                MarkdownUtil.codeblock("java", progress((user.getXp() - DergZero.calculator.totalRequired(user.getLevel() - 1)) / DergZero.calculator.xpRequired(user.getLevel())) +
                        " (" + new DecimalFormat("#,###.##").format(user.getXp() - DergZero.calculator.totalRequired(user.getLevel() - 1)) + "/" + new DecimalFormat("#,###.##").format(DergZero.calculator.xpRequired(user.getLevel())) + ")"),
                false);

        float[] rgb = Color.RGBtoHSB(204, 255, 94, null);
        embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        return embed.build();
    }
}

package fyi.sorenneedscoffee.derg_zero.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.nio.channels.Channel;

public class ModUtil {
    private static JDA jda;

    public static void setJda(JDA jda) {
        ModUtil.jda = jda;
    }

    public static User getTarget(String args) {
        User target;
        try {
            if(args.matches("<[@][!&]?[0-9]+>"))
                target = jda.getUserById(args.replaceAll("<@[!&]", "").replaceAll(">", ""));
            else if(args.matches("(.)+(#)\\d\\d\\d\\d"))
                target = jda.getUserByTag(args);
            else
                target = jda.getUserById(args);
        } catch (Exception e) {
            target = null;
        }
        return target;
    }

    public static void sendNotification(User target, TextChannel alt) {

    }
}

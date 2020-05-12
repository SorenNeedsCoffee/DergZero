package fyi.sorenneedscoffee.derg_zero.moderation.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import static net.dv8tion.jda.api.requests.ErrorResponse.CANNOT_SEND_TO_USER;

public class ModUtil {
    private static JDA jda;

    public static void setJda(JDA jda) {
        ModUtil.jda = jda;
    }

    public static User getTarget(String uId) {
        User target;
        try {
            if (uId.matches("<[@][!&]?[0-9]+>"))
                target = jda.getUserById(uId.replaceAll("<@[!&]", "").replaceAll(">", ""));
            else if (uId.matches("(.)+(#)\\d\\d\\d\\d"))
                target = jda.getUserByTag(uId);
            else
                target = jda.getUserById(uId);
        } catch (Exception e) {
            target = null;
        }
        return target;
    }

    public static void sendNotification(User target, TextChannel alt, String message, String altMessage) {
        if (target == null)
            return;
        target.openPrivateChannel()
                .flatMap(c -> c.sendMessage(message))
                .onErrorFlatMap(CANNOT_SEND_TO_USER::test,
                        (error) -> alt.sendMessage(altMessage))
                .queue();
    }
}

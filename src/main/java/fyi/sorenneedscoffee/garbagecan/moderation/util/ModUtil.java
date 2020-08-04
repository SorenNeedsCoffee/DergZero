package fyi.sorenneedscoffee.garbagecan.moderation.util;

import fyi.sorenneedscoffee.garbagecan.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

import static net.dv8tion.jda.api.requests.ErrorResponse.CANNOT_SEND_TO_USER;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ModUtil {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final JDA jda = Main.jda;
    public static DataContext context;

    public static void init() {
        context = new DataContext(Main.config.dbUrl);
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

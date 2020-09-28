package fyi.sorenneedscoffee.garbagecan.moderation.util;

import fyi.sorenneedscoffee.garbagecan.moderation.data.DataContext;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.OffenseType;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.Warning;
import fyi.sorenneedscoffee.garbagecan.moderation.data.models.WarningResult;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class WarningUtil {
    private static final int UNIQUE_ALLOWED = 4;
    private static final int SIMILAR_ALLOWED = 3;
    private static final int UNIQUE_ALLOWED_POST_KICK = UNIQUE_ALLOWED + 2;
    private static final int ID_LENGTH = 8;
    private static final char[] ALPHANUMERIC = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final DataContext context = ModUtil.context;
    private static final Random random = new Random();

    public static WarningResult addWarning(String uId, int offenseType, String comments) {
        Warning warning = ModUtil.context.addWarning(uId, offenseType, comments);

        if (warning == null)
            return WarningResult.ERROR;

        WarningResult result;

        if (context.isOnKickList(uId)) {
            if (!warning.getOffenseType().equals(OffenseType.MISC) &&
                    context.getWarnings(uId, false).size() == UNIQUE_ALLOWED_POST_KICK) {
                result = WarningResult.BAN_ACTION;
            } else {
                result = WarningResult.NO_ACTION;
            }
            result.previouslyKicked = true;
        } else {
            if (!warning.getOffenseType().equals(OffenseType.MISC) &&
                    context.getWarnings(uId, false).size() == UNIQUE_ALLOWED ||
                    context.getSimilarWarnings(warning).size() == SIMILAR_ALLOWED - 1) {
                context.addUserToKicklist(uId);
                result = WarningResult.KICK_ACTION;
            } else {
                result = WarningResult.NO_ACTION;
            }
        }


        result.overrideWarning(warning);
        return result;
    }

    public static Warning getWarning(String id) {
        return context.getWarning(id);
    }

    public static List<Warning> getWarnings(String uId, boolean includeMisc) {
        return context.getWarnings(uId, includeMisc);
    }

    public static void clearModerationHistory(String uId) {
        context.clearModerationHistory(uId);
    }

    public static String generateWarningMessage(Warning current, boolean previouslyKicked) {
        String result;

        if (current.getOffenseType().equals(OffenseType.MISC))
            result = MarkdownUtil.bold("You have received a warning from the DRACONIUM staff team") + "\n" +
                    "\n" +
                    current.toString() + "\n" +
                    "\n" +
                    MarkdownUtil.bold("Because this is a fake warning, this doesn't count towards a kick or ban.") + "\n" +
                    "\n" +
                    "If you have any questions, please contact a moderator";
        else if (previouslyKicked) {
            int remaining;

            try {
                remaining = UNIQUE_ALLOWED_POST_KICK - Objects.requireNonNull(context.getWarnings(current.getuId(), false)).size();
            } catch (NullPointerException e) {
                return "";
            }

            result = MarkdownUtil.bold("You have received a warning from the DRACONIUM staff team") + "\n" +
                    "\n" +
                    current.toString() + "\n" +
                    "\n" +
                    "You have " + MarkdownUtil.monospace(String.valueOf(remaining)) + " warnings left before a permanent ban.\n" +
                    "\n" +
                    "If you have any questions, please contact a moderator";
        } else {
            int uniqueRemaining;
            int similarRemaining;

            try {
                uniqueRemaining = UNIQUE_ALLOWED - Objects.requireNonNull(context.getWarnings(current.getuId(), false)).size();
                similarRemaining = SIMILAR_ALLOWED - Objects.requireNonNull(context.getSimilarWarnings(current)).size();
            } catch (NullPointerException e) {
                return "";
            }

            result = MarkdownUtil.bold("You have received a warning from the DRACONIUM staff team") + "\n" +
                    "\n" +
                    current.toString() + "\n" +
                    "\n" +
                    "You have " + MarkdownUtil.monospace(String.valueOf(uniqueRemaining)) + " unique warnings left before a kick or " + MarkdownUtil.monospace(String.valueOf(similarRemaining)) + " similar warnings before a kick.\n" +
                    "\n" +
                    "If you have any questions, please contact a moderator";
        }

        return result;
    }

    public static String generateActionMessage(WarningResult action) {
        String result = "";
        switch (action) {
            case KICK_ACTION:
                result = MarkdownUtil.bold("You have been kicked from the DRACONIUM server") + "\n" +
                        "\n" +
                        "Offense: " + action.getWarning().getOffenseType().getShortName() + " - " + action.getWarning().getOffenseType().getDescription() + "\n" +
                        "\n" +
                        "Additional Comments:\n" +
                        action.getWarning().getAdditionalComments() + "\n" +
                        "\n" +
                        MarkdownUtil.bold("You may return to the server if you wish, but please note you will only be able to receive one last warning before a permanent ban.");
                break;
            case BAN_ACTION:
                result = MarkdownUtil.bold("You have been banned from the DRACONIUM server") + "\n" +
                        "\n" +
                        "Offense: " + action.getWarning().getOffenseType().getShortName() + " - " + action.getWarning().getOffenseType().getDescription() + "\n" +
                        "\n" +
                        "Additional Comments:\n" +
                        action.getWarning().getAdditionalComments() + "\n" +
                        "\n" +
                        MarkdownUtil.bold("That was your last chance, and you may no longer come back. It is currently not possible to appeal this ban.");
                break;
        }

        return result;
    }

    public static String generateNewId() {
        char[] result = new char[ID_LENGTH];
        for (int i = 0; i < ID_LENGTH; i++) {
            result[i] = ALPHANUMERIC[random.nextInt(ALPHANUMERIC.length)];
        }
        return new String(result);
    }
}

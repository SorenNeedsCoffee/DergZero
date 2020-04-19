package fyi.sorenneedscoffee.derg_zero.moderation.warnings;

import fyi.sorenneedscoffee.derg_zero.moderation.util.DbManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.List;
import java.util.Objects;

public class WarningUtil {
    static final int uniqueAllowed = 4;
    static final int similarAllowed = 3;
    static final int uniqueAllowedPostKick = uniqueAllowed + 2;

    public static WarningResult addWarning(String uId, int offenseType, String comments) {
        Warning warning = DbManager.addWarning(uId, offenseType, comments);

        if(warning == null)
            return WarningResult.ERROR;

        WarningResult result;

        if(DbManager.isOnKicklist(uId)) {
            if(!warning.getOffenseType().equals(OffenseType.MISC) && DbManager.getWarnings(uId, false).size() == uniqueAllowedPostKick) {
                result = WarningResult.BAN_ACTION;
            } else {
                result = WarningResult.NO_ACTION;
            }
            result.previouslyKicked = true;
        } else {
            if(!warning.getOffenseType().equals(OffenseType.MISC) && DbManager.getWarnings(uId, false).size() == uniqueAllowed || DbManager.getSimilarWarnings(warning).size() == similarAllowed-1) {
                DbManager.addUserToKicklist(uId);
                result = WarningResult.KICK_ACTION;
            } else {
                result = WarningResult.NO_ACTION;
            }
        }


        result.overrideWarning(warning);
        return result;
    }

    public static Warning getWarning(int id) {
        return DbManager.getWarning(id);
    }

    public static List<Warning> getWarnings(String uId, boolean includeMisc) {
        return DbManager.getWarnings(uId, includeMisc);
    }

    public static void clearModerationHistory(String uId) {
        DbManager.clearModerationHistory(uId);
    }

    public static String generateWarningMessage(Warning current, boolean previouslyKicked) {
        String result;

        if(current.getOffenseType().equals(OffenseType.MISC))
            result = MarkdownUtil.bold("You have received a warning from the DRACONIUM staff team") + "\n" +
                    "\n" +
                    current.toString() + "\n" +
                    "\n" +
                    MarkdownUtil.bold("Because this is a fake warning, this doesn't count towards a kick or ban.") + "\n" +
                    "\n" +
                    "If you have any questions, please contact a moderator";
        else if(previouslyKicked) {
            int remaining;

            try {
                remaining = uniqueAllowedPostKick - Objects.requireNonNull(DbManager.getWarnings(current.getuId(), false)).size();
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
                uniqueRemaining = uniqueAllowed - Objects.requireNonNull(DbManager.getWarnings(current.getuId(), false)).size();
                similarRemaining = similarAllowed - Objects.requireNonNull(DbManager.getSimilarWarnings(current)).size();
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
}

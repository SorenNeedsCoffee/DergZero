package fyi.sorenneedscoffee.derg_zero.moderation;

import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.HashMap;
import java.util.List;

public class WarningUtil {

    public static WarningResult addWarning(String uId, int offenseType, String comments) {
        Warning warning = DbManager.addWarning(uId, offenseType, comments);

        if(warning == null)
            return WarningResult.ERROR;

        WarningResult result;

        if(DbManager.getWarnings(uId).size() == 5) {
            result = WarningResult.KICK_ACTION;
        } else if (DbManager.getSimilarWarnings(warning).size() == 2) {
            result = WarningResult.KICK_ACTION;
        } else {
            result = WarningResult.NO_ACTION;
        }


        result.overrideWarning(warning);
        return result;
    }

    public static String generateWarningMessage(Warning current) {
        List<Warning> warnings = DbManager.getWarnings(current.getuId());

        if(warnings == null)
            return "";

        int uniqueRemaining = 4 - warnings.size();
        int similarRemaining = 0;

        for(Warning w : warnings) {
            if (w.getOffenseType().equals(current.getOffenseType()))
                similarRemaining++;
        }

        similarRemaining = 2 - similarRemaining;

        String result = MarkdownUtil.bold("You have received a warning from the DRACONIUM staff team") + "\n" +
                "\n" +
                "Offense: " + current.getOffenseType().getShortName() + " - " + current.getOffenseType().getDescription() + "\n" +
                "\n" +
                "Additional Comments:\n" +
                current.getAdditionalComments() + "\n" +
                "\n" +
                "You have " + MarkdownUtil.monospace(String.valueOf(uniqueRemaining)) + " unique warnings left before a kick or " + MarkdownUtil.monospace(String.valueOf(similarRemaining)) + " similar warnings before a kick.\n" +
                "\n" +
                "If you have any questions, please contact a moderator";

        return result;
    }
}

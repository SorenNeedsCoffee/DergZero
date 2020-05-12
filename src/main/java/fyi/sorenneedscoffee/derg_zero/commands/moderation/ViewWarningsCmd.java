package fyi.sorenneedscoffee.derg_zero.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.ModCommand;
import fyi.sorenneedscoffee.derg_zero.moderation.util.ModUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.util.TimeUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.util.WarningUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class ViewWarningsCmd extends ModCommand {

    public ViewWarningsCmd() {
        this.name = "viewwarnings";
        this.aliases = new String[]{"vws"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        User target = ModUtil.getTarget(args);

        var warnings = WarningUtil.getWarnings(target.getId(), true);

        StringBuilder sb = new StringBuilder();

        sb.append("Id: " + warnings.get(0).getId() + "\n" +
                "Issued at: " + TimeUtil.formatter.format(warnings.get(0).getCreationTime().toLocalDateTime()) + " UTC" + "\n" +
                "Offense Type: " + warnings.get(0).getOffenseType().getShortName());
        for(int i = 1; i < warnings.size(); i++) {
            sb.append("\n\n");
            sb.append("Id: " + warnings.get(i).getId() + "\n" +
                    "Issued at: " + TimeUtil.formatter.format(warnings.get(i).getCreationTime().toLocalDateTime()) + " UTC" + "\n" +
                    "Offense Type: " + warnings.get(i).getOffenseType().getShortName());
        }

        event.reply(MarkdownUtil.codeblock(sb.toString()));
    }
}

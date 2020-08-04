package fyi.sorenneedscoffee.garbagecan.commands.moderation;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.commands.ModCommand;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.util.WarningUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ViewWarningsCmd extends ModCommand {

    public ViewWarningsCmd() {
        super();
        this.name = "viewwarnings";
        this.aliases = new String[]{"vws"};
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        User target = ModUtil.getTarget(args);

        var warnings = WarningUtil.getWarnings(target.getId(), true);

        StringBuilder sb = new StringBuilder();

        sb.append("Id: ").append(warnings.get(0).getId()).append("\n")
                .append("Issued at: ").append(
                        ModUtil.formatter.format(
                                warnings.get(0).getCreationTime().toLocalDateTime()
                        )
                ).append(" UTC").append("\n")
                .append("Offense Type: ").append(warnings.get(0).getOffenseType().getShortName());
        for (int i = 1; i < warnings.size(); i++) {
            sb.append("\n\n");
            sb.append("Id: ").append(warnings.get(i).getId()).append("\n")
                    .append("Issued at: ").append(
                            ModUtil.formatter.format(
                                    warnings.get(i).getCreationTime().toLocalDateTime()
                            )
                    ).append(" UTC").append("\n")
                    .append("Offense Type: ").append(warnings.get(i).getOffenseType().getShortName());
        }

        event.reply(MarkdownUtil.codeblock(sb.toString()));
    }
}

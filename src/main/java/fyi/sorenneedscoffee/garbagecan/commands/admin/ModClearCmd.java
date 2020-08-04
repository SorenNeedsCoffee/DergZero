package fyi.sorenneedscoffee.garbagecan.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.commands.AdminCommand;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import fyi.sorenneedscoffee.garbagecan.moderation.util.WarningUtil;
import net.dv8tion.jda.api.entities.User;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ModClearCmd extends AdminCommand {

    public ModClearCmd() {
        this.name = "clear";
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        User target = ModUtil.getTarget(args);

        WarningUtil.clearModerationHistory(target.getId());
    }
}

package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;
import fyi.sorenneedscoffee.derg_zero.moderation.util.ModUtil;
import fyi.sorenneedscoffee.derg_zero.moderation.util.WarningUtil;
import net.dv8tion.jda.api.entities.User;

public class ModClearCmd extends AdminCommand {

    public ModClearCmd() {
        this.name = "clear";
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        User target = ModUtil.getTarget(args);

        WarningUtil.clearModerationHistory(target.getId());
    }
}

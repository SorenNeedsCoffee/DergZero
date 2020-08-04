package fyi.sorenneedscoffee.garbagecan.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.commands.OwnerCommand;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ShutdownCmd extends OwnerCommand {
    public ShutdownCmd() {
        super();
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.aliases = new String[]{"off"};
        this.guildOnly = false;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage("\u26A0 Shutting down...").complete();
        System.exit(0);
    }
}

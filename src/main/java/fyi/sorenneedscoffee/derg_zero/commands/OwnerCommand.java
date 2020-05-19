package fyi.sorenneedscoffee.derg_zero.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public abstract class OwnerCommand extends Command {
    protected OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}

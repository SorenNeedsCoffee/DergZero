package fyi.sorenneedscoffee.derg_zero.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public abstract class OwnerCommand extends Command {
    protected OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}

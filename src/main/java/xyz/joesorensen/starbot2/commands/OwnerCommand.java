package xyz.joesorensen.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public abstract class OwnerCommand extends Command {
    protected OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}

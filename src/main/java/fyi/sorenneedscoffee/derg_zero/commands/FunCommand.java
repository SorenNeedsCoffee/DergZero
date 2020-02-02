package fyi.sorenneedscoffee.derg_zero.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public abstract class FunCommand extends Command {

    protected FunCommand() {

        this.category = new Category("Fun");

    }

}

package fyi.sorenneedscoffee.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public abstract class FunCommand extends Command {

    protected FunCommand() {

        this.category = new Category("Fun");

    }

}

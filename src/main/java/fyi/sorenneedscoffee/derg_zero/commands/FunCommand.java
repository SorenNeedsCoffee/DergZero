package fyi.sorenneedscoffee.derg_zero.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public abstract class FunCommand extends Command {

    protected FunCommand() {

        this.category = new Category("Fun");

    }

}

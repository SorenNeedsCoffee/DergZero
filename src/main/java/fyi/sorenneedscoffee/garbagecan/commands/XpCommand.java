package fyi.sorenneedscoffee.garbagecan.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public abstract class XpCommand extends Command {

    protected XpCommand() {

        this.category = new Category("User");
        this.guildOnly = true;

    }

}

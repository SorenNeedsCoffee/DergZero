package fyi.sorenneedscoffee.derg_zero.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.derg_zero.DergZero;
import fyi.sorenneedscoffee.derg_zero.commands.AdminCommand;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class NewScriptCmd extends AdminCommand {

    public NewScriptCmd() {
        this.name = "newscript";
        this.aliases = new String[]{"ns"};
        this.help = "forces script reset in script chain";
    }

    @Override
    protected void execute(CommandEvent event) {
        DergZero.script.newScript();
    }
}

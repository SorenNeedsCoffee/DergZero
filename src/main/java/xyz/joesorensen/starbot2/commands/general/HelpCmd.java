package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;

import java.util.List;
import java.util.function.Consumer;

public class HelpCmd implements Consumer<CommandEvent> {
    @Override
    public void accept(CommandEvent event) {
        List<Command> commands = event.getClient().getCommands();
        StringBuilder builder = new StringBuilder();
        builder.append("```\n");
        for(Command command : commands) {
            builder.append(event.getClient().getPrefix() + command.getName() + " ");
            builder.append("| " + command.getArguments());
        }
        builder.append("```");
    }
}

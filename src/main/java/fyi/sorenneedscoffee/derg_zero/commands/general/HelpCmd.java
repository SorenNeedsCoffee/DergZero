package fyi.sorenneedscoffee.derg_zero.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class HelpCmd implements Consumer<CommandEvent> {
    @Override
    public void accept(CommandEvent event) {
        List<Command> commands = event.getClient().getCommands();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Commands for DergZero");
        builder.setColor(Color.decode("#fc3003"));
        StringBuilder list = new StringBuilder();
        for (Command command : commands) {
            list.append("```ini\n");
            list.append(command.getName());
            if (command.getAliases().length > 0 && !command.isHidden())
                list.append(" ").append(Arrays.toString(command.getAliases()));
            list.append(" | ")
                    .append(command.getHelp());

            if (!(command.getArguments() == null || command.getArguments().isBlank() || command.getArguments().isEmpty()))
                list.append(" | <").append(command.getArguments()).append(">");

            list.append("```");
        }
        builder.addField("", list.toString(), true);


        if (event.getChannelType().isGuild()) {
            event.replyInDm(builder.build());
        } else {
            event.reply(builder.build());
        }
    }

}

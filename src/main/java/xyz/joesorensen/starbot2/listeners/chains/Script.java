package xyz.joesorensen.starbot2.listeners.chains;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Script extends ListenerAdapter {
    private ScriptManager man;
    private boolean toPurge = false;

    @Override
    public void onReady(ReadyEvent event) {
        man = new ScriptManager();
        if (!man.isActive()) {
            toPurge = true;
            man.newScript();
        }

        event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord() + " [" + man.index() + "/" + man.length() + "]").queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (!event.getChannel().getId().equals("663544151547314255"))
            return;

        if (man.checkMsg(event.getMessage().getContentDisplay())) {
            if (man.next()) {
                List<Message> messages;
                messages = getMsgs(event.getChannel());

                HashMap<User, Integer> messageCounts = new HashMap<>();

                for (Message message : messages) {
                    User user = message.getAuthor();
                    if (messageCounts.containsKey(user)) {
                        messageCounts.put(user, messageCounts.get(user) + 1);
                    } else {
                        messageCounts.put(user, 0);
                    }
                }

                for (User user : messageCounts.keySet()) {
                    event.getChannel().sendMessage("User: " + user.getName() + " Total Messages: " + messageCounts.get(user)).queue();
                    if(messageCounts.get(user)/man.length() >= 0.1) {
                        event.getGuild().addRoleToMember(user.getId(), event.getGuild().getRoleById("663947663137308713")).queue();
                        event.getChannel().sendMessage(user.getName() + " Earned the **" + event.getGuild().getRoleById("663947663137308713").getName() + "** Role!").queue();
                    }
                }

                event.getChannel().sendMessage("New Script will start in 20 minutes.");

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        event.getChannel().purgeMessages(messages);
                        man.newScript();
                    }
                }, 1200000);

            }
            event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord() + " [" + man.index() + "/" + man.length() + "]").queue();
        } else {
            if (toPurge) {
                List<Message> messages = getMsgs(event.getChannel());
                event.getChannel().purgeMessages(messages);
                toPurge = false;
            } else
                event.getMessage().delete().queue();
        }
    }

    private List<Message> getMsgs(MessageChannel channel) {
        List<Message> messages = new ArrayList<>(20000);
        for (Message message : channel.getIterableHistory().cache(false)) {
            messages.add(message);
        }
        return messages;
    }
}
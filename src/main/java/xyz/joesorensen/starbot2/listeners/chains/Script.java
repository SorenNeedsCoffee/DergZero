package xyz.joesorensen.starbot2.listeners.chains;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.joesorensen.xputil.util.XpListener;

import java.text.DecimalFormat;
import java.util.*;

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

        event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord() + " [" + new DecimalFormat("##,###.##").format(man.index()-1) + "/" + new DecimalFormat("##,###.##").format(man.length()) + "]").queue();
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
                    if(!user.isBot() || !user.isFake()) {
                        if (messageCounts.containsKey(user)) {
                            messageCounts.put(user, messageCounts.get(user) + 1);
                        } else {
                            messageCounts.put(user, 0);
                        }
                    }
                }

                for (User user : messageCounts.keySet()) {
                    event.getChannel().sendMessage("User: " + user.getName() + " Total Messages: " + messageCounts.get(user)).queue();
                    XpListener.addXP(user, Math.pow((double) messageCounts.get(user), 0.8));
                    if( (double) messageCounts.get(user)/man.length() >= 0.1) {
                        event.getGuild().addRoleToMember(user.getId(), event.getGuild().getRoleById("663947663137308713")).queue();
                        event.getChannel().sendMessage(user.getName() + " Earned the **" + event.getGuild().getRoleById("663947663137308713").getName() + "** Role!").queue();
                    }
                }

                event.getChannel().sendMessage("New Script will start in 20 minutes.");
                event.getChannel().getPermissionOverride(event.getGuild().getRoles().get(0)).getManager().deny(Permission.MESSAGE_WRITE).queue();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        event.getChannel().purgeMessages(messages);
                        event.getChannel().getPermissionOverride(event.getGuild().getRoles().get(0)).getManager().grant(Permission.MESSAGE_WRITE).queue();
                        man.newScript();
                    }
                }, 1200000);

            }
        } else {
            if (toPurge) {
                List<Message> messages = getMsgs(event.getChannel());
                event.getChannel().purgeMessages(messages);
                toPurge = false;
            } else
                event.getMessage().delete().queue();
        }
        event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord() + " [" + new DecimalFormat("##,###.##").format(man.index()-1) + "/" + new DecimalFormat("##,###.##").format(man.length()) + "]").queue();
    }

    private List<Message> getMsgs(MessageChannel channel) {
        List<Message> messages = new ArrayList<>(20000);
        for (Message message : channel.getIterableHistory().cache(false)) {
            messages.add(message);
        }
        return messages;
    }
}
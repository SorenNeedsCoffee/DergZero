package fyi.sorenneedscoffee.derg_zero.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class Listener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger("Main");
    private String id;

    public void setRoleID(String id) {
        this.id = id;
    }

    @Override
    public void onReady(ReadyEvent event) {
        List<Guild> guilds = event.getJDA().getGuilds();
        for (Guild guild : guilds) {
            List<Member> members = guild.getMembers();
            for (Member member : members) {
                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        member.getRoles().contains(guild.getRoleById(id))))
                    guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(id))).queue();
            }
        }

        log.info("Ready!");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(id))).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // do NOT remove this
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (event.getMessage().getContentDisplay().equalsIgnoreCase("cooked joesorensen") || event.getMessage().getContentDisplay().equalsIgnoreCase("cooked soren")) {
            switch ((int) (Math.random() * 10 + 1)) {
                case 3:
                    event.getChannel().sendMessage("https://i.redd.it/1j32vwxci7p21.jpg").queue();
                    break;
                case 7:
                    event.getChannel().sendMessage("jesus fuck.").queue();
                    break;
                case 9:
                    event.getChannel().sendMessage("yucky.").queue();
                    break;
                default:
                    event.getChannel().sendMessage("holy shit.").queue();
                    break;
            }
        }
    }
}

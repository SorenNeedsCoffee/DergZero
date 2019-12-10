package xyz.joesorensen.starbot2.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.joesorensen.starbot2.StarBot2;

import java.util.List;
import java.util.Objects;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class Listener extends ListenerAdapter {
    private final Logger log;
    private JDA jda;
    private String id;
    private String prefix;

    public Listener() {
        this.log = LoggerFactory.getLogger("Main");
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void setRoleID(String id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Ready!");
        List<Guild> guilds = event.getJDA().getGuilds();
        for (Guild guild : guilds) {
            List<Member> members = guild.getMembers();
            for (Member member : members) {
                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        member.getRoles().contains(guild.getRoleById(id))))
                    guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(id))).queue();
            }
        }
        StarBot2.twitchListener.track("JoeSorensen");
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

        if (event.getChannel() == jda.getTextChannelById("506503200866697226") && !event.getMessage().getContentDisplay().equalsIgnoreCase("hi"))
            event.getMessage().delete().queue();

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

    public void onLive(Message embed) {
        log.info("live!");
        jda.getPresence().setActivity(Activity.streaming("JoeSorensen is live!", "https://twitch.tv/joesorensen"));
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("442552203694047232")).getTextChannelById("442556155856814080")).sendMessage(embed).queue();
    }

    public void onOffline() {
        log.info("offline");
        jda.getPresence().setActivity(Activity.playing("On Soren's server | " + prefix + "help for help"));
    }
}

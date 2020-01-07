package xyz.joesorensen.xputil.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.joesorensen.xputil.lib.LvlRoleIDs;
import xyz.joesorensen.xputil.lib.XpInfo;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class XpListener extends ListenerAdapter {
    private static JDA jda;
    private Logger log;
    private List<String> cooldown = new ArrayList<>();
    private Timer timer = new Timer();

    public XpListener() {
        this.log = LoggerFactory.getLogger("XpUtil");
    }

    public static void replaceRole(Guild guild, Member member, String regex, String replace) {
        Objects.requireNonNull(guild).removeRoleFromMember(member, Objects.requireNonNull(jda.getRoleById(regex))).queue();
        Objects.requireNonNull(guild).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(replace))).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Getting things ready...");
        List<Guild> guilds = event.getJDA().getGuilds();
        File membersFile = new File("members.json");
        if (membersFile.exists()) {
            UserManager.loadFile();
            membersFile.delete();
        }
        for (Guild guild : guilds) {
            List<Member> members = guild.getMembers();
            for (Member member : members) {
                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        UserManager.getUser(member.getId()) != null))
                    UserManager.addUser(member.getId());

                if (!member.getRoles().contains(guild.getRoleById(LvlRoleIDs.LVL1.getId())) &&
                        !(member.getUser().isBot() || member.getUser().isFake()) &&
                        (Objects.requireNonNull(UserManager.getUser(member.getId())).getLvl() < 5))
                    guild.addRoleToMember(member, jda.getRoleById(LvlRoleIDs.LVL1.getId())).queue();
            }
        }
        jda = event.getJDA();
        log.info("XPUtil version 0.2 ready");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(LvlRoleIDs.LVL1.getId()))).queue();
            UserManager.addUser(event.getMember().getId());
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake()))
            UserManager.removeUser(event.getMember().getId());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (cooldown.indexOf(event.getAuthor().getId()) == -1 || !event.getChannel().getParent().getId().equals("506503114845716490")) {
            User update = UserManager.getUser(event.getAuthor().getId());
            update.addXp(XpInfo.earnedXP(event.getMessage().getContentDisplay().replaceAll(" ", "")));
            if (update.getXp() >= XpInfo.lvlXpRequirementTotal(update.getLvl())) {
                onLvlUp(event.getAuthor(), update);
            }
            UserManager.updateUser(update);
            cooldown.add(event.getAuthor().getId());
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cooldown.remove(event.getAuthor().getId());
                }
            }, 5000);
        }
    }

    public static void addXP(net.dv8tion.jda.api.entities.User user, double amt) {
        User update = UserManager.getUser(user.getId());
        update.addXp(amt);
        if (update.getXp() >= XpInfo.lvlXpRequirementTotal(update.getLvl())) {
            onLvlUp(user, update);
        }
        UserManager.updateUser(update);
    }

    static void onLvlUp(net.dv8tion.jda.api.entities.User user, User update) {
        update.setLvl(update.getLvl() + 1);

        EmbedBuilder embed = new EmbedBuilder();
        float[] rgb;

        embed.setAuthor("Level Up!", null, user.getAvatarUrl());
        if (jda.getGuildById("442552203694047232").getMember(user).getNickname() != null) {
            embed.setDescription("Congrats to " + jda.getGuildById("442552203694047232").getMember(user).getNickname() + " for reaching level " + update.getLvl() + "!");
        } else {
            embed.setDescription("Congrats to " + user.getName() + " for reaching level " + update.getLvl() + "!");
        }
        rgb = Color.RGBtoHSB(204, 255, 94, null);
        embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        jda.getGuildById("442552203694047232").getTextChannelById("664089444126687242").sendMessage(embed.build()).queue();

        switch (update.getLvl()) {
            case 5:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL1.getId(), LvlRoleIDs.LVL5.getId());
                break;
            case 10:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL5.getId(), LvlRoleIDs.LVL10.getId());
                break;
            case 15:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL10.getId(), LvlRoleIDs.LVL15.getId());
                break;
            case 20:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL15.getId(), LvlRoleIDs.LVL20.getId());
                break;
            case 25:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL20.getId(), LvlRoleIDs.LVL25.getId());
                break;
            case 30:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL25.getId(), LvlRoleIDs.LVL30.getId());
                break;
            case 35:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL30.getId(), LvlRoleIDs.LVL35.getId());
                break;
            case 40:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL35.getId(), LvlRoleIDs.LVL40.getId());
                break;
            case 45:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL40.getId(), LvlRoleIDs.LVL45.getId());
                break;
            case 50:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL45.getId(), LvlRoleIDs.LVL50.getId());
                break;
            case 55:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL50.getId(), LvlRoleIDs.LVL55.getId());
                break;
            case 60:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL55.getId(), LvlRoleIDs.LVL60.getId());
                break;
            case 65:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL60.getId(), LvlRoleIDs.LVL65.getId());
                break;
            case 70:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL65.getId(), LvlRoleIDs.LVL70.getId());
                break;
            case 75:
                replaceRole(jda.getGuildById("442552203694047232"), jda.getGuildById("442552203694047232").getMember(user), LvlRoleIDs.LVL70.getId(), LvlRoleIDs.LVL75.getId());
                break;

        }
    }
}

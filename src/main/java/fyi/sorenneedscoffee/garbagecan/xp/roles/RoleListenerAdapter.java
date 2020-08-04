package fyi.sorenneedscoffee.garbagecan.xp.roles;

import fyi.sorenneedscoffee.garbagecan.xp.data.implementations.JSONRoleDataContext;
import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRoleList;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class RoleListenerAdapter extends ListenerAdapter {

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        RoleUtil.init(new JSONRoleDataContext(new File("roles.json")), event.getJDA().getGuilds());
        for (Guild guild : event.getJDA().getGuilds()) {
            if (!RoleUtil.exists(guild.getId())) {
                LevelRoleList list = new LevelRoleList();
                RoleUtil.put(guild.getId(), list);
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            if (RoleUtil.exists(event.getGuild().getId()) && !RoleUtil.getList(event.getGuild().getId()).isEmpty()) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(RoleUtil.getList(event.getGuild().getId()).get(0).getRoleID())).queue();
            }
        }
    }
}

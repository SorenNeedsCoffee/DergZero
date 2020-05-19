package fyi.sorenneedscoffee.derg_zero.xp.roles;

import fyi.sorenneedscoffee.xputil.events.user.MissedUserEvent;
import fyi.sorenneedscoffee.xputil.events.xp.LevelUpEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class RoleListener extends fyi.sorenneedscoffee.xputil.listener.XPListener {
    private JDA jda;

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onLevelUp(LevelUpEvent event) {
        Guild guild = jda.getGuildById(event.getGroupId());
        User user = jda.getUserById(event.getUserId());
        RoleUtil.getList(guild.getId()).updateRole(guild, user, event.getNewLevel());
    }

    @Override
    public void onMissedUser(MissedUserEvent event) {
        Guild guild = jda.getGuildById(event.getGroupId());
        if (RoleUtil.exists(guild.getId()) && !RoleUtil.getList(guild.getId()).isEmpty()) {
            guild.addRoleToMember(guild.getMemberById(event.getUserId()), guild.getRoleById(RoleUtil.getList(guild.getId()).get(0).getRoleID())).queue();
        }
    }
}

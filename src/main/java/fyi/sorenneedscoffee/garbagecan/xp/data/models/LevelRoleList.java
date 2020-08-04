package fyi.sorenneedscoffee.garbagecan.xp.data.models;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class LevelRoleList extends ArrayList<LevelRole> {

    private void doSort() {
        this.sort((a, b) -> {
            int difference = b.getLevel() - a.getLevel();

            return -Integer.compare(difference, 0);
        });
    }

    @Override
    public boolean add(LevelRole levelRole) {
        boolean add = super.add(levelRole);
        doSort();
        return add;
    }

    public void updateRole(Guild guild, User user, int level) {
        int index = 0;
        for (LevelRole levelRole : this) {
            if (levelRole.getLevel() == level) {
                LevelRole toRemove = index > 0 ? get(index - 1) : null;
                replaceRole(guild, guild.getMember(user), toRemove != null ? toRemove.getRoleID() : null, levelRole.getRoleID());
                return;
            }

            index++;
        }
    }

    private void replaceRole(Guild guild, Member member, String oldRoleId, String newRoleID) {
        Role oldRole = guild.getRoleById(oldRoleId);
        Role newRole = guild.getRoleById(newRoleID);
        guild.removeRoleFromMember(member, oldRole)
                .flatMap((success) -> guild.addRoleToMember(member, newRole))
                .queue();
    }
}

package fyi.sorenneedscoffee.garbagecan.xp.roles;


import fyi.sorenneedscoffee.garbagecan.xp.data.RoleDataContext;
import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RemoveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RetrieveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.SaveListRequest;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.List;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class RoleUtil {
    private static final HashMap<String, LevelRoleList> roleLists = new HashMap<>();
    private static RoleDataContext context;

    public static void init(RoleDataContext context, List<Guild> guilds) {
        RoleUtil.context = context;
        for (Guild guild : guilds) {
            LevelRoleList list = context.retrieveList(new RetrieveListRequest(guild.getId()));
            if (list != null)
                put(guild.getId(), list);
        }
    }

    public static LevelRoleList getList(String guildId) {
        return roleLists.getOrDefault(guildId, null);
    }

    public static boolean exists(String guildId) {
        return roleLists.containsKey(guildId);
    }

    public static void put(String guildId, LevelRoleList list) {
        roleLists.put(guildId, list);
        context.saveList(new SaveListRequest(guildId, list));
    }

    public static void remove(String guildId) {
        roleLists.remove(guildId);
        context.removeList(new RemoveListRequest(guildId));
    }
}

package fyi.sorenneedscoffee.garbagecan.xp.data.requests;

import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRoleList;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class UpdateListRequest {
    private final String groupId;
    private final LevelRoleList list;

    public UpdateListRequest(String groupId, LevelRoleList list) {
        this.groupId = groupId;
        this.list = list;
    }

    public String getGroupId() {
        return groupId;
    }

    public LevelRoleList getList() {
        return list;
    }
}

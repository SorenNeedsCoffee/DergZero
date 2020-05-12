package fyi.sorenneedscoffee.derg_zero.xp.data.requests;

import fyi.sorenneedscoffee.derg_zero.xp.data.models.LevelRoleList;

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

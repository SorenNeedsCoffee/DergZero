package fyi.sorenneedscoffee.derg_zero.xp.data.requests;

public class RemoveListRequest {
    private final String groupId;

    public RemoveListRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}

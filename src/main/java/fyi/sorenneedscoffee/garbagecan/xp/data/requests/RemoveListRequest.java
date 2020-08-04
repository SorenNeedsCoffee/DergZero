package fyi.sorenneedscoffee.garbagecan.xp.data.requests;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class RemoveListRequest {
    private final String groupId;

    public RemoveListRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}

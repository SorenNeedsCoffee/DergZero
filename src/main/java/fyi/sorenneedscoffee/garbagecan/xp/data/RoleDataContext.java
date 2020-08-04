package fyi.sorenneedscoffee.garbagecan.xp.data;


import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RemoveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RetrieveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.SaveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.UpdateListRequest;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public interface RoleDataContext {
    void saveList(SaveListRequest request);

    LevelRoleList retrieveList(RetrieveListRequest request);

    void updateList(UpdateListRequest request);

    void removeList(RemoveListRequest request);
}

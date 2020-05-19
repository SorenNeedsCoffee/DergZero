package fyi.sorenneedscoffee.derg_zero.xp.data;


import fyi.sorenneedscoffee.derg_zero.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.RemoveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.RetrieveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.SaveListRequest;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.UpdateListRequest;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public interface RoleDataContext {
    void saveList(SaveListRequest request);

    LevelRoleList retrieveList(RetrieveListRequest request);

    void updateList(UpdateListRequest request);

    void removeList(RemoveListRequest request);
}

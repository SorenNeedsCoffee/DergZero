package fyi.sorenneedscoffee.derg_zero.xp.data;


import fyi.sorenneedscoffee.derg_zero.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.derg_zero.xp.data.requests.*;

public interface RoleDataContext {
    void saveList(SaveListRequest request);

    LevelRoleList retrieveList(RetrieveListRequest request);

    void updateList(UpdateListRequest request);

    void removeList(RemoveListRequest request);
}

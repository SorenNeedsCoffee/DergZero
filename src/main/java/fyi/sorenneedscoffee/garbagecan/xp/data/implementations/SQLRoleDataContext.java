package fyi.sorenneedscoffee.garbagecan.xp.data.implementations;

import fyi.sorenneedscoffee.garbagecan.xp.data.RoleDataContext;
import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRole;
import fyi.sorenneedscoffee.garbagecan.xp.data.models.LevelRoleList;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RemoveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.RetrieveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.SaveListRequest;
import fyi.sorenneedscoffee.garbagecan.xp.data.requests.UpdateListRequest;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLRoleDataContext implements RoleDataContext {
    private final DSLContext context = DSL.using(SQLDialect.MYSQL);
    private final String url;
    private final String[] creds;

    private final Table<Record> table;
    private final Field<String> field_groupId;
    private final Field<Integer> field_lvl;
    private final Field<String> field_roleId;

    public SQLRoleDataContext(String url) {
        this.url = url.replaceAll("(\\w+:\\w+)@", "");
        Matcher matcher = Pattern.compile("(\\w+:\\w+)@")
                .matcher(url);
        if (!matcher.find())
            throw new IllegalArgumentException("No username or password was found");
        creds = matcher.group(1).split(":");

        try {
            if (!DriverManager.getConnection(this.url, creds[0], creds[1]).isValid(0))
                throw new IllegalArgumentException();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.table = DSL.table("roles");
        this.field_groupId = DSL.field("group_id", String.class);
        this.field_lvl = DSL.field("lvl", Integer.class);
        this.field_roleId = DSL.field("role_id", String.class);
    }

    @Override
    public void saveList(SaveListRequest request) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            InsertValuesStep3<Record, String, Integer, String> query = context.insertInto(table,
                    field_groupId, field_lvl, field_roleId);

            for (LevelRole role : request.getList()) {
                query = query.values(request.getGroupId(), role.getLevel(), role.getRoleID());
            }

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (Exception e) {}
    }

    @Override
    public LevelRoleList retrieveList(RetrieveListRequest request) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.select()
                    .from(table)
                    .where(field_groupId.eq(request.getGroupId()));

            Result<Record> result = context.fetch(conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED)));

            if (result.isEmpty())
                return null;

            LevelRoleList list = new LevelRoleList();

            for (Record r : result) {
                list.add(new LevelRole(r.get(field_lvl), r.get(field_roleId)));
            }

            return list;
        } catch (Exception e) {}

        return null;
    }

    @Override
    public void updateList(UpdateListRequest request) {

    }

    @Override
    public void removeList(RemoveListRequest request) {

    }
}

package fyi.sorenneedscoffee.garbagecan.xp.data;

import fyi.sorenneedscoffee.xputil.data.DataContext;
import fyi.sorenneedscoffee.xputil.data.models.Group;
import fyi.sorenneedscoffee.xputil.data.models.User;
import fyi.sorenneedscoffee.xputil.data.requests.*;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class SQLDataContext implements DataContext {
    private final DSLContext context = DSL.using(SQLDialect.MYSQL);
    private final String url;
    private final String[] creds;

    private final Table<Record> table;
    private final Field<String> field_groupId;
    private final Field<String> field_userId;
    private final Field<Integer> field_lvl;
    private final Field<Double> field_xp;

    public SQLDataContext(String url) {
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

        table = table("USERS");
        field_groupId = field("USERS.GROUP_ID", String.class);
        field_userId = field("USERS.USER_ID", String.class);
        field_lvl = field("USERS.LVL", Integer.class);
        field_xp = field("USERS.XP", Double.class);
    }

    @Override
    public void saveMember(SaveMemberRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.insertInto(table,
                    field_groupId, field_userId, field_lvl, field_xp)
                    .values(request.getGroupId(), request.getUserId(), 1, 0.0);

            conn.createStatement().executeUpdate(query.getSQL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User retrieveMember(RetrieveMemberRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.selectFrom(table)
                    .where(field_groupId.eq(request.getGroupId()))
                    .and(field_userId.eq(request.getUserId()));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL());

            return constructUser(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Group retrieveGroup(RetrieveGroupRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.selectFrom(table)
                    .where(field_groupId.eq(request.getGroupId()));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL());
            List<User> users = new ArrayList<>();

            while (set.next()) {
                users.add(constructUser(set));
            }

            return new Group(request.getGroupId(), users);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateMember(UpdateMemberRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.update(table)
                    .set(field_lvl, request.getNewLevel())
                    .set(field_xp, request.getNewXp())
                    .where(field_groupId.eq(request.getGroupId()))
                    .and(field_userId.eq(request.getUserId()));

            conn.createStatement().executeUpdate(query.getSQL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMember(RemoveMemberRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.deleteFrom(table)
                    .where(field_groupId.eq(request.getGroupId()))
                    .and(field_userId.eq(request.getUserId()));

            conn.createStatement().executeUpdate(query.getSQL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeGroup(RemoveGroupRequest request) {
        try(Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.deleteFrom(table)
                    .where(field_groupId.eq(request.getGroupId()));

            conn.createStatement().executeUpdate(query.getSQL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User constructUser(ResultSet set) throws SQLException {
        String groupId = set.getString("group_id");
        String userId = set.getString("user_id");
        int lvl = set.getInt("lvl");
        double xp = set.getDouble("xp");

        return new User(groupId, userId, lvl, xp);
    }
}

package fyi.sorenneedscoffee.garbagecan.boosters.data;

import fyi.sorenneedscoffee.garbagecan.boosters.data.models.Booster;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.QueuedBooster;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.UserBooster;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class DataContext {
    private final DSLContext context;
    private final Random random = new Random();
    private final String url;

    private final Table<Record> aTable;
    private final Field<String> aField_slot_id;
    private final Field<Float> aField_multiplier;
    private final Field<Timestamp> aField_expiration;

    private final Table<Record> qTable;
    private final Field<Integer> qField_id;
    private final Field<Float> qField_multiplier;
    private final Field<Long> qField_duration;
    private final Field<String> qField_time_unit;

    private final Table<Record> uTable;
    private final Field<Integer> uField_id;
    private final Field<String> uField_user_id;
    private final Field<Float> uField_multiplier;
    private final Field<Long> uField_duration;
    private final Field<String> uField_time_unit;

    public DataContext(String url) {
        this.url = "jdbc:" + url;

        this.context = DSL.using(SQLDialect.MARIADB);

        aTable = table("active_boosters");
        aField_slot_id = field("slot_id", String.class);
        aField_multiplier = field("multiplier", Float.class);
        aField_expiration = field("expiration", Timestamp.class);

        qTable = table("queued_boosters");
        qField_id = field("id", Integer.class);
        qField_multiplier = field("multiplier", Float.class);
        qField_duration = field("duration", Long.class);
        qField_time_unit = field("time_unit", String.class);

        uTable = table("user_boosters");
        uField_id = field("id", Integer.class);
        uField_user_id = field("user_id", String.class);
        uField_multiplier = field("multiplier", Float.class);
        uField_duration = field("duration", Long.class);
        uField_time_unit = field("time_unit", String.class);
    }


    public List<QueuedBooster> getQueue() {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.selectFrom(qTable);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.beforeFirst();

            List<QueuedBooster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new QueuedBooster(
                                set.getInt("id"),
                                set.getFloat("multiplier"),
                                set.getLong("duration"),
                                ChronoUnit.valueOf(set.getString("time_unit"))
                        )
                );
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Booster> getBoosters() {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.selectFrom(aTable);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));

            List<Booster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new Booster(
                                set.getString("slot_id"),
                                set.getFloat("multiplier"),
                                set.getTimestamp("expiration").toLocalDateTime()
                        )
                );
            }

            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public void saveActiveBooster(Booster booster) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.insertInto(aTable, aField_slot_id, aField_multiplier, aField_expiration)
                    .values(booster.slotId, booster.multiplier, Timestamp.valueOf(booster.expiration));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeActiveBooster(String slotId) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.delete(aTable)
                    .where(aField_slot_id.eq(slotId));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveQueuedBooster(QueuedBooster booster) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.insertInto(qTable, qField_id, qField_multiplier, qField_duration, qField_time_unit)
                    .values(booster.id, booster.multiplier, booster.duration, booster.unit.name());

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeQueuedBooster(int id) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.deleteFrom(qTable)
                    .where(qField_id.eq(id));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveUserBooster(UserBooster booster) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.insertInto(uTable, uField_id, uField_user_id, uField_multiplier, uField_duration, uField_time_unit)
                    .values(booster.id, booster.userId, booster.multiplier, booster.duration, booster.unit.name());

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public UserBooster getUserBooster(int id) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.selectFrom(uTable)
                    .where(uField_id.eq(id));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.first();

            return new UserBooster(
                    set.getInt("id"),
                    set.getString("user_id"),
                    set.getFloat("multiplier"),
                    set.getLong("duration"),
                    ChronoUnit.valueOf(set.getString("time_unit"))
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<UserBooster> getUserBoosters(String uId) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.selectFrom(uTable)
                    .where(uField_user_id.eq(uId));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.beforeFirst();

            List<UserBooster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new UserBooster(
                                set.getInt("id"),
                                set.getString("user_id"),
                                set.getFloat("multiplier"),
                                set.getLong("duration"),
                                ChronoUnit.valueOf(set.getString("time_unit"))
                        )
                );
            }

            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public void removeUserBooster(int id) {
        try (Connection conn = DriverManager.getConnection(url)) {
            Query query = context.deleteFrom(uTable)
                    .where(uField_id.eq(id));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getNewQId() {
        return generateId(qTable, qField_id);
    }

    public int getNewUId() {
        return generateId(uTable, uField_id);
    }

    private int generateId(Table<Record> uTable, Field<Integer> uField_id) {
        try (Connection conn = DriverManager.getConnection(url)) {
            DSLContext temp = DSL.using(conn);
            boolean isValid = false;
            int newId = 0;

            while (!isValid) {
                newId = random.nextInt();
                isValid = !temp.fetchExists(uTable, uField_id.eq(newId));
            }

            return newId;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return -1;
    }
}

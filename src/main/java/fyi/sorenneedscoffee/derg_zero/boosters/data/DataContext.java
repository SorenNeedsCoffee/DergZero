package fyi.sorenneedscoffee.derg_zero.boosters.data;

import fyi.sorenneedscoffee.derg_zero.boosters.data.models.Booster;
import fyi.sorenneedscoffee.derg_zero.boosters.data.models.QueuedBooster;
import fyi.sorenneedscoffee.derg_zero.boosters.data.models.UserBooster;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.jooq.impl.DSL.*;

public class DataContext {
    private final DSLContext context;
    private final Random random = new Random();
    private final String url, user, pass;

    private final Table<Record> table;
    private final Field<String> field_slot_id;
    private final Field<Double> field_multiplier;
    private final Field<Timestamp> field_expiration;

    private final Table<Record> qTable;
    private final Field<Integer> qField_id;
    private final Field<Double> qField_multiplier;
    private final Field<Long> qField_duration;
    private final Field<String> qField_time_unit;

    private final Table<Record> uTable;
    private final Field<Integer> uField_id;
    private final Field<String> uField_user_id;
    private final Field<Double> uField_multiplier;
    private final Field<Long> uField_duration;
    private final Field<String> uField_time_unit;


    public DataContext(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;

        this.context = DSL.using(SQLDialect.MARIADB);

        this.table = table("active_boosters");
        this.field_slot_id = field("slot_id", String.class);
        this.field_multiplier = field("multiplier", Double.class);
        this.field_expiration = field("expiration", Timestamp.class);

        this.qTable = table("queued_boosters");
        this.qField_id = field("id", Integer.class);
        this.qField_multiplier = field("multiplier", Double.class);
        this.qField_duration = field("duration", Long.class);
        this.qField_time_unit = field("time_unit", String.class);

        this.uTable = table("user_boosters");
        this.uField_id = field("id", Integer.class);
        this.uField_user_id = field("user_id", String.class);
        this.uField_multiplier = field("multiplier", Double.class);
        this.uField_duration = field("duration", Long.class);
        this.uField_time_unit = field("time_unit", String.class);
    }


    public List<QueuedBooster> getQueue() {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.selectFrom(qTable);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.beforeFirst();

            List<QueuedBooster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new QueuedBooster(
                                set.getInt("id"),
                                set.getDouble("multiplier"),
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

    public List<Booster> getBoosters() {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.selectFrom(table);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));

            List<Booster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new Booster(
                                set.getString("slot_id"),
                                set.getDouble("multiplier"),
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
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.insertInto(table, field_slot_id, field_multiplier, field_expiration)
                    .values(booster.slotId, booster.multiplier, Timestamp.valueOf(booster.expiration));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeActiveBooster(String slotId) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.delete(table)
                    .where(field_slot_id.eq(slotId));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveQueuedBooster(QueuedBooster booster) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.insertInto(qTable, qField_id, qField_multiplier, qField_duration, qField_time_unit)
                    .values(booster.id, booster.multiplier, booster.duration, booster.unit.name());

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeQueuedBooster(int id) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.deleteFrom(qTable)
                    .where(qField_id.eq(id));

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveUserBooster(UserBooster booster) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.insertInto(uTable, uField_id, uField_user_id, uField_multiplier, uField_duration, uField_time_unit)
                    .values(booster.id, booster.userId, booster.multiplier, booster.duration, booster.unit.name());

            conn.createStatement().executeUpdate(query.getSQL(ParamType.INLINED));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public UserBooster getUserBooster(int id) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.selectFrom(uTable)
                    .where(uField_id.eq(id));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.first();

            return new UserBooster(
                    set.getInt("id"),
                    set.getString("user_id"),
                    set.getDouble("multiplier"),
                    set.getLong("duration"),
                    ChronoUnit.valueOf(set.getString("time_unit"))
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<UserBooster> getUserBoosters(String uId) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Query query = context.selectFrom(uTable)
                    .where(uField_user_id.eq(uId));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.beforeFirst();

            List<UserBooster> result = new ArrayList<>();

            while (set.next()) {
                result.add(new UserBooster(
                                set.getInt("id"),
                                set.getString("user_id"),
                                set.getDouble("multiplier"),
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
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
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
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
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

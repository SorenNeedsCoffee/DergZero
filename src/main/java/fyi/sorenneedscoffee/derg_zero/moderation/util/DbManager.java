package fyi.sorenneedscoffee.derg_zero.moderation.util;

import fyi.sorenneedscoffee.derg_zero.moderation.db.tables.records.ModerationCasesRecord;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.OffenseType;
import fyi.sorenneedscoffee.derg_zero.moderation.warnings.Warning;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fyi.sorenneedscoffee.derg_zero.moderation.db.Tables.KICK_LIST;
import static fyi.sorenneedscoffee.derg_zero.moderation.db.Tables.MODERATION_CASES;


public class DbManager {
    private static String url;
    private static Logger log;

    public static void init(String ip, String db, String user, String pass) {
        url = "jdbc:mariadb://" + ip + "/" + db + "?"
                + "user=" + user + "&password=" + pass;

        log = LoggerFactory.getLogger("DbManager");
    }

    public static Warning addWarning(String uId, int offenseType, String additionalComments) {
        Timestamp stamp = Timestamp.valueOf(TimeUtil.formatter.withZone(ZoneOffset.UTC).format(Instant.now()));
        Random random = new Random();


        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            var createdResult = context.insertInto(MODERATION_CASES, MODERATION_CASES.USER_ID, MODERATION_CASES.CREATION_TIME, MODERATION_CASES.OFFENSE_ID, MODERATION_CASES.ADDITIONAL_COMMENTS)
                    .values(uId, stamp, offenseType, additionalComments)
                    .returning(MODERATION_CASES.ID)
                    .fetch();

            return getWarning(createdResult.get(0).getId());
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static void clearModerationHistory(String uId) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            context.delete(MODERATION_CASES)
                    .where(MODERATION_CASES.USER_ID.eq(uId))
                    .execute();

            context.delete(KICK_LIST)
                    .where(KICK_LIST.ID.eq(uId))
                    .execute();

        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static Warning getWarning(int id) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            var result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.ID.eq(id))
                    .fetch();

            if (result.isEmpty())
                return null;

            ModerationCasesRecord record = result.get(0).into(MODERATION_CASES);

            return new Warning(record);
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static List<Warning> getWarnings(String uId, boolean includeMisc) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            var result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.USER_ID.eq(uId))
                    .fetch();

            var warnings = createList(result);

            if (!includeMisc)
                warnings.removeIf(warning -> warning.getOffenseType().equals(OffenseType.MISC));

            return warnings;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static List<Warning> getSimilarWarnings(Warning warning) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            var result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.USER_ID.eq(warning.getuId()))
                    .and(MODERATION_CASES.OFFENSE_ID.eq(warning.getOffenseType().getId()))
                    .fetch();

            var warnings = createList(result);
            warnings.remove(warning);

            return warnings;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public static void addUserToKicklist(String uId) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            context.insertInto(KICK_LIST, KICK_LIST.ID)
                    .values(uId)
                    .execute();

        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static boolean isOnKicklist(String uId) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            var result = context.select()
                    .from(KICK_LIST)
                    .where(KICK_LIST.ID.eq(uId))
                    .fetch();

            return !result.isEmpty();
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    private static List<Warning> createList(Result<Record> result) {
        List<Warning> warnings = new ArrayList<>();

        for (Record r : result) {
            ModerationCasesRecord record = r.into(MODERATION_CASES);
            OffenseType offense = OffenseType.getTypeById(record.getOffenseId());

            warnings.add(new Warning(record.getId(), record.getUserId(), offense, record.getAdditionalComments(), record.getCreationTime()));
        }
        return warnings;
    }
}

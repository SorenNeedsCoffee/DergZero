package fyi.sorenneedscoffee.derg_zero.moderation;

import fyi.sorenneedscoffee.derg_zero.moderation.db.tables.records.ModerationCasesRecord;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static fyi.sorenneedscoffee.derg_zero.moderation.db.Tables.MODERATION_CASES;


public class DbManager {
    private static String url;
    private static Logger log;

    public static void init(String ip, String db, String user, String pass) {
        url = "jdbc:mariadb://" + ip + "/" + db + "?"
                + "user=" + user + "&password=" + pass;

        log = LoggerFactory.getLogger("DbManager");
    }

    static Warning addWarning(String uId, int offenseType, String additionalComments) {
        Timestamp stamp = Timestamp.valueOf(TimeUtil.formatter.withZone(ZoneOffset.UTC).format(Instant.now()));


        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);
            Result<ModerationCasesRecord> createdResult = context.insertInto(MODERATION_CASES, MODERATION_CASES.USER_ID, MODERATION_CASES.CREATION_TIME, MODERATION_CASES.OFFENSE_ID, MODERATION_CASES.ADDITIONAL_COMMENTS)
                    .values(uId, stamp, offenseType, additionalComments)
                    .returning(MODERATION_CASES.ID)
                    .fetch();

            Result<Record> result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.ID.eq(createdResult.get(0).getId()))
                    .fetch();

            ModerationCasesRecord record = result.get(0).into(MODERATION_CASES);

            return new Warning(record);
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    static List<Warning> getWarnings(String uId) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            Result<Record> result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.USER_ID.eq(uId))
                    .fetch();

            return createList(result);
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    static List<Warning> getSimilarWarnings(Warning warning) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);
            Result<Record> result = context.select()
                    .from(MODERATION_CASES)
                    .where(MODERATION_CASES.USER_ID.eq(warning.getuId()))
                    .and(MODERATION_CASES.OFFENSE_ID.eq(warning.getOffenseType().getId()))
                    .fetch();

            List<Warning> warnings = createList(result);
            warnings.remove(warning);

            return warnings;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    private static List<Warning> createList(Result<Record> result) {
        List<Warning> warnings = new ArrayList<>();

        for(Record r : result) {
            ModerationCasesRecord record = r.into(MODERATION_CASES);
            OffenseType offense = OffenseType.getTypeById(record.getOffenseId());

            warnings.add(new Warning(record.getId(), record.getUserId(), offense, record.getAdditionalComments(), record.getCreationTime()));
        }
        return warnings;
    }
}

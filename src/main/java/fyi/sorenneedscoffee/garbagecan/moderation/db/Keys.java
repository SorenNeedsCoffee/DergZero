/*
 * This file is generated by jOOQ.
 */
package fyi.sorenneedscoffee.garbagecan.moderation.db;


import fyi.sorenneedscoffee.garbagecan.moderation.db.tables.KickList;
import fyi.sorenneedscoffee.garbagecan.moderation.db.tables.ModerationCases;
import fyi.sorenneedscoffee.garbagecan.moderation.db.tables.records.KickListRecord;
import fyi.sorenneedscoffee.garbagecan.moderation.db.tables.records.ModerationCasesRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>s4_d0_users</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<ModerationCasesRecord, Integer> IDENTITY_MODERATION_CASES = Identities0.IDENTITY_MODERATION_CASES;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<KickListRecord> KEY_KICK_LIST_PRIMARY = UniqueKeys0.KEY_KICK_LIST_PRIMARY;
    public static final UniqueKey<ModerationCasesRecord> KEY_MODERATION_CASES_PRIMARY = UniqueKeys0.KEY_MODERATION_CASES_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<ModerationCasesRecord, Integer> IDENTITY_MODERATION_CASES = Internal.createIdentity(ModerationCases.MODERATION_CASES, ModerationCases.MODERATION_CASES.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<KickListRecord> KEY_KICK_LIST_PRIMARY = Internal.createUniqueKey(KickList.KICK_LIST, "KEY_kick_list_PRIMARY", KickList.KICK_LIST.ID);
        public static final UniqueKey<ModerationCasesRecord> KEY_MODERATION_CASES_PRIMARY = Internal.createUniqueKey(ModerationCases.MODERATION_CASES, "KEY_moderation_cases_PRIMARY", ModerationCases.MODERATION_CASES.ID);
    }
}

/*
 * This file is generated by jOOQ.
 */
package fyi.sorenneedscoffee.xputil.db;


import fyi.sorenneedscoffee.xputil.db.tables.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class S4D0Users extends SchemaImpl {

    private static final long serialVersionUID = -131140137;

    /**
     * The reference instance of <code>s4_d0_users</code>
     */
    public static final S4D0Users S4_D0_USERS = new S4D0Users();

    /**
     * The table <code>s4_d0_users.users</code>.
     */
    public final Users USERS = fyi.sorenneedscoffee.xputil.db.tables.Users.USERS;

    /**
     * No further instances allowed
     */
    private S4D0Users() {
        super("s4_d0_users", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Users.USERS);
    }
}
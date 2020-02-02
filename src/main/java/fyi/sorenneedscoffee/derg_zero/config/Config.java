package fyi.sorenneedscoffee.derg_zero.config;

@SuppressWarnings("unused")
public final class Config {
    private String token;
    private String ownerID;
    private String defaultRoleID;
    private String prefix;

    private UsersDb usersDb;
    private ScriptDb scriptDb;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getDefaultRoleID() {
        return defaultRoleID;
    }

    public void setDefaultRoleID(String defaultRoleID) {
        this.defaultRoleID = defaultRoleID;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public UsersDb getUsersDb() {
        return usersDb;
    }

    public void setUsersDb(UsersDb usersDb) {
        this.usersDb = usersDb;
    }

    public ScriptDb getScriptDb() {
        return scriptDb;
    }

    public void setScriptDb(ScriptDb scriptDb) {
        this.scriptDb = scriptDb;
    }
}

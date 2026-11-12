package vn.ptit.libman.config;

import java.sql.*;

/** Opens one connection per unit of work; no shared mutable JDBC connection. */
public final class Database7 {
    private final String url;
    private final String user;
    private final String password;
    public Database7(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }
    public static Database7 fromEnvironment() {
        return new Database7(env("LIBMAN_DB_URL", "jdbc:h2:./data/libman;MODE=MSSQLServer;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE"),
                env("LIBMAN_DB_USER", "sa"), env("LIBMAN_DB_PASSWORD", ""));
    }
    public static String env(String key, String fallback) { return System.getenv().getOrDefault(key, fallback); }
    public boolean isDemo() { return url.startsWith("jdbc:h2:"); }
    public Connection open() throws SQLException {
        try { Class.forName(isDemo() ? "org.h2.Driver" : "com.microsoft.sqlserver.jdbc.SQLServerDriver"); }
        catch (ClassNotFoundException ex) { throw new SQLException("JDBC driver unavailable",ex); }
        return DriverManager.getConnection(url, user, password);
    }
}

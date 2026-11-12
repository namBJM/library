package vn.ptit.libman.dao;
import java.sql.Connection;
import java.util.Objects;

/** Caller owns the connection and transaction; DAO closes only statements/results. */
public abstract class DAO7 {
    protected final Connection con;
    protected DAO7(Connection con) { this.con = Objects.requireNonNull(con); }
}

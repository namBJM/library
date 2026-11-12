package vn.ptit.libman.dao;
import java.sql.*;
import vn.ptit.libman.model.Manager7;
import vn.ptit.libman.security.Password7;

public class MemberDAO7 extends DAO7 {
    public MemberDAO7(Connection con) { super(con); }
    public boolean checkExists(String userName) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT id FROM tblMember WHERE userName = ?")) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    /** Only explicit tblManager membership can create an authenticated manager. */
    public Manager7 authenticateManager(String userName, String password) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT m.* FROM tblMember m JOIN tblManager g ON g.id=m.id WHERE m.userName=?")) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !Password7.verify(password, rs.getString("passWord"))) return null;
                Manager7 m = new Manager7();
                m.setId(rs.getInt("id")); m.setUserName(rs.getString("userName")); m.setFullName(rs.getString("fullName"));
                m.setDayOfBirth(rs.getDate("dayOfBirth").toLocalDate()); m.setEmail(rs.getString("email")); m.setPhone(rs.getString("phone"));
                return m;
            }
        }
    }
}

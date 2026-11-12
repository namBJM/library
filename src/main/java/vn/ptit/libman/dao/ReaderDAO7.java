package vn.ptit.libman.dao;
import java.sql.*;
import vn.ptit.libman.model.Reader7;

public class ReaderDAO7 extends DAO7 {
    public ReaderDAO7(Connection con) { super(con); }
    /** Inserts inherited member and reader rows; generated keys set id and readerID. */
    public boolean createReaderAccount(Reader7 reader) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblMember(userName,passWord,fullName,dayOfBirth,email,phone) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,reader.getUserName()); ps.setString(2,reader.getPassWord()); ps.setString(3,reader.getFullName());
            ps.setDate(4,Date.valueOf(reader.getDayOfBirth())); ps.setString(5,reader.getEmail()); ps.setString(6,reader.getPhone());
            if (ps.executeUpdate()!=1) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (!rs.next()) return false; reader.setId(rs.getInt(1)); }
        }
        reader.setReaderID("BD" + String.format(java.util.Locale.ROOT, "%03d", reader.getId()+122));
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblReader(id,readerID) VALUES(?,?)")) {
            ps.setInt(1,reader.getId()); ps.setString(2,reader.getReaderID()); return ps.executeUpdate()==1;
        }
    }
}

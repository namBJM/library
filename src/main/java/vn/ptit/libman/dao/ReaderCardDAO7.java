package vn.ptit.libman.dao;
import java.sql.*;
import vn.ptit.libman.model.ReaderCard7;

public class ReaderCardDAO7 extends DAO7 {
    public ReaderCardDAO7(Connection con) { super(con); }
    public boolean createCard(ReaderCard7 card) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO tblReaderCard(issueDate,expiryDate,status,readerId) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1,Date.valueOf(card.getIssueDate())); ps.setDate(2,Date.valueOf(card.getExpiryDate()));
            ps.setString(3,card.getStatus()); ps.setInt(4,card.getReader().getId());
            if (ps.executeUpdate()!=1) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (!rs.next()) return false; card.setId(rs.getInt(1)); }
            return true;
        }
    }
}

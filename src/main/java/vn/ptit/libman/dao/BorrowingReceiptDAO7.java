package vn.ptit.libman.dao;
import java.sql.*;
import vn.ptit.libman.model.*;

public class BorrowingReceiptDAO7 extends DAO7 {
    public BorrowingReceiptDAO7(Connection con) { super(con); }
    /** Loads full receipt, reader, librarian and all copies, independent of report filters. */
    public BorrowingReceipt7 getBorrowingReceipt(int borrowingReceiptId) throws SQLException {
        BorrowingReceipt7 receipt;
        try (PreparedStatement ps=con.prepareStatement(RowMapper7.HEADER_SELECT+RowMapper7.HEADER_FROM+" WHERE b.id=?")) {
            ps.setInt(1,borrowingReceiptId);
            try (ResultSet rs=ps.executeQuery()) { if (!rs.next()) return null; receipt=RowMapper7.receipt(rs); }
        }
        String sql=RowMapper7.HEADER_SELECT+RowMapper7.DETAIL_COLUMNS+RowMapper7.HEADER_FROM+RowMapper7.DETAIL_JOINS+" WHERE b.id=? ORDER BY c.id";
        try (PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setInt(1,borrowingReceiptId);
            try (ResultSet rs=ps.executeQuery()) { while (rs.next()) receipt.getDsBorrowingReceiptDetail().add(RowMapper7.detail(rs,receipt)); }
        }
        return receipt;
    }
}

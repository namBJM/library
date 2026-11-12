package vn.ptit.libman.dao;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.service.DateRange7;

public class BorrowingReceiptDetailDAO7 extends DAO7 {
    public BorrowingReceiptDetailDAO7(Connection con) { super(con); }
    /** Same borrowDate predicates as the aggregate; identity is (receiptId, copyId). */
    public List<BorrowingReceiptDetail7> getDocumentBorrowingDetail(int documentId, LocalDate startDate, LocalDate endDate) throws SQLException {
        new DateRange7(startDate,endDate);
        List<BorrowingReceiptDetail7> list=new ArrayList<>();
        String sql=RowMapper7.HEADER_SELECT+RowMapper7.DETAIL_COLUMNS+RowMapper7.HEADER_FROM+RowMapper7.DETAIL_JOINS
                +" WHERE d.id=? AND b.borrowDate>=? AND b.borrowDate<=? ORDER BY b.borrowDate,b.id,c.id";
        try (PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setInt(1,documentId); ps.setDate(2,Date.valueOf(startDate)); ps.setDate(3,Date.valueOf(endDate));
            try (ResultSet rs=ps.executeQuery()) { while (rs.next()) list.add(RowMapper7.detail(rs,RowMapper7.receipt(rs))); }
        }
        return list;
    }
}

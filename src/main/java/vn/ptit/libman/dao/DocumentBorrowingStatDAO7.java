package vn.ptit.libman.dao;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.service.DateRange7;

public class DocumentBorrowingStatDAO7 extends DAO7 {
    public DocumentBorrowingStatDAO7(Connection con) { super(con); }
    /** Counts detail rows, including two copies of the same title on one receipt. */
    public List<DocumentBorrowingStat7> getDocumentBorrowingStat(LocalDate startDate, LocalDate endDate) throws SQLException {
        new DateRange7(startDate,endDate);
        String sql="""
            SELECT d.id AS documentId,d.title,d.category,d.author,d.publisher,d.publicationYear,COUNT(*) AS borrowQuantity
            FROM tblDocument d JOIN tblDocumentCopy c ON c.documentId=d.id
            JOIN tblBorrowingReceiptDetail bd ON bd.documentCopyId=c.id
            JOIN tblBorrowingReceipt b ON b.id=bd.borrowingReceiptId
            WHERE b.borrowDate>=? AND b.borrowDate<=?
            GROUP BY d.id,d.title,d.category,d.author,d.publisher,d.publicationYear
            ORDER BY borrowQuantity DESC,d.id ASC
            """;
        List<DocumentBorrowingStat7> list=new ArrayList<>();
        try (PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setDate(1,Date.valueOf(startDate)); ps.setDate(2,Date.valueOf(endDate));
            try (ResultSet rs=ps.executeQuery()) { while(rs.next()) {
                DocumentBorrowingStat7 stat=new DocumentBorrowingStat7(); RowMapper7.document(rs,stat);
                stat.setBorrowQuantity(rs.getInt("borrowQuantity")); stat.setStartDate(startDate); stat.setEndDate(endDate); list.add(stat);
            } }
        }
        return list;
    }
    public Document7 getDocument(int documentId) throws SQLException {
        try (PreparedStatement ps=con.prepareStatement("SELECT id AS documentId,title,category,author,publisher,publicationYear FROM tblDocument WHERE id=?")) {
            ps.setInt(1,documentId);
            try (ResultSet rs=ps.executeQuery()) { if (!rs.next()) return null; Document7 d=new Document7(); RowMapper7.document(rs,d); return d; }
        }
    }
}

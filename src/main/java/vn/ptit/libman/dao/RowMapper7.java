package vn.ptit.libman.dao;
import java.sql.*;
import vn.ptit.libman.model.*;

final class RowMapper7 {
    static final String HEADER_SELECT = """
        SELECT b.id AS receiptId,b.borrowDate,b.dueDate,b.returnDate,b.status,
          r.id AS readerId,r.readerID AS readerCode,rm.fullName AS readerName,rm.email AS readerEmail,rm.phone AS readerPhone,
          s.id AS librarianId,s.jobPosition,sm.fullName AS librarianName
        """;
    static final String HEADER_FROM = """
        FROM tblBorrowingReceipt b JOIN tblReader r ON r.id=b.readerId
        JOIN tblMember rm ON rm.id=r.id JOIN tblStaff s ON s.id=b.librarianId
        JOIN tblMember sm ON sm.id=s.id
        """;
    static BorrowingReceipt7 receipt(ResultSet rs) throws SQLException {
        BorrowingReceipt7 b=new BorrowingReceipt7();
        Date returned=rs.getDate("returnDate"); if(returned!=null) b.setReturnDate(returned.toLocalDate()); b.setId(rs.getInt("receiptId"));
        b.setBorrowDate(rs.getDate("borrowDate").toLocalDate()); b.setDueDate(rs.getDate("dueDate").toLocalDate()); b.setStatus(rs.getString("status"));
        Reader7 r=new Reader7(); r.setId(rs.getInt("readerId")); r.setReaderID(rs.getString("readerCode"));
        r.setFullName(rs.getString("readerName")); r.setEmail(rs.getString("readerEmail")); r.setPhone(rs.getString("readerPhone")); b.setReader(r);
        Librarian7 l=new Librarian7(); l.setId(rs.getInt("librarianId")); l.setFullName(rs.getString("librarianName")); l.setJobPosition(rs.getString("jobPosition")); b.setLibrarian(l);
        return b;
    }
    static void document(ResultSet rs, Document7 d) throws SQLException {
        d.setId(rs.getInt("documentId")); d.setTitle(rs.getString("title")); d.setCategory(rs.getString("category"));
        d.setAuthor(rs.getString("author")); d.setPublisher(rs.getString("publisher")); d.setPublicationYear(rs.getInt("publicationYear"));
    }
    static BorrowingReceiptDetail7 detail(ResultSet rs, BorrowingReceipt7 receipt) throws SQLException {
        Document7 doc=new Document7(); document(rs,doc);
        DocumentCopy7 copy=new DocumentCopy7(); copy.setId(rs.getInt("copyId")); copy.setPhysicalCondition(rs.getString("physicalCondition")); copy.setDocument(doc);
        BorrowingReceiptDetail7 detail=new BorrowingReceiptDetail7(); detail.setBorrowingReceipt(receipt); detail.setDocumentCopy(copy); detail.setConditionWhenBorrowed(rs.getString("conditionWhenBorrowed"));
        return detail;
    }
    static final String DETAIL_COLUMNS = """
        ,d.id AS documentId,d.title,d.category,d.author,d.publisher,d.publicationYear,
        c.id AS copyId,c.physicalCondition,bd.conditionWhenBorrowed
        """;
    static final String DETAIL_JOINS = """
        JOIN tblBorrowingReceiptDetail bd ON bd.borrowingReceiptId=b.id
        JOIN tblDocumentCopy c ON c.id=bd.documentCopyId JOIN tblDocument d ON d.id=c.documentId
        """;
}

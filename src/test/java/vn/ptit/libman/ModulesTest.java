package vn.ptit.libman;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import vn.ptit.libman.config.*;
import vn.ptit.libman.dao.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.security.Password7;
import vn.ptit.libman.service.*;

/** Database integration checks against real JDBC, constraints and transactions. */
class ModulesTest {
    private Database7 db;
    private RegistrationService7 service;
    private final Clock clock=Clock.fixed(Instant.parse("2026-11-13T04:00:00Z"),ZoneId.of("Asia/Ho_Chi_Minh"));
    @BeforeEach void setup() throws Exception {
        db=new Database7("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MSSQLServer;DATABASE_TO_UPPER=FALSE;DB_CLOSE_DELAY=-1","sa","");
        Bootstrap7.initializeDemo(db); service=new RegistrationService7(db,clock,1);
    }
    private RegistrationForm7 form(String user) { return new RegistrationForm7(user,"Test@12345","Test@12345","Nguyễn Văn An","2005-11-11","an@example.com","0987654321"); }
    private int count(String table) throws Exception {
        try(Connection con=db.open();Statement s=con.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+table)) { r.next(); return r.getInt(1); }
    }
    @Test void registrationPersistsAllThreeRowsAndHashesPassword() throws Exception {
        int members=count("tblMember"),readers=count("tblReader"),cards=count("tblReaderCard");
        ReaderCard7 card=service.register(form("New_Reader"));
        assertEquals(members+1,count("tblMember")); assertEquals(readers+1,count("tblReader")); assertEquals(cards+1,count("tblReaderCard"));
        assertEquals("ACTIVE",card.getStatus()); assertEquals(LocalDate.of(2026,11,13),card.getIssueDate());
        assertEquals(LocalDate.of(2027,11,13),card.getExpiryDate()); assertTrue(card.getReader().getReaderID().startsWith("BD"));
        assertNull(card.getReader().getPassWord());
        try(Connection c=db.open();PreparedStatement p=c.prepareStatement("SELECT passWord,userName FROM tblMember WHERE id=?")) {
            p.setInt(1,card.getReader().getId()); try(ResultSet r=p.executeQuery()) { assertTrue(r.next()); assertEquals("new_reader",r.getString(2)); assertNotEquals("Test@12345",r.getString(1)); assertTrue(Password7.verify("Test@12345",r.getString(1))); assertFalse(Password7.verify("wrong",r.getString(1))); }
        }
    }
    @Test void validationRejectsBadFieldsWithoutAnyWrites() throws Exception {
        int initial=count("tblMember");
        var bad=new RegistrationForm7("x","short","different","","2030-01-01","bad","abc");
        var ex=assertThrows(ValidationException7.class,()->service.register(bad));
        assertEquals(7,ex.getErrors().size()); assertEquals(initial,count("tblMember"));
        assertThrows(ValidationException7.class,()->service.register(new RegistrationForm7("test_user","Test@12345","Test@12345","Tên","2026-02-30","ok@example.com","0987654321")));
    }
    @Test void duplicateUsernameDoesNotCreateAnotherAccountOrCard() throws Exception {
        service.register(form("same_user")); int initial=count("tblMember"),cards=count("tblReaderCard");
        assertThrows(ValidationException7.class,()->service.register(form("SAME_USER")));
        assertEquals(initial,count("tblMember")); assertEquals(cards,count("tblReaderCard"));
    }
    @Test void cardFailureRollsBackMemberAndReaderRows() throws Exception {
        int initial=count("tblMember"),readers=count("tblReader"),cards=count("tblReaderCard");
        try(Connection c=db.open();Statement s=c.createStatement()) {
            s.execute("ALTER TABLE tblReaderCard ADD CONSTRAINT fail_new_card CHECK (issueDate < '2026-11-13')");
        }
        assertThrows(SQLException.class,()->service.register(form("rollback_user")));
        assertEquals(initial,count("tblMember")); assertEquals(readers,count("tblReader")); assertEquals(cards,count("tblReaderCard"));
    }
    @Test void concurrentSameUsernameCreatesExactlyOneCompleteRegistration() throws Exception {
        int members=count("tblMember"),cards=count("tblReaderCard"); ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> work=()->{try {service.register(form("racing_user"));return true;}catch(ValidationException7 ex){return false;}};
            var results=pool.invokeAll(List.of(work,work));
            int successes=0;for(var f:results) if(f.get()) successes++;
            assertEquals(1,successes); assertEquals(members+1,count("tblMember")); assertEquals(cards+1,count("tblReaderCard"));
        } finally { pool.shutdownNow(); }
    }
    @Test void statisticsCountCopiesAndIncludeBothDateBoundaries() throws Exception {
        try(Connection c=db.open()) {
            var dao=new DocumentBorrowingStatDAO7(c);
            var list=dao.getDocumentBorrowingStat(LocalDate.of(2026,8,11),LocalDate.of(2026,9,11));
            assertEquals(List.of(11,8,6,4),list.stream().map(DocumentBorrowingStat7::getBorrowQuantity).toList());
            assertEquals(2,dao.getDocumentBorrowingStat(LocalDate.of(2026,8,11),LocalDate.of(2026,8,11)).get(0).getBorrowQuantity());
            assertEquals(1,dao.getDocumentBorrowingStat(LocalDate.of(2026,9,11),LocalDate.of(2026,9,11)).get(0).getBorrowQuantity());
            assertTrue(dao.getDocumentBorrowingStat(LocalDate.of(2027,1,1),LocalDate.of(2027,2,1)).isEmpty());
            for(var stat:list) {
                var details=new BorrowingReceiptDetailDAO7(c).getDocumentBorrowingDetail(stat.getId(),stat.getStartDate(),stat.getEndDate());
                assertEquals(stat.getBorrowQuantity(),details.size());
            }
            var firstDay=new BorrowingReceiptDetailDAO7(c).getDocumentBorrowingDetail(1,LocalDate.of(2026,8,11),LocalDate.of(2026,8,11));
            assertEquals(2,firstDay.size()); assertEquals(firstDay.get(0).getBorrowingReceipt().getId(),firstDay.get(1).getBorrowingReceipt().getId());
            assertNotEquals(firstDay.get(0).getDocumentCopy().getId(),firstDay.get(1).getDocumentCopy().getId());
        }
    }
    @Test void receiptIncludesReaderLibrarianAndOtherTitles() throws Exception {
        try(Connection c=db.open()) {
            var dao=new BorrowingReceiptDAO7(c); var receipt=dao.getBorrowingReceipt(1);
            assertEquals("Khuất Văn Nam",receipt.getReader().getFullName()); assertEquals("Nguyễn Thu Hà",receipt.getLibrarian().getFullName());
            assertEquals("BD125",receipt.getReader().getReaderID());
            assertEquals(5,receipt.getDsBorrowingReceiptDetail().size());
            assertTrue(receipt.getDsBorrowingReceiptDetail().stream().anyMatch(d->d.getDocumentCopy().getDocument().getId()!=1));
            assertNull(dao.getBorrowingReceipt(999999)); assertNull(new DocumentBorrowingStatDAO7(c).getDocument(999999));
        }
    }
    @Test void managerLoginRequiresExplicitRoleAndCorrectPassword() throws Exception {
        try(Connection c=db.open()) {
            var dao=new MemberDAO7(c);
            assertNotNull(dao.authenticateManager("manager","Manager@123"));
            assertNull(dao.authenticateManager("manager","bad"));
            assertNull(dao.authenticateManager("librarian","Librarian@123"));
            assertNull(dao.authenticateManager("bandoc_demo","Reader@123"));
            assertNull(dao.authenticateManager("' OR 1=1 --","anything"));
        }
    }
    @Test void invalidRangesAreRejected() {
        assertThrows(ValidationException7.class,()->DateRange7.parse(null,"2026-11-13"));
        assertThrows(ValidationException7.class,()->DateRange7.parse("2026-02-30","2026-11-13"));
        assertThrows(ValidationException7.class,()->DateRange7.parse("2026-11-14","2026-11-13"));
        assertDoesNotThrow(()->DateRange7.parse("2026-11-13","2026-11-13"));
    }
    @Test void richDatasetIsDiverseConsistentAndIdempotent() throws Exception {
        RichDemoData7.populate(db);
        int members=count("tblMember"),receipts=count("tblBorrowingReceipt");
        assertEquals(55,count("tblDocument")); assertEquals(81,count("tblReader"));
        assertTrue(receipts>900); assertEquals(12,count("tblSupplier"));
        RichDemoData7.populate(db); assertEquals(members,count("tblMember")); assertEquals(receipts,count("tblBorrowingReceipt"));
        try(Connection c=db.open();Statement st=c.createStatement()) {
            try(ResultSet r=st.executeQuery("SELECT COUNT(*) FROM tblBorrowingReceipt WHERE returnDate<borrowDate OR (returnDate IS NOT NULL AND status<>'RETURNED')")) {r.next();assertEquals(0,r.getInt(1));}
            try(ResultSet r=st.executeQuery("SELECT COUNT(*) FROM tblBorrowingReceiptDetail a JOIN tblBorrowingReceipt x ON x.id=a.borrowingReceiptId JOIN tblBorrowingReceiptDetail b ON a.documentCopyId=b.documentCopyId AND a.borrowingReceiptId<b.borrowingReceiptId JOIN tblBorrowingReceipt y ON y.id=b.borrowingReceiptId WHERE a.documentCopyId>10 AND x.borrowDate<=COALESCE(y.returnDate,CAST('9999-12-31' AS DATE)) AND y.borrowDate<=COALESCE(x.returnDate,CAST('9999-12-31' AS DATE))")) {r.next();assertEquals(0,r.getInt(1));}
            var stats=new DocumentBorrowingStatDAO7(c).getDocumentBorrowingStat(LocalDate.of(2025,1,1),LocalDate.of(2026,11,13));
            assertTrue(stats.size()>40);
            for(var stat:stats) assertEquals(stat.getBorrowQuantity(),new BorrowingReceiptDetailDAO7(c).getDocumentBorrowingDetail(stat.getId(),stat.getStartDate(),stat.getEndDate()).size());
        }
    }
}

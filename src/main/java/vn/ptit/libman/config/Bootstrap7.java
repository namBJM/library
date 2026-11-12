package vn.ptit.libman.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.*;
import jakarta.servlet.*;
import vn.ptit.libman.dao.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.security.Password7;
import vn.ptit.libman.service.RegistrationService7;

public final class Bootstrap7 implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        try {
            Database7 db=Database7.fromEnvironment();
            if (db.isDemo()) { initializeDemo(db); RichDemoData7.populate(db); }
            ServletContext context=event.getServletContext();
            context.setAttribute("database",db); context.setAttribute("demoMode",db.isDemo());
            Clock clock=Clock.system(ZoneId.of("Asia/Ho_Chi_Minh"));
            context.setAttribute("registrationService",new RegistrationService7(db,clock,
                    Integer.parseInt(Database7.env("LIBMAN_CARD_VALID_YEARS","1"))));
            context.setAttribute("clock",clock);
        } catch (Exception ex) { throw new IllegalStateException("Không thể khởi tạo LibMan. Kiểm tra cấu hình cơ sở dữ liệu.",ex); }
    }
    public static void createSchema(Connection con) throws SQLException, IOException {
        try (InputStream in=Bootstrap7.class.getResourceAsStream("/db/schema.sql")) {
            if (in==null) throw new FileNotFoundException("db/schema.sql");
            String sql=new String(in.readAllBytes(),StandardCharsets.UTF_8).replaceAll("(?m)--[^\\r\\n]*","");
            for (String statement:sql.split(";")) if (!statement.isBlank())
                try (Statement s=con.createStatement()) { s.execute(statement); }
        }
    }
    public static void initializeDemo(Database7 db) throws SQLException, IOException {
        // Explicit loading also works when the JDBC driver is isolated in a WAR loader.
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException ex) { throw new IllegalStateException(ex); }
        try (Connection con=db.open()) {
            try (ResultSet tables=con.getMetaData().getTables(null,null,"tblMember",new String[]{"TABLE"})) { if (tables.next()) {
                try(ResultSet columns=con.getMetaData().getColumns(null,null,"tblBorrowingReceipt","returnDate")) {
                    if(!columns.next()) execute(con,"ALTER TABLE tblBorrowingReceipt ADD returnDate DATE NULL");
                }
                execute(con,"UPDATE tblReader SET readerID='BD125' WHERE id IN (SELECT id FROM tblMember WHERE userName='bandoc_demo')");
                return;
            } }
            createSchema(con);
            con.setAutoCommit(false);
            try { seedDemo(con); con.commit(); }
            catch (SQLException | RuntimeException ex) { con.rollback(); throw ex; }
        }
    }
    private static void seedDemo(Connection con) throws SQLException {
        int manager=insertMember(con,"manager","Quản lý thư viện","Manager@123");
        execute(con,"INSERT INTO tblManager(id) VALUES(?)",manager);
        int librarian=insertMember(con,"librarian","Nguyễn Thu Hà","Librarian@123");
        execute(con,"INSERT INTO tblStaff(id,jobPosition) VALUES(?,?)",librarian,"LIBRARIAN");
        Reader7 reader=new Reader7(); reader.setUserName("bandoc_demo"); reader.setPassWord(Password7.hash("Reader@123"));
        reader.setFullName("Khuất Văn Nam"); reader.setDayOfBirth(LocalDate.of(2005,11,11));
        reader.setEmail("bandoc@example.com"); reader.setPhone("0987654321");
        if (!new ReaderDAO7(con).createReaderAccount(reader)) throw new SQLException("Demo reader failed");
        ReaderCard7 card=new ReaderCard7(); card.setReader(reader); card.setIssueDate(LocalDate.of(2026,1,1)); card.setExpiryDate(LocalDate.of(2027,1,1)); card.setStatus("ACTIVE");
        if (!new ReaderCardDAO7(con).createCard(card)) throw new SQLException("Demo card failed");
        String[][] docs={{"Bố Già","Sách văn học","Mario Puzo","NXB Văn học","2019"},
            {"Không Gia Đình","Sách văn học","Hector Malot","NXB Kim Đồng","2020"},
            {"Nhà Giả Kim","Sách văn học","Paulo Coelho","NXB Hội Nhà Văn","2021"},
            {"Cơ sở dữ liệu","Giáo trình","Nguyễn Văn Ba","NXB Giáo dục","2022"},
            {"Tạp chí Khoa học Công nghệ","Tạp chí","Nhiều tác giả","PTIT","2026"}};
        for (String[] d:docs) {
            int id=insert(con,"INSERT INTO tblDocument(title,category,author,publisher,publicationYear) VALUES(?,?,?,?,?)",d[0],d[1],d[2],d[3],Integer.parseInt(d[4]));
            execute(con,"INSERT INTO tblDocumentCopy(physicalCondition,documentId) VALUES(?,?)","Tốt",id);
            execute(con,"INSERT INTO tblDocumentCopy(physicalCondition,documentId) VALUES(?,?)","Tốt",id);
        }
        // Fixed dates make the report reproducible. Two copies of Bố Già on the first receipt.
        String[] dates={"2026-08-11","2026-08-14","2026-08-16","2026-08-19","2026-08-23","2026-08-27","2026-08-30","2026-09-02","2026-09-06","2026-09-11"};
        for (int i=0;i<dates.length;i++) {
            LocalDate day=LocalDate.parse(dates[i]);
            int receipt=insert(con,"INSERT INTO tblBorrowingReceipt(borrowDate,dueDate,status,readerId,librarianId) VALUES(?,?,?,?,?)",Date.valueOf(day),Date.valueOf(day.plusDays(14)),i<8 ? "RETURNED" : "BORROWING",reader.getId(),librarian);
            execute(con,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,1,"Tốt");
            if (i==0) execute(con,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,2,"Tốt");
            if (i<8) execute(con,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,3,"Tốt");
            if (i<6) execute(con,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,5,"Tốt");
            if (i<4) execute(con,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,7,"Tốt");
        }
    }
    private static int insertMember(Connection con,String user,String name,String password) throws SQLException {
        return insert(con,"INSERT INTO tblMember(userName,passWord,fullName,dayOfBirth,email,phone) VALUES(?,?,?,?,?,?)",user,Password7.hash(password),name,Date.valueOf("1990-01-01"),user+"@example.com","0901234567");
    }
    private static int insert(Connection con,String sql,Object... args) throws SQLException {
        try (PreparedStatement ps=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            for(int i=0;i<args.length;i++) ps.setObject(i+1,args[i]); ps.executeUpdate();
            try(ResultSet keys=ps.getGeneratedKeys()) { if (!keys.next()) throw new SQLException("Missing generated key"); return keys.getInt(1); }
        }
    }
    private static void execute(Connection con,String sql,Object... args) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement(sql)) { for(int i=0;i<args.length;i++) ps.setObject(i+1,args[i]); ps.executeUpdate(); }
    }
}

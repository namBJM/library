package vn.ptit.libman.config;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import vn.ptit.libman.security.Password7;

/** Deterministic, synthetic library history. Adds data without deleting existing accounts. */
public final class RichDemoData7 {
    public static synchronized void populate(Database7 db) throws SQLException {
        if (!db.isDemo()) throw new IllegalArgumentException("Rich demo data requires H2 demo mode");
        try(Connection c=db.open()) {
            try(Statement s=c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS tblDemoDataset (version INT PRIMARY KEY, description NVARCHAR(255))");
            }
            try(Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT version FROM tblDemoDataset WHERE version=1")) { if(r.next()) return; }
            c.setAutoCommit(false);
            try { seed(c); execute(c,"INSERT INTO tblDemoDataset VALUES(1,?)","Dữ liệu mô phỏng đa dạng 2025–2026"); c.commit(); }
            catch(SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    private static void seed(Connection c) throws SQLException {
        Random random=new Random(577);
        String hash=Password7.hash("SeedReader@123");
        String[] last={"Nguyễn","Trần","Lê","Phạm","Hoàng","Vũ","Đặng","Bùi","Đỗ","Hồ"};
        String[] names={"Minh Anh","Đức Anh","Thu Hà","Hoàng Long","Phương Thảo","Quang Huy","Ngọc Linh","Thanh Tùng","Khánh Chi","Hải Nam","Mai Trang","Tuấn Kiệt"};
        List<Integer> readers=new ArrayList<>(),staff=new ArrayList<>();
        for(int i=0;i<80;i++) {
            int id=insert(c,"INSERT INTO tblMember(userName,passWord,fullName,dayOfBirth,email,phone) VALUES(?,?,?,?,?,?)",
                "demo_reader_"+(i+1),hash,last[i%last.length]+" "+names[(i*7+i/10)%names.length],Date.valueOf(LocalDate.of(1980+i%27,1+i%12,1+i%27)),"reader"+(i+1)+"@example.com",String.format(Locale.ROOT,"09%08d",10000000+i));
            execute(c,"INSERT INTO tblReader VALUES(?,?)",id,"BD"+String.format(Locale.ROOT,"%03d",id+122));
            execute(c,"INSERT INTO tblReaderCard(issueDate,expiryDate,status,readerId) VALUES(?,?,?,?)",Date.valueOf("2025-01-01"),Date.valueOf(i<4?"2025-12-31":"2027-12-31"),i>=76?"INACTIVE":"ACTIVE",id);
            if(i>=4 && i<76) readers.add(id);
        }
        for(int i=0;i<8;i++) {
            int id=insert(c,"INSERT INTO tblMember(userName,passWord,fullName,dayOfBirth,email,phone) VALUES(?,?,?,?,?,?)","demo_staff_"+(i+1),hash,last[(i+2)%10]+" "+names[i],Date.valueOf(LocalDate.of(1985+i,3+i,12)),"staff"+(i+1)+"@example.com",String.format(Locale.ROOT,"08%08d",10000000+i));
            execute(c,"INSERT INTO tblStaff VALUES(?,?)",id,"LIBRARIAN"); staff.add(id);
        }
        String[][] titles={
            {"Dế Mèn phiêu lưu ký","Tô Hoài"},{"Lão Hạc","Nam Cao"},{"Truyện Kiều","Nguyễn Du"},{"Số đỏ","Vũ Trọng Phụng"},{"Tắt đèn","Ngô Tất Tố"},
            {"Đắc nhân tâm","Dale Carnegie"},{"Tư duy nhanh và chậm","Daniel Kahneman"},{"Đi tìm lẽ sống","Viktor Frankl"},{"Những người khốn khổ","Victor Hugo"},{"Hoàng tử bé","Antoine de Saint-Exupéry"},
            {"Lập trình Java căn bản","Nhóm biên soạn mô phỏng"},{"Lập trình hướng đối tượng","Nhóm biên soạn mô phỏng"},{"Cấu trúc dữ liệu và giải thuật","Nhóm biên soạn mô phỏng"},{"Mạng máy tính","Nhóm biên soạn mô phỏng"},{"Hệ điều hành","Nhóm biên soạn mô phỏng"},
            {"Phân tích thiết kế hệ thống thông tin","Nhóm biên soạn mô phỏng"},{"Cơ sở dữ liệu nâng cao","Nhóm biên soạn mô phỏng"},{"Công nghệ phần mềm","Nhóm biên soạn mô phỏng"},{"An toàn thông tin","Nhóm biên soạn mô phỏng"},{"Trí tuệ nhân tạo","Nhóm biên soạn mô phỏng"},
            {"Giải tích 1","Nhóm biên soạn mô phỏng"},{"Đại số tuyến tính","Nhóm biên soạn mô phỏng"},{"Xác suất thống kê","Nhóm biên soạn mô phỏng"},{"Toán rời rạc","Nhóm biên soạn mô phỏng"},{"Vật lý đại cương","Nhóm biên soạn mô phỏng"},
            {"Kinh tế vi mô","Nhóm biên soạn mô phỏng"},{"Kinh tế vĩ mô","Nhóm biên soạn mô phỏng"},{"Quản trị học","Nhóm biên soạn mô phỏng"},{"Marketing căn bản","Nhóm biên soạn mô phỏng"},{"Khởi nghiệp đổi mới sáng tạo","Nhóm biên soạn mô phỏng"},
            {"English for Information Technology","Nhóm biên soạn mô phỏng"},{"Ngữ pháp tiếng Anh thực hành","Nhóm biên soạn mô phỏng"},{"Tiếng Nhật sơ cấp","Nhóm biên soạn mô phỏng"},{"Tiếng Hàn nhập môn","Nhóm biên soạn mô phỏng"},{"Kỹ năng thuyết trình","Nhóm biên soạn mô phỏng"},
            {"Lịch sử Việt Nam","Nhóm biên soạn mô phỏng"},{"Văn hóa Việt Nam","Nhóm biên soạn mô phỏng"},{"Địa lý tự nhiên","Nhóm biên soạn mô phỏng"},{"Môi trường và phát triển bền vững","Nhóm biên soạn mô phỏng"},{"Tâm lý học đại cương","Nhóm biên soạn mô phỏng"},
            {"Tạp chí Công nghệ số 01/2026","Ban biên tập mô phỏng"},{"Tạp chí Công nghệ số 02/2026","Ban biên tập mô phỏng"},{"Tạp chí Khoa học dữ liệu","Ban biên tập mô phỏng"},{"Tạp chí Giáo dục đại học","Ban biên tập mô phỏng"},{"Tạp chí Kinh tế và Dự báo","Ban biên tập mô phỏng"},
            {"Cẩm nang nghiên cứu khoa học","Nhóm biên soạn mô phỏng"},{"Kỹ năng làm việc nhóm","Nhóm biên soạn mô phỏng"},{"Thiết kế giao diện người dùng","Nhóm biên soạn mô phỏng"},{"Điện toán đám mây","Nhóm biên soạn mô phỏng"},{"Internet vạn vật","Nhóm biên soạn mô phỏng"}
        };
        String[] categories={"Văn học","Kỹ năng sống","Công nghệ thông tin","Toán và khoa học tự nhiên","Kinh tế và quản trị","Ngoại ngữ và kỹ năng","Khoa học xã hội","Tạp chí","Tài liệu tham khảo"};
        String[] publishers={"NXB Giáo dục","NXB Đại học Quốc gia Hà Nội","NXB Khoa học và Kỹ thuật","NXB Trẻ","NXB Kim Đồng","NXB Văn học"};
        List<Integer> documents=new ArrayList<>(),copies=new ArrayList<>();
        Map<Integer,LocalDate> available=new HashMap<>();
        Map<Integer,String> conditions=new HashMap<>();
        for(int i=0;i<titles.length;i++) {
            int group=i<5?0:i<10?1:i<20?2:i<25?3:i<30?4:i<35?5:i<40?6:i<45?7:8;
            int doc=insert(c,"INSERT INTO tblDocument(title,category,author,publisher,publicationYear) VALUES(?,?,?,?,?)",titles[i][0],categories[group],titles[i][1],publishers[i%publishers.length],i>=40&&i<45?2026:2016+i%11); documents.add(doc);
            for(int j=0;j<4+i%6;j++) {
                String condition=j==3?"Cũ, còn sử dụng tốt":j==4?"Bìa trầy nhẹ":"Tốt";
                int copy=insert(c,"INSERT INTO tblDocumentCopy(physicalCondition,documentId) VALUES(?,?)",condition,doc);
                // Keep the last three titles unborrowed for empty-detail scenarios.
                if(i<47) {copies.add(copy); available.put(copy,LocalDate.of(2025,1,1));conditions.put(copy,condition);}
            }
        }
        List<Integer> suppliers=new ArrayList<>();
        for(int i=0;i<12;i++) suppliers.add(insert(c,"INSERT INTO tblSupplier(supplierName,email,phoneNumber) VALUES(?,?,?)","Nhà cung cấp mô phỏng "+(i+1),"supplier"+(i+1)+"@example.com",String.format(Locale.ROOT,"024%08d",30000000+i)));
        // Inventory imports precede all generated borrowing history.
        for(int i=0;i<documents.size();i++) {
            int receipt=insert(c,"INSERT INTO tblImportReceipt(importDate,supplierId,librarianId) VALUES(?,?,?)",Date.valueOf(LocalDate.of(2025,1,5).plusDays(i)),suppliers.get(i%12),staff.get(i%8));
            execute(c,"INSERT INTO tblImportReceiptDetail VALUES(?,?,?)",receipt,documents.get(i),4+i%6);
        }
        LocalDate today=LocalDate.of(2026,11,13);
        for(LocalDate day=LocalDate.of(2025,6,1);day.isBefore(today);day=day.plusDays(1)) {
            int quantity=1+random.nextInt(3);
            for(int n=0;n<quantity;n++) {
                int duration=7*(1+random.nextInt(3));LocalDate due=day.plusDays(duration);
                boolean returned=day.isBefore(today.minusDays(40)) || random.nextInt(4)!=0;
                LocalDate returnedOn=returned?day.plusDays(2+random.nextInt(duration+7)):null;
                if(returnedOn!=null && !returnedOn.isBefore(today)) returnedOn=null;
                List<Integer> selected=new ArrayList<>();
                for(int tries=0;tries<100 && selected.size()<1+(n%4);tries++) {
                    // Bias some titles for visibly different popularity levels.
                    int copy=copies.get(random.nextInt(random.nextBoolean()?Math.min(70,copies.size()):copies.size()));
                    if(!available.get(copy).isAfter(day) && !selected.contains(copy)) selected.add(copy);
                }
                if(selected.isEmpty()) continue;
                int receipt=insert(c,"INSERT INTO tblBorrowingReceipt(borrowDate,dueDate,returnDate,status,readerId,librarianId) VALUES(?,?,?,?,?,?)",Date.valueOf(day),Date.valueOf(due),returnedOn==null?null:Date.valueOf(returnedOn),returnedOn==null?"BORROWING":"RETURNED",readers.get(random.nextInt(readers.size())),staff.get(random.nextInt(staff.size())));
                for(int copy:selected) {execute(c,"INSERT INTO tblBorrowingReceiptDetail VALUES(?,?,?)",receipt,copy,conditions.get(copy));available.put(copy,returnedOn==null?LocalDate.MAX:returnedOn.plusDays(1));}
            }
        }
    }
    private static int insert(Connection c,String sql,Object... values) throws SQLException {
        try(PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {bind(p,values);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(!r.next())throw new SQLException("Missing generated key");return r.getInt(1);}}
    }
    private static void execute(Connection c,String sql,Object... values) throws SQLException {try(PreparedStatement p=c.prepareStatement(sql)){bind(p,values);p.executeUpdate();}}
    private static void bind(PreparedStatement p,Object[] values) throws SQLException {for(int i=0;i<values.length;i++)p.setObject(i+1,values[i]);}
}

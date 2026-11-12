# LibMan

Ứng dụng Java triển khai hai module **Đăng kí làm thẻ bạn đọc** và **Thống kê tài liệu theo số lần mượn**, dựa trên tài liệu Word và 13 sơ đồ PlantUML `.txt` ở thư mục cha.

## Chạy nhanh trên Windows

Cần **JDK 17 trở lên** (`java`, `javac` trên PATH hoặc `JAVA_HOME`). Máy hiện tại đã có JDK. Lần đầu cần Internet để tải Maven và các thư viện; script kiểm tra SHA-512 của bản Maven tải về.

Mở terminal trong thư mục `libman`:

```powershell
.\mvnw.cmd package exec:java
```

Mở **http://localhost:8080**. Nhấn `Ctrl+C` trong terminal để dừng. Nếu máy đã cài Maven, có thể dùng `mvn package exec:java`. Trên macOS/Linux dùng Maven đã cài cùng lệnh này.

- Đăng kí thẻ: **Trang chủ → Đăng kí thẻ bạn đọc**.
- Đăng nhập quản lý: **manager / Manager@123** (chỉ có trong cơ sở dữ liệu demo).
- Xem báo cáo: **Không gian quản lý → Xem thống kê tài liệu**.
- Khoảng ngày mẫu: **11/08/2026–11/09/2026**.
- Chọn **Xem chi tiết** của một tài liệu, sau đó **Xem phiếu** ở một lượt mượn.

Cơ sở dữ liệu demo H2 được tạo và lưu tại `data/libman.mv.db`; khởi động lại vẫn giữ tài khoản đã đăng kí. Đổi cổng bằng `$env:PORT='8081'` trước khi chạy. Không cần cài SQL Server để xem demo. Không chạy hai tiến trình cùng mở một file H2.

## Công nghệ và kiến trúc

- Java 17, Maven, Servlet 6.0, JSP 3.1/JSTL, Tomcat 10.1, JDBC.
- SQL Server là cơ sở dữ liệu đích; H2 ở chế độ MSSQLServer dùng để chạy thử và kiểm thử.
- `web`: Servlet điều phối request và Filter bảo vệ phiên; JSP trong `WEB-INF/views` chỉ hiển thị.
- `service`: kiểm tra biểu mẫu, điều phối giao dịch đăng kí.
- `dao`: câu lệnh SQL có tham số, ánh xạ dữ liệu; không tự commit hoặc đóng connection của caller.
- `model`: lớp thực thể có thuộc tính `private`, getter/setter và quan hệ kế thừa theo thiết kế.
- `config`: mở kết nối, tạo cơ sở dữ liệu và dữ liệu demo.

Tên URL giữ theo sơ đồ: `MainCustomerUI.jsp`, `RegisterCardUI.jsp`, `doRegisterCard.jsp`, `MainManagerUI.jsp`, `SelectDocStatUI.jsp`, `DocumentBorrowingStatUI.jsp`, `BorrowingReceiptDetailUI.jsp`, `BorrowingReceiptUI.jsp`. Các URL do Servlet xử lý, sau đó forward đến JSP; không nhúng JDBC vào JSP.

## Nghiệp vụ đã triển khai

### Đăng kí và cấp thẻ

1. Nhập tên đăng nhập, mật khẩu, xác nhận mật khẩu, họ tên, ngày sinh, email, số điện thoại.
2. Kiểm tra dữ liệu ở cả trình duyệt và máy chủ; kiểm tra trùng tên đăng nhập.
3. Ba DAO dùng **cùng một Connection**, tắt auto-commit; ghi `tblMember`, `tblReader`, `tblReaderCard`.
4. Sinh `readerID` từ khóa chính do cơ sở dữ liệu cấp, tự sinh mã thẻ, ngày cấp, ngày hết hạn và trạng thái `ACTIVE`.
5. Chỉ báo thành công sau `commit`; lỗi ở bất kỳ bước ghi nào sẽ `rollback`.
6. Hiển thị thẻ vừa cấp, nhấn **OK · Về trang chính**. Dùng Post/Redirect/Get để tải lại trang kết quả không gửi lại biểu mẫu.

Tên đăng nhập được chuẩn hóa về chữ thường. Ràng buộc UNIQUE vẫn xử lý trường hợp hai yêu cầu gửi đồng thời. Email không có ràng buộc duy nhất vì thiết kế CSDL cuối chỉ yêu cầu `userName` và `readerID` duy nhất. Mật khẩu được băm PBKDF2-HMAC-SHA256 với salt ngẫu nhiên; không lưu xác nhận mật khẩu hoặc đưa mật khẩu vào trang lỗi/phiên.

### Thống kê và xem phiếu

- Mọi trang quản lý yêu cầu phiên có `Manager7`; bạn đọc và thủ thư không có quyền xem báo cáo.
- Chọn đủ hai ngày hợp lệ, ngày bắt đầu ≤ ngày kết thúc.
- Đếm **dòng chi tiết mượn của các bản thuộc tài liệu**, lọc theo `tblBorrowingReceipt.borrowDate >= startDate AND borrowDate <= endDate`.
- Một phiếu mượn hai bản của cùng tài liệu được tính **hai lượt**; khóa chi tiết là `(borrowingReceiptId, documentCopyId)`.
- Chỉ liệt kê tài liệu có lượt mượn; sắp xếp giảm dần theo số lượt, rồi theo mã tài liệu khi bằng nhau. Tính cả phiếu đã trả và đang mượn.
- Danh sách chi tiết giữ nguyên khoảng thời gian; số dòng khớp số lượt ở báo cáo.
- Chọn dòng chi tiết sẽ mở phiếu bằng `borrowingReceiptId`, hiển thị bạn đọc, thủ thư và **tất cả tài liệu trong phiếu**.
- Có trạng thái không có dữ liệu, tham số/ngày sai, tài liệu/phiếu không tồn tại, lỗi kết nối, phiên biểu mẫu hết hạn.

Dữ liệu mẫu có 5 đầu tài liệu, 10 phiếu, tổng **29 lượt mượn** trong khoảng mẫu. Số lượt lần lượt: Bố Già **11**, Không Gia Đình **8**, Nhà Giả Kim **6**, Cơ sở dữ liệu **4**. Tạp chí chưa được mượn nên không xuất hiện. Phiếu PM1 có **5 bản** của 4 đầu tài liệu, giúp kiểm tra luồng xem toàn bộ phiếu.

## Những điểm thống nhất từ tài liệu

Word chứa nhiều phiên bản phân tích: một số đoạn còn chờ thủ thư duyệt, tên bảng có hậu tố `7`, hoặc khóa `borrowingDetailId`. Code ưu tiên bộ sơ đồ `.txt` đã thống nhất, đặc biệt `07`, `08`, `09`, `10`, `11`:

| Nội dung | Quyết định triển khai |
|---|---|
| Đăng kí thẻ | Tạo tài khoản và cấp thẻ ngay, cùng một giao dịch; không có bước chờ duyệt |
| Bảng dữ liệu | Tên `tblMember`, `tblReader`… không có hậu tố `7`; tên lớp Java giữ hậu tố `7` |
| Chi tiết phiếu | Khóa ghép phiếu + bản tài liệu; mở phiếu theo mã phiếu |
| Ngày trả thực tế | Bổ sung cột `returnDate` theo yêu cầu giao diện; ngày chưa được ghi nhận hiển thị rõ, không suy diễn từ trạng thái. SQL Server cũ cần chạy `db/migrate-return-date.sql`; H2 demo tự cập nhật. |
| Thời hạn thẻ | Tài liệu không ấn định; mặc định **1 năm**, cấu hình `LIBMAN_CARD_VALID_YEARS` từ 1 đến 10 |
| Ngày cấp thẻ | Theo múi giờ Việt Nam `Asia/Ho_Chi_Minh` |
| Vai trò quản lý | `Manager7 extends Member7`; bổ sung bảng `tblManager(id)` làm dấu hiệu quyền rõ ràng, không suy đoán quyền từ tài khoản chưa có reader/staff |

`tblManager` là phần mở rộng có chủ đích để đăng nhập/phân quyền thực tế. Sơ đồ ban đầu không lưu thuộc tính hoặc quan hệ nào để phân biệt một Member là Manager. Tài khoản tự đăng kí chỉ được ghi vào `tblReader`, không thể tự nhận quyền quản lý.

## Sử dụng SQL Server

1. Tạo một cơ sở dữ liệu trống tên `LibMan` trong SQL Server/SSMS.
2. Chọn cơ sở dữ liệu đó và chạy **`src/main/resources/db/schema.sql`**. Script chứa đầy đủ bảng thiết kế và bảng quyền `tblManager`; chỉ chạy một lần trên CSDL trống.
3. Cấu hình thông tin kết nối trong terminal chạy ứng dụng:

```powershell
$env:LIBMAN_DB_URL='jdbc:sqlserver://localhost:1433;databaseName=LibMan;encrypt=true;trustServerCertificate=true'
$env:LIBMAN_DB_USER='libman_user'
$env:LIBMAN_DB_PASSWORD='mat-khau-cua-ban'
$env:LIBMAN_CARD_VALID_YEARS='1'
```

`trustServerCertificate=true` chỉ dành cho SQL Server chạy cục bộ dùng chứng chỉ tự ký. Khi triển khai thật, dùng chứng chỉ hợp lệ và `trustServerCertificate=false`. Tài khoản JDBC cần quyền đọc/ghi các bảng và quyền lấy khóa identity. Ứng dụng **không** tự tạo bảng hoặc nạp tài khoản demo vào SQL Server.

4. Tạo tài khoản quản lý bằng công cụ dòng lệnh (không có endpoint web cấp quyền):

```powershell
$env:LIBMAN_MANAGER_USER='quanly'
$env:LIBMAN_MANAGER_NAME='Người quản lý thư viện'
$env:LIBMAN_MANAGER_PASSWORD='mat-khau-moi-tu-8-den-128-ky-tu'
.\mvnw.cmd compile exec:java "-Dexec.mainClass=vn.ptit.libman.CreateManager7"
Remove-Item Env:LIBMAN_MANAGER_PASSWORD
```

Công cụ tạo Member và Manager trong một giao dịch, với ngày sinh/thông tin liên hệ placeholder được ghi rõ trong code; cập nhật hồ sơ thực trước khi sử dụng thực tế. Tên tài khoản phải mới, không trùng Member hiện có.

5. Chạy ` .\mvnw.cmd package exec:java ` hoặc triển khai `target/libman.war` lên **Tomcat 10.1**. Khi chép WAR vào `webapps` của Tomcat, mở `http://localhost:8080/libman/`. Các biến môi trường phải nằm trong tiến trình khởi động Tomcat.

SQL Server trống chưa có dữ liệu mượn thì báo cáo sẽ hiển thị trạng thái không có dữ liệu. Hai module này không bổ sung chức năng tạo phiếu mượn hoặc nhập tài liệu.

## Kiểm thử

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
# Khi ứng dụng đang chạy, dùng Python 3 để kiểm tra HTTP/JSP:
python scripts/smoke_http.py
```

Đã chạy thành công **9 kiểm thử JUnit và 36 kiểm tra HTTP** trên bản demo.

9 kiểm thử JUnit dùng cơ sở dữ liệu JDBC H2 tách biệt: đăng kí thành công và băm mật khẩu, dữ liệu sai, tên đăng nhập trùng, rollback khi tạo thẻ lỗi, đăng kí đồng thời cùng tên, đếm bản/ngày biên và khớp chi tiết, toàn bộ phiếu và mã không tồn tại, phân quyền quản lý, khoảng ngày không hợp lệ.

Script HTTP kiểm tra render tiếng Việt, CSS, chặn truy cập chưa đăng nhập, CSRF, đăng kí và trang kết quả, lỗi đăng kí, đăng nhập, thống kê/chi tiết/toàn bộ phiếu, ngày sai, rỗng, ID sai, đăng xuất. Mặc định chạy trên demo và tạo một tài khoản thử có tên ngẫu nhiên; không chạy script này trên CSDL thật.

Các kiểm thử tự động đã chạy trên H2; **chưa kiểm chứng trên một máy chủ SQL Server thực**. Script SQL và DAO dùng cú pháp JDBC/SQL Server, nhưng cần chạy lại trên SQL Server trước khi triển khai thật.

## Đối chiếu mã nguồn

| Tài liệu | Code chính |
|---|---|
| `06_lop_thiet_ke.txt` | `model/*7.java` trong phạm vi hai module |
| `07_thiet_ke_csdl.txt` | `resources/db/schema.sql` |
| `08`, `11`, `12` đăng kí | `RegistrationService7`, `RegistrationForm7`, `MemberDAO7`, `ReaderDAO7`, `ReaderCardDAO7` |
| `09`, `10`, `13` thống kê | `DocumentBorrowingStatDAO7`, `BorrowingReceiptDetailDAO7`, `BorrowingReceiptDAO7`, `DateRange7` |
| Giao diện và điều phối | `LibraryServlet7`, `SecurityFilter7`, `WEB-INF/views/*.jsp` |

Các lớp thống kê nhà cung cấp/bạn đọc và module nhập, mượn/trả, CRUD không nằm trong phạm vi yêu cầu. Schema giữ bảng liên quan để tương thích thiết kế tổng thể. Các file `.docx` và `.txt` gốc không bị chỉnh sửa; tệp `~$...docx` 162 byte là file khóa Word, không phải tài liệu nội dung.

Tham chiếu nền tảng: [Tomcat 10.1 và Servlet/JSP](https://tomcat.apache.org/migration-10.1.html), [H2 MSSQLServer compatibility](https://h2database.github.io/html/features.html), [Maven 3.9.11](https://maven.apache.org/docs/3.9.11/release-notes.html).

## Bộ dữ liệu mở rộng

H2 tự nạp thêm 50 đầu tài liệu, 80 bạn đọc, 8 thủ thư, 12 nhà cung cấp, 50 phiếu nhập và lịch sử mượn mô phỏng từ 01/06/2025 đến 12/11/2026. Dữ liệu có phân bố lượt mượn khác nhau, phiếu nhiều tài liệu, trả sớm/trễ, đang mượn và các đầu tài liệu chưa được mượn. Các tên nhà cung cấp, ấn bản, tài khoản và giao dịch là dữ liệu mô phỏng; không phải hồ sơ thực.

Mật khẩu chung của tài khoản `demo_reader_1`…`demo_reader_80` và `demo_staff_1`…`demo_staff_8` là `SeedReader@123`. Chúng không có quyền quản lý. Các tài khoản cũ được giữ nguyên. `tblDemoDataset` đánh dấu lần nạp; khởi động lại không tạo bản ghi trùng. Không đổi/xóa marker để nạp lại. SQL Server không tự nạp dữ liệu demo.

Chọn khoảng **01/06/2025–12/11/2026** để xem toàn bộ lịch sử mới, hoặc lọc từng tháng để so sánh. Các kiểm thử dùng CSDL nhỏ riêng và có thêm kiểm thử bộ dữ liệu mở rộng về tính đa dạng, không nạp trùng, ngày trả và xung đột bản tài liệu.

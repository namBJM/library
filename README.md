# LibMan

Ứng dụng Java triển khai hai module **Đăng kí làm thẻ bạn đọc** và **Thống kê tài liệu theo số lần mượn**

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

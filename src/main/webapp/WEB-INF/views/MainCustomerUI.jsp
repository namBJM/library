<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Trang chủ"/><c:set var="active" value="home"/>
<%@ include file="common/header.jspf" %>

<p class="eyebrow">DỊCH VỤ THƯ VIỆN</p><p class="subtitle">Các chức năng dành cho bạn đọc và nhân viên quản lý.</p>
<div class="feature-grid"><section class="card"><div class="feature-icon" aria-hidden="true">▣</div><h2>Đăng kí thẻ bạn đọc</h2><p>Điền thông tin cá nhân, tạo tài khoản và nhận thẻ thư viện trong một lần đăng kí.</p><a href="${pageContext.request.contextPath}/RegisterCardUI.jsp">Tạo thẻ mới →</a></section>
<section class="card"><div class="feature-icon" aria-hidden="true">▥</div><h2>Thống kê tài liệu</h2><p>Theo dõi số lượt mượn theo thời gian, xem từng lượt mượn và phiếu mượn tương ứng.</p><a href="${pageContext.request.contextPath}/MainManagerUI.jsp">Truy cập dành cho quản lý →</a></section></div>
<%@ include file="common/footer.jspf" %>

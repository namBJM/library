<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Không gian quản lý"/><c:set var="active" value="manager"/>
<%@ include file="common/header.jspf" %>
<p class="manager-name">QL Khuất Văn Nam</p><section class="hero"><p class="eyebrow">THỐNG KÊ TÀI LIỆU</p><p>Xem tài liệu được mượn nhiều nhất, đối chiếu từng lượt mượn và truy xuất đầy đủ phiếu mượn.</p><a class="btn" href="${pageContext.request.contextPath}/SelectDocStatUI.jsp">Xem thống kê tài liệu →</a><span class="ornament" aria-hidden="true">▥</span></section>

<form method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><button class="btn secondary">Đăng xuất</button></form>
<%@ include file="common/footer.jspf" %>

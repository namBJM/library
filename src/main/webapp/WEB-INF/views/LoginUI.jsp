<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Đăng nhập quản lý"/><c:set var="active" value="login"/>
<%@ include file="common/header.jspf" %>
<section class="card login-card"><h1>Đăng nhập thư viện</h1><p class="subtitle">Sử dụng tài khoản quản lý để xem báo cáo thống kê.</p>
<c:if test="${not empty loginError}"><div class="notice error" role="alert"><c:out value="${loginError}"/></div></c:if>
<form method="post" action="${pageContext.request.contextPath}/LoginUI.jsp"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><div class="field"><label for="userName">Tên đăng nhập</label><input id="userName" name="userName" autocomplete="username" maxlength="50" required></div><div class="field"><label for="passWord">Mật khẩu</label><input id="passWord" name="passWord" type="password" autocomplete="current-password" maxlength="128" required></div><button class="btn">Đăng nhập →</button></form>

</section><%@ include file="common/footer.jspf" %>

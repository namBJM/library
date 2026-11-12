<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Thông báo"/>
<%@ include file="common/header.jspf" %>
<section class="card"><p class="eyebrow">THÔNG BÁO TỪ THƯ VIỆN</p><h1>Chưa thể thực hiện yêu cầu</h1><p class="subtitle" role="alert"><c:out value="${empty message ? 'Trang không tồn tại hoặc yêu cầu chưa thể xử lý. Vui lòng quay lại và thử lại.' : message}"/></p><a class="btn secondary" href="${pageContext.request.contextPath}/MainCustomerUI.jsp">Về trang chủ</a></section>
<%@ include file="common/footer.jspf" %>

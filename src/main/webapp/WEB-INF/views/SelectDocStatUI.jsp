<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Thống kê tài liệu"/><c:set var="active" value="stats"/>
<%@ include file="common/header.jspf" %>
<a class="back" href="${pageContext.request.contextPath}/MainManagerUI.jsp">← Quay lại</a><h1>Thống kê tài liệu theo số lần mượn</h1>
<section class="card"><div class="card-head"><div><h2>Khoảng thời gian thống kê</h2><p>Tính theo ngày mượn, bao gồm ngày bắt đầu và ngày kết thúc.</p></div></div>
<c:if test="${not empty errors}"><div class="notice error" role="alert"><c:out value="${errors.dateRange}"/></div></c:if>
<form class="filter-row" method="get" action="${pageContext.request.contextPath}/DocumentBorrowingStatUI.jsp">
<div class="field"><label for="startDate">Từ ngày <span class="required">*</span></label><input id="startDate" name="startDate" type="date" required value="<c:out value='${empty param.startDate and applicationScope.demoMode ? "2026-08-11" : param.startDate}'/>"></div>
<div class="field"><label for="endDate">Đến ngày <span class="required">*</span></label><input id="endDate" name="endDate" type="date" required value="<c:out value='${empty param.endDate and applicationScope.demoMode ? "2026-09-11" : param.endDate}'/>"></div>
<button class="btn">Xem thống kê →</button></form></section>

<%@ include file="common/footer.jspf" %>

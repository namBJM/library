<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Chi tiết phiếu mượn"/><c:set var="active" value="stats"/>
<%@ include file="common/header.jspf" %>
<c:choose><c:when test="${not empty documentId}"><a class="back" href="${pageContext.request.contextPath}/BorrowingReceiptDetailUI.jsp?documentId=${documentId}&amp;startDate=${startDate}&amp;endDate=${endDate}">← Quay lại các lượt mượn</a></c:when><c:otherwise><a class="back" href="${pageContext.request.contextPath}/SelectDocStatUI.jsp">← Quay lại thống kê</a></c:otherwise></c:choose>
<h1>Chi tiết phiếu mượn</h1>
<section class="card receipt-fields"><dl class="receipt-list">
<dt>Mã phiếu mượn</dt><dd><ui:code prefix="PM" value="${receipt.id}"/></dd>
<dt>Mã bạn đọc</dt><dd><c:out value="${receipt.reader.readerID}"/></dd>
<dt>Họ tên bạn đọc</dt><dd><c:out value="${receipt.reader.fullName}"/></dd>
<dt>Số điện thoại</dt><dd><c:out value="${receipt.reader.phone}"/></dd>
<dt>Email</dt><dd><c:out value="${receipt.reader.email}"/></dd>
</dl>
<c:forEach items="${receipt.dsBorrowingReceiptDetail}" var="detail"><dl class="receipt-list document-fields">
<dt>Mã tài liệu</dt><dd><ui:code prefix="TL" value="${detail.documentCopy.document.id}"/></dd>
<dt>Tên tài liệu</dt><dd><c:out value="${detail.documentCopy.document.title}"/></dd>
<dt>Tác giả</dt><dd><c:out value="${detail.documentCopy.document.author}"/></dd>
</dl></c:forEach>
<dl class="receipt-list"><dt>Ngày mượn</dt><dd><ui:date value="${receipt.borrowDate}"/></dd>
<dt>Ngày hẹn trả</dt><dd><ui:date value="${receipt.dueDate}"/></dd>
<dt>Ngày trả thực tế</dt><dd><c:choose><c:when test="${not empty receipt.returnDate}"><ui:date value="${receipt.returnDate}"/></c:when><c:otherwise>Chưa ghi nhận</c:otherwise></c:choose></dd>
<dt>Tình trạng phiếu mượn</dt><dd><ui:status value="${receipt.status}"/></dd></dl></section>
<%@ include file="common/footer.jspf" %>

<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Chi tiết lượt mượn"/><c:set var="active" value="stats"/>
<%@ include file="common/header.jspf" %>
<a class="back" href="${pageContext.request.contextPath}/DocumentBorrowingStatUI.jsp?startDate=${startDate}&amp;endDate=${endDate}">← Quay lại thống kê tài liệu</a>
<h1>Các lượt mượn tài liệu</h1>
<section class="card"><div class="details-intro"><div class="book-tile" aria-hidden="true">▤</div><div><span class="code"><ui:code prefix="TL" value="${document.id}"/></span><h2><c:out value="${document.title}"/></h2><p><c:out value="${document.author}"/> · <c:out value="${document.category}"/> · <c:out value="${document.publisher}"/> (${document.publicationYear})</p></div></div></section>
<section class="card table-card"><div class="table-heading"><h2>${fn:length(listBorrowing)} lượt mượn</h2><span class="range-chip"><ui:date value="${startDate}"/> — <ui:date value="${endDate}"/></span></div>
<c:choose><c:when test="${empty listBorrowing}"><div class="empty"><h2>Không có lượt mượn</h2><p>Tài liệu này không có lượt mượn trong khoảng thời gian đã chọn.</p></div></c:when><c:otherwise>
<div class="table-scroll"><table><thead><tr><th scope="col">STT</th><th scope="col">MÃ PHIẾU MƯỢN</th><th scope="col">BẠN ĐỌC</th><th scope="col">NGÀY MƯỢN</th><th scope="col">THAO TÁC</th></tr></thead><tbody>
<c:forEach items="${listBorrowing}" var="detail" varStatus="row"><tr><td class="code">${row.count}</td><td class="doc-title"><ui:code prefix="PM" value="${detail.borrowingReceipt.id}"/></td><td class="doc-title"><c:out value="${detail.borrowingReceipt.reader.fullName}"/><small><c:out value="${detail.borrowingReceipt.reader.readerID}"/></small></td><td><ui:date value="${detail.borrowingReceipt.borrowDate}"/></td><td><a href="${pageContext.request.contextPath}/BorrowingReceiptUI.jsp?borrowingReceiptId=${detail.borrowingReceipt.id}&amp;documentId=${document.id}&amp;startDate=${startDate}&amp;endDate=${endDate}" aria-label="Xem phiếu mượn">Xem phiếu ↗</a></td></tr></c:forEach>
</tbody></table></div>
</c:otherwise></c:choose></section>
<%@ include file="common/footer.jspf" %>

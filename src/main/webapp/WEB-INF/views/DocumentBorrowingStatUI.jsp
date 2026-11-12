<%@ page pageEncoding="UTF-8" %>
<%@ include file="common/tags.jspf" %>
<c:set var="title" value="Thống kê tài liệu"/><c:set var="active" value="stats"/>
<%@ include file="common/header.jspf" %>
<a class="back" href="${pageContext.request.contextPath}/SelectDocStatUI.jsp?startDate=${startDate}&amp;endDate=${endDate}">← Đổi khoảng thời gian</a>
<h1>Thống kê tài liệu theo số lần mượn</h1>
<div class="heading-row"><span class="range-chip"><ui:date value="${startDate}"/> — <ui:date value="${endDate}"/></span></div>
<div class="metric-grid"><div class="metric"><p>TÀI LIỆU ĐƯỢC MƯỢN</p><strong>${fn:length(listStat)} <small>đầu tài liệu</small></strong></div><div class="metric"><p>TỔNG SỐ LƯỢT MƯỢN</p><strong>${totalBorrows} <small>lượt</small></strong></div><div class="metric"><p>LƯỢT MƯỢN CAO NHẤT</p><strong>${empty listStat ? 0 : listStat[0].borrowQuantity} <small>lượt / tài liệu</small></strong></div></div>
<section class="card table-card"><div class="table-heading"><h2>Danh sách tài liệu</h2><small>Sắp xếp theo số lượt mượn giảm dần</small></div>
<c:choose><c:when test="${empty listStat}"><div class="empty"><div class="empty-icon" aria-hidden="true">▤</div><h2>Không có dữ liệu thống kê phù hợp</h2><p>Chưa có tài liệu được mượn trong khoảng thời gian này. Bạn có thể chọn khoảng thời gian khác.</p></div></c:when><c:otherwise>
<div class="table-scroll"><table><caption class="small-print">Tài liệu được mượn từ <ui:date value="${startDate}"/> đến <ui:date value="${endDate}"/></caption><thead><tr><th scope="col">STT</th><th scope="col">MÃ TÀI LIỆU</th><th scope="col">TÀI LIỆU</th><th scope="col">SỐ LƯỢT MƯỢN</th><th scope="col">THAO TÁC</th></tr></thead><tbody>
<c:forEach items="${listStat}" var="stat" varStatus="row"><tr><td class="code">${row.count}</td><td class="code"><ui:code prefix="TL" value="${stat.id}"/></td><td class="doc-title"><c:out value="${stat.title}"/><small><c:out value="${stat.author}"/></small></td><td><span class="count">${stat.borrowQuantity}</span></td><td><a href="${pageContext.request.contextPath}/BorrowingReceiptDetailUI.jsp?documentId=${stat.id}&amp;startDate=${startDate}&amp;endDate=${endDate}" aria-label="Xem lượt mượn của <c:out value='${stat.title}'/>">Xem chi tiết ↗</a></td></tr></c:forEach>
</tbody></table></div>
</c:otherwise></c:choose></section>
<%@ include file="common/footer.jspf" %>

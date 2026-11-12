<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="value" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<span class="badge ${value eq 'BORROWING' ? 'borrowing' : ''}"><c:choose><c:when test="${value eq 'ACTIVE'}">Hoạt động</c:when><c:when test="${value eq 'RETURNED'}">Đã trả</c:when><c:when test="${value eq 'BORROWING'}">Đang mượn</c:when><c:otherwise><c:out value="${value}"/></c:otherwise></c:choose></span>

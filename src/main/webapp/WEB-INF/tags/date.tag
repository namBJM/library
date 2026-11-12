<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="text" value="${value}"/><c:choose><c:when test="${not empty text}"><c:out value="${fn:substring(text,8,10)}/${fn:substring(text,5,7)}/${fn:substring(text,0,4)}"/></c:when><c:otherwise>—</c:otherwise></c:choose>

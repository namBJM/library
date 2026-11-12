<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="prefix" required="true" type="java.lang.String" %><%@ attribute name="value" required="true" type="java.lang.Integer" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %><c:out value="${prefix}"/><c:if test="${value lt 100}">0</c:if><c:if test="${value lt 10}">0</c:if><c:out value="${value}"/>

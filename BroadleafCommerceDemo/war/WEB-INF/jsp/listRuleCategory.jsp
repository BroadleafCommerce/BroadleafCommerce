<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<jsp:include page="snippets/header.jsp"/>
	
	<h1>Manage Rule Categories</h1>
	
	

	<table>
		<tr><td><b>Category</b></td></tr>
		<c:forEach var="item" items="${ruleCategoryList}">
			<tr>
				<td>${item.name}</td>
			</tr>
		</c:forEach>
	</table>

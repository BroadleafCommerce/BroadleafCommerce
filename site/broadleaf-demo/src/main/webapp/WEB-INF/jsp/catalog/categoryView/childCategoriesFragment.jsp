<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:forEach var="childCategory" items="${currentCategory.childCategories}" varStatus="status">
        <%--getting all the category products as a test is not performant --%>
      	<%--<blc:productsForCategory var="products" categoryId="${childCategory.id}"/>
      	<c:if test="${fn:length(products) gt 0}">--%>
       	<div class="span-4 <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">last</c:if>" align="center">
			<a href="<c:out value="${pageContext.request.contextPath}"/>/${childCategory.generatedUrl}">
				<img border="0" title="${childCategory.name}" alt="${childCategory.name}" src="<c:choose><c:when test="${!(fn:startsWith(childCategory.categoryMedia.small.url,'http')) && fn:startsWith(childCategory.categoryMedia.small.url,'/')}"><c:out value="${pageContext.request.contextPath}"/></c:when></c:choose>${childCategory.categoryMedia.small.url}" width="100" />
			</a><br />
			<a class="noTextUnderline" href="<c:out value="${pageContext.request.contextPath}"/>/${childCategory.generatedUrl}"><b>${childCategory.name}</b></a>
		</div>
		<c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">
			<div class="span-13">&nbsp;</div>
		</c:if>
	<%--</c:if>--%>
</c:forEach>
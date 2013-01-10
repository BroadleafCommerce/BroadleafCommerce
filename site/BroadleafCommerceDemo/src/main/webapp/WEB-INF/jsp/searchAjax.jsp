<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h2 style="color: rgb(86, 111, 50);">
    Search Results
</h2>
<c:choose>
<c:when test="${fn:length(products) == 0}">
    No results were found for your query.
</c:when>
<c:when test="${fn:length(categories) == 1}">
    <c:forEach var="product" items="${products}" varStatus="status">
        <div class="searchProduct span-3">
            <a href="/broadleafdemo/${category.generatedUrl}?productId=${product.id}">
                <img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" width="75"/>
                <br/>
            <c:out value="${product.name}"/></a>
        </div>
        <c:if test="${status.index % 4 == 3}">
            <div style="clear:both"> </div>
        </c:if>
    </c:forEach>
</c:when>
<c:otherwise>
    <c:forEach var="category" items="${categories}" varStatus="status">
        <div class="searchCategory span-13">
            <h3 style="color: rgb(86, 111, 50);"><c:out value="${category.name}"/></h3>
            <c:forEach var="product" items="${categoryGroups[category.id]}" varStatus="status" end="3">
                <div class="searchProduct span-3">
                    <a href="/broadleafdemo/${category.generatedUrl}?productId=${product.id}">
                        <img border="0" title="${product.name}" alt="${product.name}" src="/broadleafdemo${product.productImages.small}" width="75"/>
                        <br/>
                    <c:out value="${product.name}"/></a>
                </div>
            </c:forEach>
            <div style="clear:both"> </div>
        </div>
    </c:forEach>
</c:otherwise>
</c:choose>

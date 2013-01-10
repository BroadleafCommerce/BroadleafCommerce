<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:forEach var="childCategory" items="${currentCategory.childCategories}" varStatus="status">
        <blc:productsForCategory var="products" categoryId="${childCategory.id}"/>
        <c:if test="${fn:length(products) gt 0}">
        <div class="span-4 <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">last</c:if>" align="center">
            <a href="/broadleafdemo/${childCategory.generatedUrl}">
                <img border="0" title="${childCategory.name}" alt="${childCategory.name}" src="/broadleafdemo${childCategory.categoryImages.small}" width="100" />
            </a><br />
            <a class="noTextUnderline" href="/broadleafdemo/${childCategory.generatedUrl}"><b>${childCategory.name}</b></a>
        </div>
        <c:if test="${status.index != 0 && (status.index + 1) % 3 == 0}">
            <div class="span-13">&nbsp;</div>
        </c:if>
    </c:if>
</c:forEach>
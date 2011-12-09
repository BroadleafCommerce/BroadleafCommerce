#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<div class="mainContentAreaFull" style="padding:8px;">
<c:choose>
	<c:when test="${symbol_dollar}{!empty displayProducts}">
		<table>
	 	<c:forEach var="product" items="${symbol_dollar}{displayProducts}" varStatus="status">
	 			<tr>
				<td>
					${symbol_dollar}{product.name}  <br/>
					<c:if test="${symbol_dollar}{!(empty product.manufacturer) }" >
						<span> <b>Manufacturer:</b> ${symbol_dollar}{product.manufacturer} </span> <br/>
					</c:if>
					<c:if test="${symbol_dollar}{!(empty product.model) }" >
						<span> <b>Model:</b> ${symbol_dollar}{product.model} </span> <br/>
					</c:if>
				<td>
				<td>
					${symbol_dollar}<c:out value="${symbol_dollar}{product.sku.retailPrice}" />
				</td>
				<td>
					<a class="addCartBtn" href="<c:url value="/basket/addItem.htm">
					    <c:param name="skuId" value="${symbol_dollar}{product.sku.id}"/>
                        <c:param name="productId" value="${symbol_dollar}{product.id}"/>
						<c:param name="categoryId" value="${symbol_dollar}{product.defaultCategory.id}"/>
						<c:param name="quantity" value="1"/> </c:url>">Add to Cart</a>
				</td>
				</tr>
		</c:forEach>
		</table>
	</c:when>
</c:choose>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" />
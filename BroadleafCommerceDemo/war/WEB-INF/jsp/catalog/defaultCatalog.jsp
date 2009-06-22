<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
	<tiles:putAttribute name="mainContent" type="string">


	<blc:breadcrumb categoryList="${breadcrumbCategories}" />
	<h1>${currentCategory.name}</h1>
	
	<c:choose>
		<c:when test="${!empty currentCategory.childCategories}">


			<c:forEach var="child" items="${currentCategory.childCategories}" varStatus="status">
				<p><a href="/broadleafdemo/${child.generatedUrl}">${child.name}</a></p>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<h2>This category has no sub-categories</h2>
		</c:otherwise>	
	</c:choose>
	
	<c:if test="${fn:length(currentCategory.featuredProducts) > 0 }" >
		<h3> FEATURED PRODUCTS </h3>
		<table border="0">
			<tr>
				<th>Name</th>
			</tr>
			<c:forEach var="featuredProduct" items="${currentCategory.featuredProducts}" varStatus="status">
				<tr>
					<td>
						<blc:productLink product="${featuredProduct.product}" />
			  			<c:if test="${featuredProduct.product.isFeaturedProduct == true}" > 
							--FEATURED PRODUCT!-- 
						</c:if> 
						Promo Message: ${featuredProduct.promotionMessage}
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	
	<c:choose>
		<c:when test="${!empty currentProducts}">
		 	<tags:grid columnCount="4" collection="${currentProducts}">
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.id}">
					<img border="0" title="${item.name}" alt="${item.name}" src="/broadleafdemo${item.productImages.small}" />
				</a><br />
				<a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${item.id}">${item.name}</a>
				<c:if test="${product.isFeaturedProduct}"><br />Featured</c:if>
			</tags:grid>
		</c:when>
		<c:otherwise>
			<h2>This category has no products</h2>
		</c:otherwise>	
	</c:choose>
	
	<% /* not sure how we are going to ge the grid layout to paginate. list is shown below */ %>
	<c:choose>
		<c:when test="${fn:length(currentProducts) > 0}">
			<h2>Here is a list of products under this category:</h2>
			<c:set var="numPerPage" value="15"/>
			<div id="Searchresult">
				This content will be replaced when pagination inits.
			</div>
			<br style="clear:both;" />
	        <!-- Container element for all the Elements that are to be paginated -->
	        <div id="hiddenresult" style="display:none;">
		        <c:forEach var="product" items="${currentProducts}" varStatus="status">
		        	<c:if test="${status.index % numPerPage == 0}">
						<div class="result">
					</c:if>
							<blc:productLink product="${product}" />
							<c:if test="${product.isFeaturedProduct == true}" >
								--FEATURED PRODUCT!-- 
							</c:if>
							<br />
					<c:if test="${((status.index+1) % numPerPage == 0) || (status.last) }">
						</div>
					</c:if>
				</c:forEach>
	        </div>
	        <c:if test="${fn:length(currentProducts) > numPerPage}">
		        <div id="Pagination" class="pagination">
		        </div>
				<br style="clear:both;" />
			</c:if>
			<c:if test="${fn:length(currentProducts) <= numPerPage}">
		        <div id="Pagination" class="pagination" style="display:none;width: 1px; height: 1px;">
		        </div>
			</c:if>
		</c:when>
		<c:otherwise>
			<h2>This category has no products</h2>
		</c:otherwise>	
	</c:choose>

	</tiles:putAttribute>
</tiles:insertDefinition>
<script type="text/javascript">
    // This is a very simple demo that shows how a range of elements can
    // be paginated.

    /**
     * Callback function that displays the content.
     *
     * Gets called every time the user clicks on a pagination link.
     *
     * @param {int}page_index New Page index
     * @param {jQuery} jq the container with the pagination links as a jQuery object
     */
	function pageselectCallback(page_index, jq){
        var new_content = $('#hiddenresult div.result:eq('+page_index+')').clone();
        $('#Searchresult').empty().append(new_content);
        return false;
    }
   
    /** 
     * Callback function for the AJAX content loader.
     */
    function initPagination() {
        var num_entries = $('#hiddenresult div.result').length;
        // Create pagination element
        $("#Pagination").pagination(num_entries, {
            num_edge_entries: 2,
            num_display_entries: 8,
            callback: pageselectCallback,
            items_per_page:1
        });
     }
            
    // Load HTML snippet with AJAX and insert it into the Hiddenresult element
    // When the HTML has loaded, call initPagination to paginate the elements        
    $(document).ready(function(){      
        initPagination();
    });
</script>
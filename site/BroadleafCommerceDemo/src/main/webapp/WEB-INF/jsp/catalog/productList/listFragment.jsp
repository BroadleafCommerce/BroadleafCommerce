<%@ include file="/WEB-INF/jsp/include.jsp" %>

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
                            <c:if test="${product.featuredProduct == true}" >
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
    </c:choose>

    
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
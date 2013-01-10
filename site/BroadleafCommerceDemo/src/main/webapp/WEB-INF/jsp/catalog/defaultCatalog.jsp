<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">

    <script>
        function updateSearchFilterResults() {
            $('#mainContent').prepend("<div class='grayedOut'><img style='margin-top:25px' src='/broadleafdemo/images/ajaxLoading.gif'/></div>");
            var postData = $('#refineSearch').serializeArray();
            postData.push({name:'ajax',value:'true'});
            $('#mainContent').load($('#refineSearch').attr('action'), postData);
        }
    </script>
    
    <div class="breadcrumb">
        <blc:breadcrumb categoryList="${breadcrumbCategories}" />
    </div>
    <div class="catalogContainer columns mainContentAreaFull" style="padding:8px 0 8px 8px;">
    <div class="span-5" style="width:170px;margin-left:10px;">
        <form:form method="post" id="refineSearch" commandName="doSearch">
        <blc:searchFilter categories="${displayCategories}" queryString="">
            <blc:searchFilterItem property="name" displayTitle="Categories" />          
        </blc:searchFilter>
            <blc:searchFilter products="${displayProducts}" queryString="">
        Narrow results by:
                <blc:searchFilterItem property="manufacturer" displayTitle="Manufacturers"/>
                <br />
                <blc:searchFilterItem property="skus[0].salePrice" displayTitle="Prices" displayType="sliderRange"/>
            </blc:searchFilter>

            <blc:displayFeaturedProducts products="${displayProducts}" var="featuredProducts" maxFeatures="3">
                <br/><br/>
                <c:if test="${!(empty featuredProducts)}">
                    <h3>Featured </h3>
                    <c:forEach var="product" items="${featuredProducts}" >
                        <div align="center">
                            <a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
                                <c:out value="${product.name}"/>
                            </a><br>
                            <a href="/broadleafdemo/${currentCategory.generatedUrl}?productId=${product.id}">
                                <img border="0" src="/broadleafdemo${product.productImages.small}" width="75"/>
                            </a>
                        </div>  
                        <br/>
                    </c:forEach>
                </c:if>
            </blc:displayFeaturedProducts>

        </form:form>
    </div>
    <div class="span-14" id="mainContent">
        <jsp:include page="/WEB-INF/jsp/catalog/categoryView/mainContentFragment.jsp" />
    </div>
    <jsp:include page="/WEB-INF/jsp/catalog/promos/categoryPromos.jsp" />
    </div>

    </tiles:putAttribute>
</tiles:insertDefinition>

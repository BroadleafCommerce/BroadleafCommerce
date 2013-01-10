<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
    
    <h3 class="pageTitle" ><b>Manage Orders </b></h3>

    <c:choose>
        <c:when test="${!(empty orderList)}"> 
            <table class="cartTable" border="1">
                <thead>
                    <tr>
                        <th class="alignCenter"></th>
                        <th class="alignCenter">Order Number</th>
                        <th class="alignCenter">Order Date</th>
                        <th class="alignCenter">Total</th>
                        <th class="alignCenter">Status</th>
                    </tr>
                </thead>
                <c:forEach var="item" items="${orderList}" varStatus="status">
                    <tr>
                        <td class="alignCenter"><a class="noTextUnderline" href="viewOrderDetails.htm?orderNumber=${item.orderNumber}"> View Order</a>
                        <td class="alignCenter">${item.orderNumber}</td>
                        <td class="alignCenter"><fmt:formatDate type="date" dateStyle="full" value="${item.submitDate}" /></td>
                        <td class="alignCenter">$${item.total}</td>
                        <td class="alignCenter">${item.status.type}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            You haven't placed any orders!
        </c:otherwise>  
    </c:choose>
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>
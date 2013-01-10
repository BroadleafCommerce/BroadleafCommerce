<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="mainContent" type="string">
    <div class="mainContentAreaFull" style="padding:8px 0 8px 8px;">
    <form:form method="post" action="processFindOrder.htm" commandName="findOrderForm">
        <div class="columns">
            <div class="column span-5">
                <h3 class="pageTitle">Find Order</h3>
                <table class="basicTable">
                    <tr>
                        <td nowrap>Order Number</td>
                        <td><form:input path="orderNumber"/></td>
                    </tr>
                    <tr>
                        <td nowrap>Postal Code</td>
                        <td><form:input path="postalCode"/></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td><button type="submit" name="findOrder" value="Find Order">Find Order</button></td>
                    </tr>
                </table>
            </div>
        </div>
    </form:form>
    </div>
    </tiles:putAttribute>
</tiles:insertDefinition>
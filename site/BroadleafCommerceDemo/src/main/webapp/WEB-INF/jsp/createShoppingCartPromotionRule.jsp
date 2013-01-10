<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security"
    uri="http://www.springframework.org/security/tags"%>
<jsp:include page="snippets/navigation.jsp" />

<style>
.cellLabel {
    text-align: right;
    width: 100px;
}
</style>

<form:form method="post" commandName="shoppingCartPromotion">
    <br />
    <h4 class="formSectionHeader">Promotion Information</h4>
    <spring:hasBindErrors name="shoppingCartPromotion">
        <spring:bind path="shoppingCartPromotion.*">
            <c:forEach var="error" items="${status.errorMessages}">
                <tr>
                    <td><font color="red"><c:out value="${error}" /></font></td>
                </tr>
                <br />
            </c:forEach>
        </spring:bind>
    </spring:hasBindErrors>

    <table class="formTable">

        <tr>
            <td class="cellLabel">Name:</td>
            <td><input type="text" name="name"
                value="${shoppingCartPromotion.name}" /></td>
        </tr>

        <tr>
            <td class="cellLabel">Coupon Code:</td>
            <td><input type="text" name="couponCode"
                value="${shoppingCartPromotion.couponCode}" /></td>
        </tr>

    </table>
    <br />
    <table>
        <tr>
            <td>
            <h4>Condition</h4>
            </td>
        </tr>
        <tr>
            <td class="cellLabel">Order Total:</td>
            <td><select name="logicalOperator">
                <option value="<">is less than</option>
                <option value="<=">is less than or equal to</option>
                <option value=">">is greater than</option>
                <option value=">=">is greater than or equal to</option>
                <option value="==">is equal to</option>
                <option value="!=">is not equal to</option>
            </select></td>
            <td>$</td>
            <td><input type="text" name="orderTotal"
                value="${shoppingCartPromotion.orderTotal}" /></td>
        </tr>

    </table>

    <div class="formButtonFooter personFormButtons">
    <p><input type="submit" class="saveButton" value="Save Changes" /></p>
    </div>

</form:form>
<p>
<a href="<c:url value="/createShoppingCartPromotionTestOrder.htm" />">Create Test Order</a>
</p>
<a href="<c:url value="/logout"/>">Logout</a>


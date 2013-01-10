<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security"
    uri="http://www.springframework.org/security/tags"%>
<jsp:include page="snippets/navigation.jsp" />


<form:form method="post" commandName="couponCode">
    <br />
    <h4 class="formSectionHeader">Order Information</h4>
    <spring:hasBindErrors name="couponCode">
        <spring:bind path="couponCode.*">
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
            <td>
                Coupon Code:
            </td>
            <td>
                <input type="text" name="code" value="${couponCode.code}" />
            </td>
        </tr>
        
        <tr>
            <td>Order Total:</td>
            <td><input type="text" name="orderTotal" />
        </tr>
    
    </table>

    
    <div class="formButtonFooter personFormButtons">
        <p><input type="submit" class="saveButton" value="Save Changes" /></p>
    </div>
    
</form:form>

<a href="<c:url value="/logout"/>">Logout</a>


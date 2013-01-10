<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<div id="header">
    <a href="/broadleafdemo"><img class="logo" src="/broadleafdemo/images/havalettaLogo.png" /></a>
    <a href="http://www.broadleafcommerce.org/" class="promoCTA">This is a demo store. No payments accepted or orders fulfilled.</a>
    <ul id="userNav" class="clearfix">
        <li>
            <c:choose>
                <c:when test="${customer.anonymous}">
                    <a href="/broadleafdemo/registerCustomer/registerCustomer.htm">Login</a>
                </c:when>
                <c:otherwise>
                    <a href="/broadleafdemo/account/myAccount.htm">Hello <c:out value="${customer.firstName}"/></a></li>
                    <li><a href="/broadleafdemo/logout.htm">Logout</a>
                </c:otherwise>
            </c:choose>
        </li>
        <li><a class="noTextUnderline" href="/broadleafdemo/orders/findOrder.htm" > Find Order </a></li>
        <li><a href="/broadleafdemo/storeLocator/findStores.htm">Store Locator</a></li>
        <li class="last"><a class="cartLink" href="/broadleafdemo/basket/viewCart.htm">View Cart</a></li>
    </ul>
    
<ul id="primaryNav" class="clearfix">
    <li><a class="${currentCategory.generatedUrl==null?'active':''}" href="/broadleafdemo/store">Home</a></li>
    <li><a class="${currentCategory.generatedUrl=='store/coffee'?'active':''}" href="/broadleafdemo/store/coffee">Coffee</a></li>
    <li><a class="${currentCategory.generatedUrl=='store/equipment/grinders'?'active':''}" href="/broadleafdemo/store/equipment/grinders">Grinders</a></li>
    <li><a class="${currentCategory.generatedUrl=='store/equipment/brewers'?'active':''}" href="/broadleafdemo/store/equipment/brewers">Coffee Brewers</a></li>
    <li><a class="${currentCategory.generatedUrl=='store/equipment/espresso'?'active':''}" href="/broadleafdemo/store/equipment/espresso">Espresso Machines</a></li>
</ul>
    
    
    <div id="searchBar">
        <form id="search" method="post" action="/broadleafdemo/search/results.htm">
            <input class="searchField" type="text" name="queryString" id="queryString" size="30" helpText="Search the store..." /><input type="image"
            class="imageBtn" src="/broadleafdemo/images/searchBtn.png" alt="Search" />
        </form>
    </div>
    <img class="slogan" src="/broadleafdemo/images/slogan2.gif" />
</div>


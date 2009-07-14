<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<div id="header"><a href="/broadleafdemo"><img class="logo" src="/broadleafdemo/images/demoLogo.gif" /></a>
<ul id="userNav" class="clearfix">
	<li><a href="/broadleafdemo/registerCustomer/registerCustomer.htm">Login</a></li>
	<li><a href="#">Help</a></li>
	<li><a href="/broadleafdemo/account/myAccount.htm">My Account</a></li>
	<li><a href="/broadleafdemo/storeLocator/findStores.htm">Store Locator</a></li>
	<li class="last"><a href="/broadleafdemo/basket/viewCart.htm">View Cart</a></li>

</ul>
<div id="searchBar">
<form id="search" method="post" action="/broadleafdemo/search/results.htm">
<input class="searchField" type="text" name="queryString" id="queryString" size="30" helpText="Search the store..." /><input type="image"
	class="imageBtn" src="/broadleafdemo/images/goButton.gif" alt="Search" />
	</form></div>
</div>
<div class="slogan"><img src="/broadleafdemo/images/slogan.gif" /></div>
<div class="contentWrapper navGradientBg">
<ul id="primaryNav" class="clearfix">
	<li>&nbsp;&nbsp;</li>
	<li><a class="${currentCategory.generatedUrl=='store'?'active':''}" href="/broadleafdemo/store">Home</a></li>
	<li><a class="${currentCategory.generatedUrl=='store/coffee'?'active':''}" href="/broadleafdemo/store/coffee">Coffee</a></li>
	<li><a href="#">Tea</a></li>
	<li><a class="${currentCategory.generatedUrl=='store/equipment/grinders'?'active':''}" href="/broadleafdemo/store/equipment/grinders">Grinders</a></li>
	<li><a class="${currentCategory.generatedUrl=='store/equipment/brewers'?'active':''}" href="/broadleafdemo/store/equipment/brewers">Coffee Brewers</a></li>
	<li><a class="${currentCategory.generatedUrl=='store/equipment/espresso'?'active':''}" href="/broadleafdemo/store/equipment/espresso">Espresso Machines</a></li>
	<li><a href="#">Accessories</a></li>
</ul>
</div>

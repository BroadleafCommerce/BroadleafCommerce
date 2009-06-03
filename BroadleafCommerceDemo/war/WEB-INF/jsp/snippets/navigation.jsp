<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ taglib prefix="form" uri="/spring-form"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<div id="header"><a href="/broadleafdemo"><img class="logo" src="/broadleafdemo/images/demoLogo.gif" /></a>
<ul id="userNav" class="clearfix">
	<li><a href="#">Help</a></li>
	<li><a href="#">My Account</a></li>
	<li class="last"><a href="#">View Cart</a></li>
</ul>
<div id="searchBar"><input type="text" size="20" /><input type="submit" value="GO" /></div>
</div>

<ul id="primaryNav" class="clearfix">
	<li><a href="/broadleafdemo/store">Home</a></li>
	<li><a href="/broadleafdemo/store/coffee">Coffee</a></li>
	<li><a href="#">Tea</a></li>
	<li><a href="/broadleafdemo/store/equipment/grinders">Grinders</a></li>
	<li><a href="/broadleafdemo/store/equipment/brewers">Coffee Brewers</a></li>
	<li><a href="/broadleafdemo/store/equipment/espresso">Espresso Machines</a></li>
	<li><a href="#">Accessories</a></li>
</ul>

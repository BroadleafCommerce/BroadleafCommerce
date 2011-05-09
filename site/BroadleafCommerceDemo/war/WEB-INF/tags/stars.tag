<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="numberOfStars" required="true" description="Integer from 0 to 5; How many stars to display." %>
<%@ attribute name="cssClass" required="false" description="Renders the class attribute on the div element that contains the stars." %>

<div class="${cssClass}">
	<c:forEach begin="1" end="5" varStatus="starLoop">
		<c:choose>
		<c:when test="${starLoop.index <= numberOfStars}">
			<div class="ui-stars-star ui-stars-star-disabled ui-stars-star-on"><a title="">${starLoop.index}</a></div>
		</c:when>
		<c:otherwise>
			<div class="ui-stars-star ui-stars-star-disabled"><a title="">${starLoop.index}</a></div>
		</c:otherwise>
		</c:choose>
	</c:forEach>
</div>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ variable name-given="item" variable-class="java.lang.Object" scope="NESTED" %>

<%@ attribute name="rowClass"  description="Class name that will be applied to each row container element." %>
<%@ attribute name="cellClass" description="Class name that will be applied to each cell container element." %>
<%@ attribute name="firstRowClass" description="Class name that will be applied only to the first row container element." %>
<%@ attribute name="columnCount" required="true" description="Number of columns in the grid." %>
<%@ attribute name="collection" required="true" type="java.lang.Object" description="Collection of objects to iterate over." %>
<%@ attribute name="startIndex" description="Starting index." %>
<%@ attribute name="endIndex" description="Ending index." %>
<%@ attribute name="renderAsTable" description="Use TABLE layout instead of default DIV layout." %>

<c:if test="${!empty collection}">
	<c:set var="collectionLength" value="${fn:length(collection)}" />
	<c:if test="${empty endIndex}">
		<c:set var="endIndex" value="${collectionLength-1}" />
	</c:if>
	<c:if test="${endIndex < 0}">
		<c:set var="endIndex" value="0" />
	</c:if>
	<c:if test="${empty startIndex || startIndex < 0}">
		<c:set var="startIndex" value="0" />
	</c:if>

	<fmt:formatNumber var="columnWidth" type="number" maxFractionDigits="2" value="${(100/columnCount)-0.1}" />

	<c:choose>
	<c:when test="${renderAsTable}">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<c:forEach var="rowStart" begin="${startIndex}" end="${endIndex}" step="${columnCount}" varStatus="rowStatus">
		<c:if test="${!rowStatus.first}"><c:remove var="firstRowClass" /></c:if>
		<tr<c:if test="${!empty rowClass || !empty firstRowClass}"> class="${rowClass}${empty firstRowClass?'':' '}${firstRowClass}"</c:if>>
			<c:forEach begin="${rowStart}" end="${(rowStart+columnCount-1 > endIndex) ? endIndex : rowStart+columnCount-1}" var="item" items="${collection}">
				<td<c:if test="${!empty cellClass}"> class="${cellClass}"</c:if>>
					<jsp:doBody />
				</td>
			</c:forEach>
		</tr>
	</c:forEach>
	</table>
	</c:when>
	<c:otherwise>
		<c:if test="${empty rowClass}">
			<c:set var="rowClass" value="columns" />
		</c:if>
		<c:if test="${empty cellClass}">
			<c:set var="cellClass" value="column" />
		</c:if>

		<c:forEach var="rowStart" begin="${startIndex}" end="${endIndex}" step="${columnCount}" varStatus="rowStatus">
			<c:if test="${!rowStatus.first}"><c:remove var="firstRowClass" /></c:if>
			<div class="${rowClass}${empty firstRowClass?'':' '}${firstRowClass}">
				<c:forEach begin="${rowStart}" end="${(rowStart+columnCount-1 > endIndex) ? endIndex : rowStart+columnCount-1}" var="item" items="${collection}">
					<div class="${cellClass}" style="width:${columnWidth}%;">
						<jsp:doBody />
					</div>
				</c:forEach>
			</div>
		</c:forEach>
	</c:otherwise>
	</c:choose>

</c:if>
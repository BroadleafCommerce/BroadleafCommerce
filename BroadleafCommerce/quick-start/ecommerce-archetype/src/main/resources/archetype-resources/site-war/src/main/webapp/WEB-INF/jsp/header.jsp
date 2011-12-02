#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
<head>
<!-- Blueprint Framework CSS -->
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/blueprint/screen.css" />

<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/layout.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/master.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/navigation.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/style.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/pagination.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/thickbox.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/search.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/product.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/category.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/checkout.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/order.css" />
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/ui.stars.css" />


<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/jquery-smoothness/jquery-ui-1.7.2.custom.css" />

<!--[if lt IE 8]>
<link rel="stylesheet" href="css/blueprint/ie.css" type="text/css" media="screen, projection">
<![endif]-->

<!--[if IE]>
<link rel="stylesheet" type="text/css" href="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/css/ieConditional.css" />
<![endif]-->
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/jquery-1.3.2.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/behaviors.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/jquery.pagination.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/ui.core.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/ui.slider.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/thickbox-compressed.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/product.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/checkout.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/ui.stars.js"></script>
<script type="text/javascript" src="<c:out value="${symbol_dollar}{pageContext.request.contextPath}"/>/js/catalog.js"></script>
</head>
<body>
<tags:sandboxRibbon jqueryUIDatepicker="${symbol_dollar}{pageContext.request.contextPath}/js/ui.datepicker.js"/>
<div style="height: 100px; width: 1px"></div>
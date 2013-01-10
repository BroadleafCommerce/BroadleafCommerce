<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<a href="/browse/index.jhtml?CATID=72586">  
    <img src="<c:out value="${item.itemAttributes.imageUrl}"/>" border="0" alt="<c:out value="${item.name}"/>" title="<c:out value="${item.name}"/>" width="67" height="80">
</a>

<p valign="bottom">
    <a href="/browse/index.jhtml?CATID=72586">
        <c:out value="${item.name}"/>
    </a>&nbsp;&nbsp;<img src="http://images.containerstore.com/images/etc/rightArrow.gif">
</p>


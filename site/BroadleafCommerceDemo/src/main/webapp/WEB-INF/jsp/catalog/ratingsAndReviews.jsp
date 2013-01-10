<%@ include file="/WEB-INF/jsp/include.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
    $(".interactiveRating").stars({inputType:"select",captionEl: $("#stars-cap")});
    $("#submitReview").click(function(){
        $.ajax({
            type: "GET",
            url: "/broadleafdemo/rating/saveReview.htm",
            data: {
                productId: ${currentProduct.id},
                reviewText: $("#reviewText").val(),
                rating: $("input[name='customerRating']").val()
            },
            success: function(data) {
                $("#yourReview").html(data);
            }
        });
        return false;
    });
});
</script>

<h4 class="topRule" style="color:#566F32;font-size:18px;">Customer Ratings & Reviews</h4>
<div class="columns">
    <div class="productLeftCol column span-6">
        <h4 style="margin-bottom:6px;">Average Customer Review</h4>
        <fmt:formatNumber value="${ratingSummary.averageRating}" maxFractionDigits="0" var="avgRating" />
        <tags:stars numberOfStars="${avgRating}" cssClass="reviewStars" /> (${fn:length(ratingSummary.reviews)} customer reviews)
    </div>
<c:choose>
    <c:when test="${customer.registered}">
    <div class="productRightCol">
        <h4 style="margin-bottom:6px;">Do you own this product?</h4>
        <a class="reviewBtn" href="#" onclick="$('#yourReview').slideDown();return false;">Write a Review</a>
    </div>
    </c:when>
    <c:otherwise>
    <div class="productRightCol">
        <h4 style="margin-bottom:6px;">Login to write a review.</h4>
    </div>
    </c:otherwise>
</c:choose>
</div>
<div id="yourReview" class="clearfix span-11" style="display:none;background:#fff0c5;border:1px solid #edd38a;margin:10px 0;">
<div style="margin:8px;">
    <div class="clearfix" style="font-size:13px;font-weight:normal;margin:0 0 6px 0;">
    <div style="float:left;margin-right:12px;">Please rate this product:</div>
        <span class="interactiveRating"><select name="customerRating">
            <option value="1">1 Star (Poor)</option>
            <option value="2">2 Stars (Below Average)</option>
            <option value="3">3 Stars (Average)</option>
            <option value="4">4 Stars (Good)</option>
            <option value="5">5 Stars (Excellent)</option>
        </select></span><span id="stars-cap" style="margin-left:6px;color:#000;">&nbsp;</span>
    </div>
    
    <p style="font-size:13px;font-weight:normal;margin:0;">Please describe your experience with this product:</p>
    <textarea id="reviewText" style="height: 200px; width: 97%;"></textarea><br />
    <div style="text-align: right;margin:2px 0 5px;"><a href="#" onclick="$('#yourReview').slideUp();return false;">Cancel</a>&nbsp;&nbsp;&nbsp;<a id="submitReview" class="reviewBtn" href="#">Submit Review</a></div>
</div>
</div>

<c:if test="${!empty ratingSummary.reviews}"><h4 style="margin:8px 0 4px 0;">Customer Reviews</h4></c:if>

<c:forEach var="review" items="${ratingSummary.reviews}">
    Rating: ${review.ratingDetail.rating}
    <fmt:formatNumber value="${review.ratingDetail.rating}" maxFractionDigits="0" var="rating" />
    <h4 style="margin:8px 0 4px 0;"><tags:stars numberOfStars="${rating}" cssClass="reviewStars" /><fmt:formatDate type="date" dateStyle="full" value="${review.reviewSubmittedDate}" /></h4>
    <p>${review.reviewText}
    </p>
    
</c:forEach>
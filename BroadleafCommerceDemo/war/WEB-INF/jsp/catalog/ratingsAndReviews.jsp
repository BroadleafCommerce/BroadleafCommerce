<%@ include file="/WEB-INF/jsp/include.jsp"%>
<script type="text/javascript">
$(document).ready(function(){
	$(".interactiveRating").stars({inputType:"select",captionEl: $("#stars-cap")});
});
</script>

<h4 style="border-top:1px dashed #EE2200;font-weight:bold;color:#555;margin-top:10px;padding-top:10px;">Customer Ratings & Reviews</h4>
<div class="columns">
	<div class="productLeftCol column span-7">
		<h4 style="margin-bottom:6px;">Average Customer Review</h4>
		<tags:stars numberOfStars="3" cssClass="reviewStars" /> (3 customer reviews)
	</div>
	<div class="column span-5" style="border-left:1px solid #ccc;padding:0 0 8px 10px;">
	<h4 style="margin-bottom:6px;">Do you own this product?</h4>
	<a class="reviewBtn" href="#" onclick="$('#yourReview').slideDown();return false;">Write a Review</a>
	</div>
</div>
<div id="yourReview" class="clearfix span-13" style="display:none;background:#eef;border:1px solid #dde;padding:6px;margin:10px 0;">
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
	<textarea class="span-13" style="height: 200px; width: 100%;"></textarea><br />
	<div style="text-align: right;margin:2px 0 5px;"><a href="#" onclick="$('#yourReview').slideUp();return false;">Cancel</a>&nbsp;&nbsp;&nbsp;<a class="reviewBtn" href="#">Submit Review</a></div>
</div>

<h4 style="margin:8px 0 4px 0;">Customer Reviews</h4>

<h4 style="margin:8px 0 4px 0;"><tags:stars numberOfStars="2" cssClass="reviewStars" /> February 9, 2007</h4>
<p>I bought this grinder after seeing it recommended on another site. Unfortunately, I quickly discovered that this grinder does not do a good job of fine grinding. Also, the lid is a little finicky going on and can't be used to hold the ground coffee. If you need your coffee ground fine, and don't want to shell out the bucks for a burr grinder, try the Braun KSM2.
</p>

<h4 style="margin:8px 0 4px 0;"><tags:stars numberOfStars="2" cssClass="reviewStars" /> September 24, 2003</h4>
<p>This coffee grinder is very inconvenient to use--getting the ground coffee out of the machine is difficult and messy. Removing the coffee requires a tiny spoon or a brush, and even then is incomplete, leaving dregs in the grinder to turn rancid and spoil the flavor of future coffee, and a mess on the counter. I've used several less expensive models that were lots less trouble. Also, the body of the grinder is too fat to pick up securely with one hand, creating a tendency to drop it and make an even bigger mess. It looks like very little thought went into this design.
</p>

<h4 style="margin:8px 0 4px 0;"><tags:stars numberOfStars="5" cssClass="reviewStars" /> December 18, 2006</h4>
<p>I've had nothing but good from this grinder and would recommend it. I AM drinking more coffee because it produces such a fine smooth cup. I shake it around as it hums and tap it out into my Bodum French Press and, man, what a good cup of coffee. It has made me move up the coffee bean path (via Whole Foods) from my good old family standard. Ethiopian beans, fresh ground with this machine, fresh shaked into the press and zapp! I'm awake to sip and enjoy my morning. It's been the good machine you'd expect from Bodum and presents a pleasant experience. I fresh grind all my coffee now.
</p>
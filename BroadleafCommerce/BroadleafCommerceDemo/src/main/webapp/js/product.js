 $(document).ready(function(){
   $("#fbLink").attr("href", "http://www.facebook.com/share.php?u=" + window.location);
   $("#diggLink").attr("href", "http://digg.com/submit?url=" + escape(window.location));
   $("#deliciousLink").attr("href", "http://del.icio.us/post?noui&jump=close&url=" + window.location);
 });
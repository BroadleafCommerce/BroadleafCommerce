/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

/**
 * Attempt to force the middle part of the breadcrumb on entity detail pages to go back to the filtered
 * main entity list
 */
$(document).ready(function() {
   var $midBcLink = $('a.back-button');
   var url = $midBcLink.attr('href');

   var full = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
   var ref = document.referrer;
   
   // If the referrer is blank, we don't need to do anything special
   if (ref == undefined || ref == '') {
       return false;
   }
   
   // If the referring URL starts with our current location...
   if (ref.indexOf(full + url) == 0) {
       ref = ref.substring(full.length + url.length);
   
       // and the remainder of the referring URL only consists of query parameters...
       if (ref.charAt(0) == '?') {
            // we will send the user back to where they were with the filters
           $midBcLink.attr('href', document.referrer);
       }
   }
});

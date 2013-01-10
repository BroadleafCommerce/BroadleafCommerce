/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.controller.catalog;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("blRatingController")
@RequestMapping("/rating")
public class RatingController {
    
    private static final Log LOG = LogFactory.getLog(RatingController.class);
    
    @Resource(name="blCustomerState")
    protected CustomerState customerState;
    @Resource(name="blRatingService")
    private RatingService ratingService;
    
    @RequestMapping(value = "/saveReview.htm", method = {RequestMethod.GET})
    public String saveReview(HttpServletRequest request) {
        LOG.debug("Save Review Request recieved");
        LOG.debug("Product Id = " + request.getParameter("productId"));     
        ratingService.reviewItem(request.getParameter("productId"), new RatingType("PRODUCT"), customerState.getCustomer(request), Double.valueOf(request.getParameter("rating")), request.getParameter("reviewText"));
        return "catalog/reviewSubmitted";
    }

}

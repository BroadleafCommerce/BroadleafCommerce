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
package org.broadleafcommerce.rating.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.web.CartController;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.rating.service.RatingService;
import org.broadleafcommerce.rating.service.type.RatingType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("blRatingController")
public class RatingController {

    private static final Log LOG = LogFactory.getLog(CartController.class);

    @Resource(name="blRatingService")
    protected final RatingService ratingService;

    @Resource(name="blCustomerState")
    protected final CustomerState customerState;

    public RatingController() {
        this.ratingService = null;
        this.customerState = null;
    }


    @RequestMapping(value = "saveReview.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public String saveReview(@RequestParam(required=true) String productId,
            @RequestParam(required=true) Double rating,
            @RequestParam(required=true) String reviewText,
            HttpServletRequest request) {

        ratingService.reviewItem(productId, RatingType.PRODUCT, customerState.getCustomer(request), rating, reviewText);

        return "catalog/reviewSubmitted";
    }


}

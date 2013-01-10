package org.broadleafcommerce.catalog.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.rating.service.RatingService;
import org.broadleafcommerce.rating.service.type.RatingType;
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

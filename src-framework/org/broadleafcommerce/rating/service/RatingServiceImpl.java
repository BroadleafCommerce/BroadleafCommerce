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
package org.broadleafcommerce.rating.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.rating.dao.RatingSummaryDao;
import org.broadleafcommerce.rating.dao.ReviewDetailDao;
import org.broadleafcommerce.rating.domain.RatingDetail;
import org.broadleafcommerce.rating.domain.RatingDetailImpl;
import org.broadleafcommerce.rating.domain.RatingSummary;
import org.broadleafcommerce.rating.domain.RatingSummaryImpl;
import org.broadleafcommerce.rating.domain.ReviewDetail;
import org.broadleafcommerce.rating.domain.ReviewDetailImpl;
import org.broadleafcommerce.rating.domain.ReviewFeedback;
import org.broadleafcommerce.rating.domain.ReviewFeedbackImpl;
import org.broadleafcommerce.rating.service.type.RatingSortType;
import org.broadleafcommerce.rating.service.type.RatingType;
import org.broadleafcommerce.time.SystemTime;

public class RatingServiceImpl implements RatingService {

    @Resource
    private RatingSummaryDao ratingSummaryDao;

    @Resource
    private ReviewDetailDao reviewDetailDao;

    public void deleteRatingSummary(RatingSummary ratingSummary) {
        ratingSummaryDao.deleteRatingSummary(ratingSummary);
    }

    public void markReviewHelpful(Long reviewId, Customer customer, Boolean helpful) {
        ReviewDetail reviewDetail = reviewDetailDao.readReviewDetailById(reviewId);

        if (reviewDetail != null) {
            ReviewFeedback reviewFeedback = new ReviewFeedbackImpl(customer, helpful, reviewDetail);
            reviewDetail.getReviewFeedback().add(reviewFeedback);
            reviewDetailDao.saveReviewDetail(reviewDetail);
        }

    }

    public void rateItem(String itemId, RatingType type, Customer customer, Double rating) {
        RatingSummary ratingSummary = this.readRatingSummary(itemId, type);

        if (ratingSummary == null) {
            ratingSummary = new RatingSummaryImpl(itemId, type);
        }

        RatingDetail ratingDetail = new RatingDetailImpl(ratingSummary, rating, SystemTime.asDate(), customer);
        ratingSummary.getRatings().add(ratingDetail);
        ratingSummaryDao.saveRatingSummary(ratingSummary);
    }

    public RatingSummary readRatingSummary(String itemId, RatingType type) {
        return ratingSummaryDao.readRatingSummary(itemId, type);
    }

    public Map<String, RatingSummary> readRatingSummaries(List<String> itemIds, RatingType type) {
        List<RatingSummary> ratings = ratingSummaryDao.readRatingSummaries(itemIds, type);
        Map<String, RatingSummary> ratingsMap = new HashMap<String, RatingSummary>();

        for (RatingSummary ratingSummary : ratings) {
            ratingsMap.put(ratingSummary.getItemId(), ratingSummary);
        }

        return ratingsMap;
    }

    @SuppressWarnings("unchecked")
    public List<ReviewDetail> readReviews(String itemId, RatingType type, int start, int finish, RatingSortType sortBy) {
        RatingSummary summary = this.readRatingSummary(itemId, type);
        List<ReviewDetail> reviews = summary.getReviews();
        List<ReviewDetail> reviewsToReturn = new ArrayList<ReviewDetail>();
        int i = 0;
        for (ReviewDetail review : reviews) {
            if (i > finish) {
                break;
            }

            if (i >= start) {
                reviewsToReturn.add(review);
            }

            i++;
        }

        String sortByBeanProperty = "reviewSubmittedDate";
        if (sortBy == RatingSortType.MOST_HELPFUL) {
            sortByBeanProperty = "helpfulCount";
        }

        Collections.sort(reviewsToReturn, new BeanComparator(sortByBeanProperty));

        return reviewsToReturn;
    }

    public RatingSummary saveRatingSummary(RatingSummary ratingSummary) {
        return ratingSummaryDao.saveRatingSummary(ratingSummary);
    }

    public void reviewItem(String itemId, RatingType type, Customer customer, String reviewText) {
        RatingSummary ratingSummary = this.readRatingSummary(itemId, type);

        if (ratingSummary == null) {
            ratingSummary = new RatingSummaryImpl(itemId, type);
        }

        ReviewDetail reviewDetail = new ReviewDetailImpl(customer, SystemTime.asDate(), reviewText, ratingSummary);
        ratingSummary.getReviews().add(reviewDetail);
        ratingSummaryDao.saveRatingSummary(ratingSummary);
    }

}

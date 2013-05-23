/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.rating.domain;

import org.broadleafcommerce.core.rating.service.type.ReviewStatusType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_REVIEW_DETAIL")
public class ReviewDetailImpl implements ReviewDetail {

    @Id
    @GeneratedValue(generator = "ReviewDetailId")
    @GenericGenerator(
        name="ReviewDetailId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ReviewDetailImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.rating.domain.ReviewDetailImpl")
        }
    )
    @Column(name = "REVIEW_DETAIL_ID")
    private Long id;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    @Index(name="REVIEWDETAIL_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Customer customer;

    @Column(name = "REVIEW_SUBMITTED_DATE", nullable = false)
    protected Date reivewSubmittedDate;

    @Column(name = "REVIEW_TEXT", nullable = false)
    protected String reviewText;

    @Column(name = "REVIEW_STATUS", nullable = false)
    @Index(name="REVIEWDETAIL_STATUS_INDEX", columnNames={"REVIEW_STATUS"})
    protected String reviewStatus;

    @Column(name = "HELPFUL_COUNT", nullable = false)
    protected Integer helpfulCount;

    @Column(name = "NOT_HELPFUL_COUNT", nullable = false)
    protected Integer notHelpfulCount;

    @ManyToOne(optional = false, targetEntity = RatingSummaryImpl.class)
    @JoinColumn(name = "RATING_SUMMARY_ID")
    @Index(name="REVIEWDETAIL_SUMMARY_INDEX", columnNames={"RATING_SUMMARY_ID"})
    protected RatingSummary ratingSummary;

    @OneToMany(mappedBy = "reviewDetail", targetEntity = ReviewFeedbackImpl.class, cascade = {CascadeType.ALL})
    protected List<ReviewFeedback> reviewFeedback;

    @OneToOne(targetEntity = RatingDetailImpl.class)
    @JoinColumn(name = "RATING_DETAIL_ID")
    @Index(name="REVIEWDETAIL_RATING_INDEX", columnNames={"RATING_DETAIL_ID"})
    protected RatingDetail ratingDetail;

    public ReviewDetailImpl() {}

    public ReviewDetailImpl(Customer customer, Date reivewSubmittedDate, RatingDetail ratingDetail, String reviewText, RatingSummary ratingSummary) {
        super();
        this.customer = customer;
        this.reivewSubmittedDate = reivewSubmittedDate;
        this.reviewText = reviewText;
        this.ratingSummary = ratingSummary;
        this.reviewFeedback = new ArrayList<ReviewFeedback>();
        this.helpfulCount = Integer.valueOf(0);
        this.notHelpfulCount = Integer.valueOf(0);
        this.reviewStatus = ReviewStatusType.PENDING.getType();
        this.ratingDetail = ratingDetail;
    }

    @Override
    public Date getReviewSubmittedDate() {
        return reivewSubmittedDate;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getReviewText() {
        return reviewText;
    }

    @Override
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    @Override
    public ReviewStatusType getStatus() {
        return new ReviewStatusType(reviewStatus);
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public Integer helpfulCount() {
        return helpfulCount;
    }

    @Override
    public Integer notHelpfulCount() {
        return notHelpfulCount;
    }

    @Override
    public RatingSummary getRatingSummary() {
        return ratingSummary;
    }

    @Override
    public RatingDetail getRatingDetail() {
        return ratingDetail;
    }

    @Override
    public List<ReviewFeedback> getReviewFeedback() {
        return reviewFeedback == null ? new ArrayList<ReviewFeedback>() : reviewFeedback;
    }

}

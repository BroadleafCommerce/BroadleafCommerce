/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.rating.domain;

import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_REVIEW_FEEDBACK")
public class ReviewFeedbackImpl implements ReviewFeedback {

    @Id
    @GeneratedValue(generator = "ReviewFeedbackId")
    @GenericGenerator(
        name="ReviewFeedbackId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ReviewFeedbackImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.rating.domain.ReviewFeedbackImpl")
        }
    )
    @Column(name = "REVIEW_FEEDBACK_ID")
    protected Long id;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    @Index(name="REVIEWFEED_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Customer customer;

    @Column(name = "IS_HELPFUL", nullable = false)
    protected Boolean isHelpful = false;

    @ManyToOne(optional = false, targetEntity = ReviewDetailImpl.class)
    @JoinColumn(name = "REVIEW_DETAIL_ID")
    @Index(name="REVIEWFEED_DETAIL_INDEX", columnNames={"REVIEW_DETAIL_ID"})
    protected ReviewDetail reviewDetail;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public ReviewDetail getReviewDetail() {
        return reviewDetail;
    }

    @Override
    public Boolean getIsHelpful() {
        return isHelpful;
    }

    @Override
    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void setReviewDetail(ReviewDetail reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

}

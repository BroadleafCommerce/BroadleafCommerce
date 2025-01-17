/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_REVIEW_FEEDBACK", indexes = {
        @Index(name = "REVIEWFEED_CUSTOMER_INDEX", columnList = "CUSTOMER_ID"),
        @Index(name = "REVIEWFEED_DETAIL_INDEX", columnList = "REVIEW_DETAIL_ID")
})
public class ReviewFeedbackImpl implements ReviewFeedback, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ReviewFeedbackId")
    @GenericGenerator(
            name = "ReviewFeedbackId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "ReviewFeedbackImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.core.rating.domain.ReviewFeedbackImpl")
            }
    )
    @Column(name = "REVIEW_FEEDBACK_ID")
    protected Long id;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "ReviewFeedback_customer")
    @AdminPresentationToOneLookup
    protected Customer customer;

    @Column(name = "IS_HELPFUL", nullable = false)
    @AdminPresentation(friendlyName = "ReviewFeedback_isHelpful")
    protected Boolean isHelpful = false;

    @ManyToOne(optional = false, targetEntity = ReviewDetailImpl.class)
    @JoinColumn(name = "REVIEW_DETAIL_ID")
    @AdminPresentationToOneLookup
    @AdminPresentation(friendlyName = "ReviewFeedback_reviewDetail")
    protected ReviewDetail reviewDetail;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public ReviewDetail getReviewDetail() {
        return reviewDetail;
    }

    @Override
    public void setReviewDetail(ReviewDetail reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

    @Override
    public Boolean getIsHelpful() {
        return isHelpful;
    }

    @Override
    public void setIsHelpful(Boolean isHelpful) {
        this.isHelpful = isHelpful;
    }

}

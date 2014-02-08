/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

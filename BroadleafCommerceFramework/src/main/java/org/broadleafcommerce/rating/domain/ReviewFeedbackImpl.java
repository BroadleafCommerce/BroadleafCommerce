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
package org.broadleafcommerce.rating.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_REVIEW_FEEDBACK")
public class ReviewFeedbackImpl implements ReviewFeedback {

    @Id
    @GeneratedValue(generator = "ReviewFeedbackId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ReviewFeedbackId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ReviewFeedbackImpl", allocationSize = 50)
    @Column(name = "REVIEW_FEEDBACK_ID")
    protected Long id;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Column(name = "IS_HELPFUL", nullable = false)
    protected Boolean isHelpful;

    @ManyToOne(optional = false, targetEntity = ReviewDetailImpl.class)
    @JoinColumn(name = "REVIEW_DETAIL_ID")
    protected ReviewDetail reviewDetail;

    public Long getId() {
        return id;
    }

    public ReviewFeedbackImpl(Customer customer, Boolean isHelpful, ReviewDetail reviewDetail) {
        super();
        this.customer = customer;
        this.isHelpful = isHelpful;
        this.reviewDetail = reviewDetail;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Boolean isHelpful() {
        return isHelpful;
    }

    public ReviewDetail getReviewDetail() {
        return reviewDetail;
    }

}

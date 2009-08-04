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

import java.util.Date;

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
@Table(name = "BLC_RATING_DETAIL")
public class RatingDetailImpl implements RatingDetail {

    @Id
    @GeneratedValue(generator = "RatingDetailId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "RatingDetailId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "RatingDetailImpl", allocationSize = 50)
    @Column(name = "RATING_DETAIL_ID")
    private Long id;

    @Column(name = "RATING", nullable = false)
    protected Double rating;

    @Column(name = "RATING_SUBMITTED_DATE", nullable = false)
    protected Date ratingSubmittedDate;

    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @ManyToOne(optional = false, targetEntity = RatingSummaryImpl.class)
    @JoinColumn(name = "RATING_SUMMARY_ID")
    protected RatingSummary ratingSummary;

    public RatingDetailImpl() {
    }

    public RatingDetailImpl(RatingSummary ratingSummary, Double rating, Date ratingSubmittedDate, Customer customer) {
        super();
        this.ratingSummary = ratingSummary;
        this.rating = rating;
        this.ratingSubmittedDate = ratingSubmittedDate;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public Double getRating() {
        return rating;
    }

    public Date getRatingSubmittedDate() {
        return ratingSubmittedDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setRating(Double newRating) {
        this.rating = newRating;
    }

    public RatingSummary getRatingSummary() {
        return ratingSummary;
    }
}

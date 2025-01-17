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

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "BLC_RATING_DETAIL", indexes = {@Index(name = "RATING_CUSTOMER_INDEX", columnList = "CUSTOMER_ID")})
public class RatingDetailImpl implements RatingDetail, Serializable {

    @Column(name = "RATING", nullable = false)
    @AdminPresentation(friendlyName = "RatingDetail_rating", prominent = true)
    protected Double rating;
    @Column(name = "RATING_SUBMITTED_DATE", nullable = false)
    @AdminPresentation(friendlyName = "RatingDetail_submittedDate", prominent = true)
    protected Date ratingSubmittedDate;
    @ManyToOne(targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "RatingDetail_customer", prominent = true)
    @AdminPresentationToOneLookup
    protected Customer customer;
    @ManyToOne(optional = false, targetEntity = RatingSummaryImpl.class)
    @JoinColumn(name = "RATING_SUMMARY_ID")
    @AdminPresentation(friendlyName = "RatingDetail_summary")
    @AdminPresentationToOneLookup
    protected RatingSummary ratingSummary;
    @Id
    @GeneratedValue(generator = "RatingDetailId")
    @GenericGenerator(
            name = "RatingDetailId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "RatingDetailImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.core.rating.domain.RatingDetailImpl")
            }
    )
    @Column(name = "RATING_DETAIL_ID")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Double getRating() {
        return rating;
    }

    @Override
    public void setRating(Double newRating) {
        this.rating = newRating;
    }

    @Override
    public Date getRatingSubmittedDate() {
        return ratingSubmittedDate;
    }

    @Override
    public void setRatingSubmittedDate(Date ratingSubmittedDate) {
        this.ratingSubmittedDate = ratingSubmittedDate;
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
    public RatingSummary getRatingSummary() {
        return ratingSummary;
    }

    @Override
    public void setRatingSummary(RatingSummary ratingSummary) {
        this.ratingSummary = ratingSummary;
    }

}

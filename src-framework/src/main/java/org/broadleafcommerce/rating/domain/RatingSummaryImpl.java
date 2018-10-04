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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.rating.service.type.RatingType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_RATING_SUMMARY")
public class RatingSummaryImpl implements RatingSummary {

    @Id
    @GeneratedValue(generator = "RatingSummaryId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "RatingSummaryId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "RatingSummaryImpl", allocationSize = 50)
    @Column(name = "RATING_SUMMARY_ID")
    private Long id;

    @Column(name = "ITEM_ID", nullable = false)
    private String itemId;

    @Column(name = "RATING_TYPE", nullable = false)
    private String ratingType;

    @Column(name = "AVERAGE_RATING", nullable = false)
    protected Double averageRating;

    @OneToMany(mappedBy = "ratingSummary", targetEntity = RatingDetailImpl.class)
    protected List<RatingDetail> ratings;

    @OneToMany(mappedBy = "ratingSummary", targetEntity = ReviewDetailImpl.class)
    protected List<ReviewDetail> reviews;

    public RatingSummaryImpl(String itemId, RatingType ratingType) {
        super();
        this.itemId = itemId;
        this.ratingType = ratingType.getType();
        this.ratings = new ArrayList<RatingDetail>();
        this.reviews = new ArrayList<ReviewDetail>();
    }

    public Long getId() {
        return id;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public String getItemId() {
        return itemId;
    }

    public Integer getNumberOfRatings() {
        return getRatings().size();
    }

    public Integer getNumberOfReviews() {
        return getReviews().size();
    }

    public RatingType getRatingType() {
        return new RatingType(ratingType);
    }

    public List<RatingDetail> getRatings() {
        return ratings == null ? new ArrayList<RatingDetail>() : ratings;
    }

    public List<ReviewDetail> getReviews() {
        return reviews == null ? new ArrayList<ReviewDetail>() : reviews;
    }

    public void resetAverageRating() {
        double sum = 0;
        for (RatingDetail detail : ratings) {
            sum += detail.getRating();
        }

        this.averageRating = sum / ratings.size();
    }
}

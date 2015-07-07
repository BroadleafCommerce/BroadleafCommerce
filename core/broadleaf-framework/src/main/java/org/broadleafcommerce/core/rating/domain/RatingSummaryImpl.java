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

import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_RATING_SUMMARY")
public class RatingSummaryImpl implements RatingSummary {

    @Id
    @GeneratedValue(generator = "RatingSummaryId")
    @GenericGenerator(
        name="RatingSummaryId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="RatingSummaryImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.rating.domain.RatingSummaryImpl")
        }
    )
    @Column(name = "RATING_SUMMARY_ID")
    protected Long id;

    @Column(name = "ITEM_ID", nullable = false)
    @Index(name="RATINGSUMM_ITEM_INDEX", columnNames={"ITEM_ID"})
    protected String itemId;

    @Column(name = "RATING_TYPE", nullable = false)
    @Index(name="RATINGSUMM_TYPE_INDEX", columnNames={"RATING_TYPE"})
    protected String ratingTypeStr;

    @Column(name = "AVERAGE_RATING", nullable = false)
    protected Double averageRating = new Double(0);

    @OneToMany(mappedBy = "ratingSummary", targetEntity = RatingDetailImpl.class, cascade = {CascadeType.ALL})
    protected List<RatingDetail> ratings = new ArrayList<RatingDetail>();

    @OneToMany(mappedBy = "ratingSummary", targetEntity = ReviewDetailImpl.class, cascade = {CascadeType.ALL})
    protected List<ReviewDetail> reviews = new ArrayList<ReviewDetail>();

    @Override
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Double getAverageRating() {
        return averageRating;
    }
    
    @Override
    public void resetAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            this.averageRating = new Double(0);
        } else {
            double sum = 0;
            for (RatingDetail detail : ratings) {
                sum += detail.getRating();
            }

            this.averageRating = new Double(sum / ratings.size());
        }
    }

    @Override
    public String getItemId() {
        return itemId;
    }
    
    @Override
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public Integer getNumberOfRatings() {
        return getRatings().size();
    }

    @Override
    public Integer getNumberOfReviews() {
        return getReviews().size();
    }

    @Override
    public RatingType getRatingType() {
        return new RatingType(ratingTypeStr);
    }
    
    @Override
    public void setRatingType(RatingType type) {
        ratingTypeStr = (type == null) ? null : type.getType();
    }

    @Override
    public List<RatingDetail> getRatings() {
        return ratings == null ? new ArrayList<RatingDetail>() : ratings;
    }
    
    @Override
    public void setRatings(List<RatingDetail> ratings) {
        this.ratings = ratings;
    }

    @Override
    public List<ReviewDetail> getReviews() {
        return reviews == null ? new ArrayList<ReviewDetail>() : reviews;
    }
    
    @Override
    public void setReviews(List<ReviewDetail> reviews) {
        this.reviews = reviews;
    }

}

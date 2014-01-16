/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_OFFER_CODE")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationOverrides(
    {
        @AdminPresentationOverride(name="offer.appliesToOrderRules", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="offer.appliesToFulfillmentGroupRules", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="offer.appliesToCustomerRules", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="offer.maxUses", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="offer.uses", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="offer.targetItemCriteria", value=@AdminPresentation(excluded = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OfferCodeImpl_baseOfferCode")
public class OfferCodeImpl implements OfferCode {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferCodeId")
    @GenericGenerator(
        name="OfferCodeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferCodeImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferCodeImpl")
        }
    )
    @Column(name = "OFFER_CODE_ID")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer_Code_Id", group = "OfferCodeImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="OFFERCODE_OFFER_INDEX", columnNames={"OFFER_ID"})
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer", group = "OfferCodeImpl_Description")
    protected Offer offer;

    @Column(name = "OFFER_CODE", nullable=false)
    @Index(name="OFFERCODE_CODE_INDEX", columnNames={"OFFER_CODE"})
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer_Code", order=1, group = "OfferCodeImpl_Description", prominent=true)
    protected String offerCode;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_Start_Date", order=1, group = "OfferCodeImpl_Activity_Range")
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_End_Date", order=2, group = "OfferCodeImpl_Activity_Range")
    protected Date endDate;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_Max_Uses", order=2, group = "OfferCodeImpl_Description", prominent=true)
    protected int maxUses;

    @Column(name = "USES")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_Uses", visibility = VisibilityEnum.HIDDEN_ALL)
    @Deprecated
    protected int uses;
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OrderImpl.class)
    @JoinTable(name = "BLC_ORDER_OFFER_CODE_XREF", joinColumns = @JoinColumn(name = "OFFER_CODE_ID", referencedColumnName = "OFFER_CODE_ID"), inverseJoinColumns = @JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<Order> orders = new ArrayList<Order>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Offer getOffer() {
        return offer;
    }

    @Override
    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public String getOfferCode() {
        return offerCode;
    }

    @Override
    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    @Override
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
    
    @Override
    public boolean isUnlimitedUse() {
        return getMaxUses() == 0;
    }
    
    @Override
    public boolean isLimitedUse() {
        return getMaxUses() > 0;
    }

    @Override
    @Deprecated
    public int getUses() {
        return uses;
    }

    @Override
    @Deprecated
    public void setUses(int uses) {
        this.uses = uses;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(offer)
            .append(offerCode)
            .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof OfferCodeImpl) {
            OfferCodeImpl that = (OfferCodeImpl) o;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.offer, that.offer)
                .append(this.offerCode, that.offerCode)
                .build();
        }
        
        return false;
    }


}


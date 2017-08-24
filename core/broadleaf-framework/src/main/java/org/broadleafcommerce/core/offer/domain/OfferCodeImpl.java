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
package org.broadleafcommerce.core.offer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.DefaultPostLoaderDao;
import org.broadleafcommerce.common.persistence.PostLoaderDao;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "BLC_OFFER_CODE")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.FALSE, friendlyName = "OfferCodeImpl_baseOfferCode")
@SQLDelete(sql="UPDATE BLC_OFFER_CODE SET ARCHIVED = 'Y' WHERE OFFER_CODE_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
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
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer_Code_Id")
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="OFFERCODE_OFFER_INDEX", columnNames={"OFFER_ID"})
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer", order=2000,
            prominent = true, gridOrder = 2000)
    @AdminPresentationToOneLookup()
    protected Offer offer;

    @Column(name = "OFFER_CODE", nullable=false)
    @Index(name="OFFERCODE_CODE_INDEX", columnNames={"OFFER_CODE"})
    @AdminPresentation(friendlyName = "OfferCodeImpl_Offer_Code", order = 1000, prominent = true, gridOrder = 1000)
    protected String offerCode;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_Start_Date", order = 3000,
            defaultValue = "today")
    protected Date offerCodeStartDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_End_Date", order = 4000,
        validationConfigurations = {
            @ValidationConfiguration(
                validationImplementation = "blAfterStartDateValidator",
                configurationItems = {
                    @ConfigurationItem(itemName = "otherField", itemValue = "offerCodeStartDate")
                    }) 
        })
    protected Date offerCodeEndDate;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName = "OfferCodeImpl_Code_Max_Uses", order = 5000,
            tooltip = "OfferCodeImpl_Code_Max_Uses_Tooltip")
    protected Integer maxUses;

    @Column(name = "USES")
    @Deprecated
    protected int uses;
    
    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy="addedOfferCodes", targetEntity = OrderImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<Order> orders = new ArrayList<Order>();

    @Transient
    protected Offer sbClonedOffer;

    @Transient
    protected Offer deproxiedOffer;
    
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
        if (deproxiedOffer == null) {
            PostLoaderDao postLoaderDao = DefaultPostLoaderDao.getPostLoaderDao();

            if (postLoaderDao != null && offer.getId() != null) {
                Long id = offer.getId();
                deproxiedOffer = postLoaderDao.find(OfferImpl.class, id);
            } else if (offer instanceof HibernateProxy) {
                deproxiedOffer = HibernateUtils.deproxy(offer);
            } else {
                deproxiedOffer = offer;
            }
        }

        return deproxiedOffer;
    }

    @Override
    public void setOffer(Offer offer) {
        this.offer = offer;
        sbClonedOffer = deproxiedOffer = null;
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
        return maxUses == null ? 0 : maxUses;
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
        return offerCodeStartDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.offerCodeStartDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return offerCodeEndDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.offerCodeEndDate = endDate;
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
    public Character getArchived() {
       ArchiveStatus temp;
       if (archiveStatus == null) {
           temp = new ArchiveStatus();
       } else {
           temp = archiveStatus;
       }
       return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public boolean isActive() {
        boolean datesActive;
        // If the start date for this offer code has not been set, just delegate to the offer to determine if the code is
        // active rather than requiring the user to set offer code dates as well
        if (offerCodeStartDate == null) {
            datesActive = DateUtil.isActive(getOffer().getStartDate(), getOffer().getEndDate(), true);
        } else {
            datesActive = DateUtil.isActive(offerCodeStartDate, offerCodeEndDate, true);
        }
        return datesActive && 'Y' != getArchived();
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
        if (o != null && getClass().isAssignableFrom(o.getClass())) {
            OfferCodeImpl that = (OfferCodeImpl) o;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.offer, that.offer)
                .append(this.offerCode, that.offerCode)
                .build();
        }
        
        return false;
    }


    @Override
    public <G extends OfferCode> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OfferCode cloned = createResponse.getClone();
        cloned.setEndDate(offerCodeEndDate);
        cloned.setMaxUses(maxUses);
        if (offer != null) {
            cloned.setOffer(offer.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setStartDate(offerCodeStartDate);
        cloned.setArchived(getArchived());
        cloned.setOfferCode(offerCode);
        cloned.setUses(uses);
        return  createResponse;
    }
}

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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_OFFER_RULE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferRuleImpl implements OfferRule {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferRuleId")
    @GenericGenerator(
        name="OfferRuleId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferRuleImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferRuleImpl")
        }
    )
    @Column(name = "OFFER_RULE_ID")
    protected Long id;
    
    @Lob
    @Type(type = "org.hibernate.type.MaterializedClobType")
    @Column(name = "MATCH_RULE", length = Integer.MAX_VALUE - 1)
    protected String matchRule;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.OfferRule#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.OfferRule#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.OfferRule#getMatchRule()
     */
    @Override
    public String getMatchRule() {
        return matchRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.OfferRule#setMatchRule(java.lang.String)
     */
    @Override
    public void setMatchRule(String matchRule) {
        this.matchRule = matchRule;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((matchRule == null) ? 0 : matchRule.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        OfferRuleImpl other = (OfferRuleImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (matchRule == null) {
            if (other.matchRule != null)
                return false;
        } else if (!matchRule.equals(other.matchRule))
            return false;
        return true;
    }

    @Override
    public <G extends OfferRule> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OfferRule cloned = createResponse.getClone();
        cloned.setMatchRule(matchRule);
        return createResponse;
    }
}

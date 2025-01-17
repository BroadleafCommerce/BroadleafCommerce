/*-
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.Length;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * @author bpolster
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE_RULE")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX,
                skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class PageRuleImpl implements PageRule, ProfileEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageRuleId")
    @GenericGenerator(
            name = "PageRuleId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "PageRuleImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.cms.page.domain.PageRuleImpl")
            }
    )
    @Column(name = "PAGE_RULE_ID")
    protected Long id;

    @Lob
    @JdbcType(LongVarcharJdbcType.class)
    @Column(name = "MATCH_RULE", length = Length.LONG32 - 1)
    protected String matchRule;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getMatchRule() {
        return matchRule;
    }

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
        PageRuleImpl other = (PageRuleImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (matchRule == null) {
            return other.matchRule == null;
        } else
            return matchRule.equals(other.matchRule);
    }

    @Override
    public PageRule cloneEntity() {
        PageRuleImpl newField = new PageRuleImpl();
        newField.matchRule = matchRule;

        return newField;
    }

    @Override
    public <G extends PageRule> CreateResponse<G> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        PageRule cloned = createResponse.getClone();
        cloned.setMatchRule(matchRule);
        return createResponse;
    }
}

/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Profile;

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
 * @author bpolster
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE_RULE")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
@ProfileEntity
public class PageRuleImpl implements PageRule {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "PageRuleId")
    @GenericGenerator(
        name="PageRuleId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PageRuleImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.page.domain.PageRuleImpl")
        }
    )
    @Column(name = "PAGE_RULE_ID")
    protected Long id;
    
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "MATCH_RULE", length = Integer.MAX_VALUE - 1)
    protected String matchRule;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.StructuredContentRule#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.StructuredContentRule#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.StructuredContentRule#getMatchRule()
     */
    @Override
    public String getMatchRule() {
        return matchRule;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.offer.domain.StructuredContentRule#setMatchRule(java.lang.String)
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
        PageRuleImpl other = (PageRuleImpl) obj;
        
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
    public PageRule cloneEntity() {
        PageRuleImpl newField = new PageRuleImpl();
        newField.matchRule = matchRule;

        return newField;
    }

    @Override
    public <G extends PageRule> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        PageRule cloned = createResponse.getClone();
        cloned.setMatchRule(matchRule);
        return createResponse;
    }
}

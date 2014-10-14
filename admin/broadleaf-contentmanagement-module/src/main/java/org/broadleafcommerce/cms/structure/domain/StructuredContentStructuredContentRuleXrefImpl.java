/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC_RULE_MAP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blCMSElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class StructuredContentStructuredContentRuleXrefImpl implements StructuredContentStructuredContentRuleXref, StructuredContentRule {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentStructuredContentRuleXrefId")
    @GenericGenerator(
            name = "StructuredContentStructuredContentRuleXrefId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "StructuredContentStructuredContentRuleXrefImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.cms.structure.domain.StructuredContentStructuredContentRuleXrefImpl")
            })
    @Column(name = "BLC_SC_SC_RULE_ID")
    protected Long id;

    @ManyToOne(targetEntity = StructuredContentImpl.class, optional = false)
    @JoinColumn(name = "SC_ID")
    @AdminPresentation(excluded = true)
    protected StructuredContent structuredContent;

    @ManyToOne(targetEntity = StructuredContentRuleImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "SC_RULE_ID")
    @ClonePolicy
    protected StructuredContentRule structuredContentRule;

    @Column(name = "MAP_KEY", nullable = false)
    @Index(name = "SCRULE_KEY_INDEX", columnNames = { "MAP_KEY" })
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String key;

    public StructuredContentStructuredContentRuleXrefImpl() {
        //Default constructor for JPA...
    }

    public StructuredContentStructuredContentRuleXrefImpl(StructuredContent sc, StructuredContentRule scRule, String key) {
        this.structuredContent = sc;
        this.structuredContentRule = scRule;
        this.key = key;
    }

    @Override
    public String getMatchRule() {
        createEntityInstance();
        return structuredContentRule.getMatchRule();
    }

    @Override
    public void setMatchRule(String matchRule) {
        createEntityInstance();
        this.structuredContentRule.setMatchRule(matchRule);
    }

    @Override
    public StructuredContentRule cloneEntity() {
        createEntityInstance();
        return structuredContentRule.cloneEntity();
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setStructuredContent(StructuredContent sc) {
        this.structuredContent = sc;
    }

    @Override
    public StructuredContent getStructuredContent() {
        return structuredContent;
    }

    @Override
    public void setStructuredContentRule(StructuredContentRule scRule) {
        this.structuredContentRule = scRule;
    }

    @Override
    public StructuredContentRule getStructuredContentRule() {
        return structuredContentRule;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    protected void createEntityInstance() {
        if (structuredContentRule == null) {
            structuredContentRule = new StructuredContentRuleImpl();
        }
    }

}

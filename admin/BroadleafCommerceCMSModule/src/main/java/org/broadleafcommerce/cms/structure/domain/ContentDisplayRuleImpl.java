/*
 * Copyright 2008-2011 the original author or authors.
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.broadleafcommerce.openadmin.audit.Auditable;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CONTENT_DISPLAY_RULE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@EntityListeners(value = { AdminAuditableListener.class })
public class ContentDisplayRuleImpl implements ContentDisplayRule {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ContentDisplayRuleId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ContentDisplayRuleId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ContentDisplayRuleImpl", allocationSize = 10)
    @Column(name = "CONTENT_DISPLAY_RULE_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column (name = "PRIORITY")
    protected Integer priority;

    @Column (name = "RULE_DESCRIPTION")
    protected String ruleDesciption;

    @Column (name = "MVEL_RULE")
    @Lob
    protected String mvelRule;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String getRuleDesciption() {
        return ruleDesciption;
    }

    @Override
    public void setRuleDesciption(String ruleDesciption) {
        this.ruleDesciption = ruleDesciption;
    }

    @Override
    public String getMvelRule() {
        return mvelRule;
    }

    @Override
    public void setMvelRule(String mvelRule) {
        this.mvelRule = mvelRule;
    }

    public AdminAuditable getAuditable() {
        return auditable;
    }

    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }
}


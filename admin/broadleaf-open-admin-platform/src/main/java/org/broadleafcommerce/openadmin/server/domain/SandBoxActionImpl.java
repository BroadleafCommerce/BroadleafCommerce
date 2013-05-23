/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX_ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
@EntityListeners(value = { AdminAuditableListener.class })
public class SandBoxActionImpl implements SandBoxAction {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxActionId")
    @GenericGenerator(
        name="SandBoxActionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SandBoxActionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.openadmin.server.domain.SandBoxActionImpl")
        }
    )
    @Column(name = "SANDBOX_ACTION_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @Column(name = "ACTION_TYPE")
    protected String sandBoxActionType;

    @Column(name = "ACTION_COMMENT")
    protected String comment;

    @ManyToMany(targetEntity = SandBoxItemImpl.class, cascade = CascadeType.ALL)
    @JoinTable(
        name = "SANDBOX_ITEM_ACTION",
        joinColumns = {@JoinColumn(name = "SANDBOX_ACTION_ID", referencedColumnName = "SANDBOX_ACTION_ID")},
        inverseJoinColumns = {@JoinColumn(name ="SANDBOX_ITEM_ID", referencedColumnName = "SANDBOX_ITEM_ID")}
    )
    protected List<SandBoxItem> sandBoxItems;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public SandBoxActionType getActionType() {
        return SandBoxActionType.getInstance(sandBoxActionType);
    }

    @Override
    public void setActionType(SandBoxActionType type) {
        sandBoxActionType = type.getType();
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public List<SandBoxItem> getSandBoxItems() {
        return sandBoxItems;
    }

    @Override
    public void setSandBoxItems(List<SandBoxItem> sandBoxItems) {
        this.sandBoxItems = sandBoxItems;
    }

    @Override
    public void addSandBoxItem(SandBoxItem item) {
        if (sandBoxItems == null) {
            sandBoxItems = new ArrayList<SandBoxItem>();
        }
        sandBoxItems.add(item);
    }

    @Override
    public AdminAuditable getAuditable() {
        return auditable;
    }

    @Override
    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }
}

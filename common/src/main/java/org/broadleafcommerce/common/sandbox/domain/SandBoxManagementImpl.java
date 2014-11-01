/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sandbox.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * This class is required mostly as a workaround for an issue in Hibernate. It's obscure, but I'll try to explain.
 * SandBox ids are used as discriminators in workflow. SandBoxes themselves are also able to be managed in the
 * admin (add new sandbox, etc...) Site ids are used as discriminators in multitenant. When workflow and multitenant
 * are used together, both discriminators are in effect. Because sandboxes can be managed in the admin, it is required
 * that they have a site discriminator to be managed in the multitenant admin. This intermingling of references
 * ends up causing this exception at runtime during, for example, a product save:
 *
 * HibernateException: Found two representations of same collection
 *
 * To workaround, we use this management entity that exposes the properties seamlessly of SandBox to the admin, but
 * holds the site discriminator on its own table (rather than BLC_SANDBOX), which fixes the issue.
 *
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX_MGMT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class SandBoxManagementImpl implements AdminMainEntity, SandBoxManagement {

    private static final Log LOG = LogFactory.getLog(SandBoxManagementImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxMgmtId")
    @GenericGenerator(
        name="SandBoxMgmtId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SandBoxManagementImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.sandbox.domain.SandBoxManagementImpl")
        }
    )
    @Column(name = "SANDBOX_MGMT_ID")
    protected Long id;

    @OneToOne(targetEntity = SandBoxImpl.class, cascade={CascadeType.ALL}, optional = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "SANDBOX_ID")
    protected SandBox sandBox;

    @Override
    public String getMainEntityName() {
        return sandBox.getName();
    }

    @Override
    public SandBox getSandBox() {
        return sandBox;
    }

    @Override
    public void setSandBox(SandBox sandBox) {
        this.sandBox = sandBox;
    }
}

/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.enumeration.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
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

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_DATA_DRVN_ENUM_VAL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "DataDrivenEnumerationValueImpl_friendyName")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class DataDrivenEnumerationValueImpl implements DataDrivenEnumerationValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataDrivenEnumerationValueId")
    @GenericGenerator(
        name="DataDrivenEnumerationValueId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="DataDrivenEnumerationValueImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValueImpl")
        }
    )
    @Column(name = "ENUM_VAL_ID")
    protected Long id;

    @ManyToOne(targetEntity = DataDrivenEnumerationImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ENUM_TYPE")
    protected DataDrivenEnumeration type;

    @Column(name = "ENUM_KEY")
    @Index(name = "ENUM_VAL_KEY_INDEX", columnNames = {"ENUM_KEY"})
    @AdminPresentation(friendlyName = "DataDrivenEnumerationValueImpl_Key", order = 1, gridOrder = 1, prominent = true)
    protected String key;

    @Column(name = "DISPLAY")
    @AdminPresentation(friendlyName = "DataDrivenEnumerationValueImpl_Display", order = 2, gridOrder = 2, prominent = true)
    protected String display;

    @Column(name = "HIDDEN")
    @Index(name = "HIDDEN_INDEX", columnNames = {"HIDDEN"})
    @AdminPresentation(friendlyName = "DataDrivenEnumerationValueImpl_Hidden", order = 3, gridOrder = 3, prominent = true)
    protected Boolean hidden = false;

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public Boolean getHidden() {
        if (hidden == null) {
            return Boolean.FALSE;
        } else {
            return hidden;
        }
    }

    @Override
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public DataDrivenEnumeration getType() {
        return type;
    }

    @Override
    public void setType(DataDrivenEnumeration type) {
        this.type = type;
    }

    @Override
    public <G extends DataDrivenEnumerationValue> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        DataDrivenEnumerationValue cloned = createResponse.getClone();
        cloned.setKey(key);
        cloned.setDisplay(display);
        cloned.setHidden(hidden);
        if (type != null) {
            cloned.setType(type.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}

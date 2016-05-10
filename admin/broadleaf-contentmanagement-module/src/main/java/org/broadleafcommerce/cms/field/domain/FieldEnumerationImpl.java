/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Created by jfischer
 * @deprecated use {@link DataDrivenEnumerationImpl} instead
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FLD_ENUM")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@Deprecated
public class FieldEnumerationImpl implements FieldEnumeration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FieldEnumerationId")
    @GenericGenerator(
        name="FieldEnumerationId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FieldEnumerationImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.field.domain.FieldEnumerationImpl")
        }
    )
    @Column(name = "FLD_ENUM_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @OneToMany(mappedBy = "fieldEnumeration", targetEntity = FieldEnumerationItemImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderBy("fieldOrder")
    @BatchSize(size = 20)
    protected List<FieldEnumerationItem> enumerationItems;

    @Override
    public List<FieldEnumerationItem> getEnumerationItems() {
        return enumerationItems;
    }

    @Override
    public void setEnumerationItems(List<FieldEnumerationItem> enumerationItems) {
        this.enumerationItems = enumerationItems;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

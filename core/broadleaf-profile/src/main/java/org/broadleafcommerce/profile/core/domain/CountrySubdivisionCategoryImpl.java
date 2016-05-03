/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_COUNTRY_SUB_CAT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "CountrySubdivisionCategoryImpl_baseCategory")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
public class CountrySubdivisionCategoryImpl implements CountrySubdivisionCategory, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CountrySubdivisionCategoryId")
    @GenericGenerator(
            name="CountrySubdivisionCategoryId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name="segment_value", value="CountrySubdivisionCategoryImpl"),
                    @Parameter(name="entity_name", value="org.broadleafcommerce.profile.core.domain.CountrySubdivisionCategoryImpl")
            }
    )
    @Column(name = "COUNTRY_SUB_CAT_ID")
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @AdminPresentation(friendlyName = "CountrySubdivisionCategoryImpl_Name", order=1, prominent = true, translatable = true)
    protected String name;

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
        return DynamicTranslationProvider.getValue(this, "name", name);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
}

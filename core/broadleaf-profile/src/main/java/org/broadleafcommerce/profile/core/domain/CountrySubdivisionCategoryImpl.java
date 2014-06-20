/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
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

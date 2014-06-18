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
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_COUNTRY_SUB")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "CountrySubdivisionImpl_baseSubdivision")
public class CountrySubdivisionImpl implements CountrySubdivision, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ABBREVIATION")
    protected String abbreviation;

    @Column(name = "NAME", nullable = false)
    @Index(name="COUNTRY_SUB_NAME_IDX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "CountrySubdivisionImpl_Name", order=9, group = "CountrySubdivisionImpl_Address", prominent = true, translatable = true)
    protected String name;

    @Column(name = "ALT_ABBREVIATION")
    @Index(name="COUNTRY_SUB_ALT_ABRV_IDX", columnNames={"ALT_ABBREVIATION"})
    @AdminPresentation(friendlyName = "CountrySubdivisionImpl_AltAbbreviation", order=10, group = "CountrySubdivisionImpl_Address", prominent = true)
    protected String alternateAbbreviation;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CountryImpl.class, optional = false)
    @JoinColumn(name = "COUNTRY")
    protected Country country;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CountrySubdivisionCategoryImpl.class)
    @JoinColumn(name = "COUNTRY_SUB_CAT")
    protected CountrySubdivisionCategory category;

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String getAlternateAbbreviation() {
        return alternateAbbreviation;
    }

    @Override
    public void setAlternateAbbreviation(String alternateAbbreviation) {
        this.alternateAbbreviation = alternateAbbreviation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Country getCountry() {
        return country;
    }

    @Override
    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public CountrySubdivisionCategory getCategory() {
        return category;
    }

    @Override
    public void setCategory(CountrySubdivisionCategory category) {
        this.category = category;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }
}

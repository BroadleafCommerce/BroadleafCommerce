/*
 * Copyright 2012 the original author or authors.
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

/**
 * 
 */
package org.broadleafcommerce.core.order.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.util.LocaleUtil;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.ProductOptionTranslation;
import org.broadleafcommerce.core.catalog.domain.ProductOptionTranslationImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueTranslation;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Base Fulfillment Option")
public class FulfillmentOptionImpl implements FulfillmentOption {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "FulfillmentOptionId")
    @GenericGenerator(
        name="FulfillmentOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentOptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl")
        }
    )
    @Column(name = "FULFILLMENT_OPTION_ID")
    protected Long id;
    
    @Column(name = "NAME")
    protected String name;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION")
    protected String longDescription;

    @Column(name = "USE_FLAT_RATES")
    protected Boolean useFlatRates = true;

    @Column(name = "FULFILLMENT_TYPE", nullable = false)
    @AdminPresentation(friendlyName = "Fulfillment Type", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.FulfillmentType")
    protected String fulfillmentType = FulfillmentType.PHYSICAL.getType();

    @ManyToMany(targetEntity = FulfillmentOptionTranslationImpl.class)
    @JoinTable(name = "BLC_FULFILLMENT_OPTION_TRANSLATION_XREF",
            joinColumns = @JoinColumn(name = "FULFILLMENT_OPTION_ID", referencedColumnName = "FULFILLMENT_OPTION_ID"),
            inverseJoinColumns = @JoinColumn(name = "TRANSLATION_ID", referencedColumnName = "TRANSLATION_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "MAP_KEY", nullable = false) })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 10)
    @AdminPresentationMap(
            friendlyName = "FulfillmentOptionImpl_Translations",
            dataSourceName = "productOptionTranslationDS",
            keyPropertyFriendlyName = "TranslationsImpl_Key",
            deleteEntityUponRemove = true,
            mapKeyOptionEntityClass = FulfillmentOptionTranslationImpl.class,
            mapKeyOptionEntityDisplayField = "friendlyName",
            mapKeyOptionEntityValueField = "translationsKey"

    )
    protected Map<String, FulfillmentOptionTranslation> translations = new HashMap<String,FulfillmentOptionTranslation>();

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
        if (translations != null && BroadleafRequestContext.hasLocale())  {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();

            // Search for translation based on locale
            String localeCode = locale.getLocaleCode();
            if (localeCode != null) {
                FulfillmentOptionTranslation translation = translations.get(localeCode);
                if (translation != null && translation.getName() != null) {
                    return translation.getName();
                }
            }

            // try just the language
            String languageCode = LocaleUtil.findLanguageCode(locale);
            if (languageCode != null && ! localeCode.equals(languageCode)) {
                FulfillmentOptionTranslation translation = translations.get(languageCode);
                if (translation != null && translation.getName() != null) {
                    return translation.getName();
                }
            }
        }
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLongDescription() {
        if (translations != null && BroadleafRequestContext.hasLocale())  {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();

            // Search for translation based on locale
            String localeCode = locale.getLocaleCode();
            if (localeCode != null) {
                FulfillmentOptionTranslation translation = translations.get(localeCode);
                if (translation != null && translation.getLongDescription() != null) {
                    return translation.getLongDescription();
                }
            }

            // try just the language
            String languageCode = LocaleUtil.findLanguageCode(locale);
            if (languageCode != null && ! localeCode.equals(languageCode)) {
                FulfillmentOptionTranslation translation = translations.get(languageCode);
                if (translation != null && translation.getLongDescription() != null) {
                    return translation.getLongDescription();
                }
            }
        }
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Boolean getUseFlatRates() {
        return useFlatRates;
    }

    @Override
    public void setUseFlatRates(Boolean useFlatRates) {
        this.useFlatRates = useFlatRates;
    }

    @Override
    public FulfillmentType getFulfillmentType() {
        return FulfillmentType.getInstance(fulfillmentType);
    }

    @Override
    public void setFulfillmentType(FulfillmentType fulfillmentType) {
        this.fulfillmentType = (fulfillmentType == null) ? null : fulfillmentType.getType();
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

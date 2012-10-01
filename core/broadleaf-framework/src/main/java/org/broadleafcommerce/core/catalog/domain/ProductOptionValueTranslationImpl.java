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

package org.broadleafcommerce.core.catalog.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

/**
 * Author: jerryocanas
 * Date: 9/19/12
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_VALUE_TRANSLATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductOptionTranslationImpl_friendyName")
public class ProductOptionValueTranslationImpl implements java.io.Serializable, ProductOptionValueTranslation {

    private static final long serialVersionUID = 1L;

    @Transient
    private static final Log LOG = LogFactory.getLog(ProductOptionValueTranslationImpl.class);

    @Id
    @GeneratedValue(generator = "ProductOptionTranslationID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ProductOptionTranslationID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ProductOptionValueTranslationImpl_AttributeValue", allocationSize = 50)
    @Column(name = "TRANSLATION_ID")
    @AdminPresentation(friendlyName = "ProductOptionTranslationImpl_ID", order = 1, group = "ProductOptionValueTranslationImpl_AttributeValue", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "ATTRIBUTE_VALUE", nullable = false)
    @AdminPresentation(friendlyName = "Attribute_Value", order = 3, group = "ProductOptionValueTranslationImpl_AttributeValue", prominent = true, groupOrder = 1)
    protected String attributeValue;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = true)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "ProductOptionTranslationImpl_locale", order = 3, group = "ProductOptionValueTranslationImpl_AttributeValue", prominent = true, groupOrder = 1)
    protected Locale locale;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    @Override
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

}

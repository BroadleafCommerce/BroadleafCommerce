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

package org.broadleafcommerce.core.order.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.domain.LocaleIf;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;

/**
 * Author: jerryocanas
 * Date: 9/19/12
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION_TRANSLATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "FulfillmentOptionTranslationImpl_friendyName")
public class FulfillmentOptionTranslationImpl implements java.io.Serializable, FulfillmentOptionTranslation, LocaleIf {

    private static final long serialVersionUID = 1L;

    @Transient
    private static final Log LOG = LogFactory.getLog(FulfillmentOptionTranslationImpl.class);

    @Id
    @GeneratedValue(generator = "FulfillmentOptionTranslationID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentOptionTranslationID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentOptionTranslationImpl_LongDescription", allocationSize = 50)
    @Column(name = "TRANSLATION_ID")
    @AdminPresentation(friendlyName = "FulfillmentOptionTranslationImpl_ID", order = 1, group = "FulfillmentOptionTranslationImpl_LongDescription", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable = false)
    @AdminPresentation(friendlyName = "FulfillmentOptionTranslationImpl_name", order = 2, group = "FulfillmentOptionTranslationImpl_LongDescription", prominent = true, groupOrder = 1)
    protected String name;

    @Column(name = "LONG_DESCRIPTION", nullable = false)
    @AdminPresentation(friendlyName = "FulfillmentOptionImpl_LongDescription", order = 3, group = "FulfillmentOptionTranslationImpl_LongDescription", prominent = true, groupOrder = 1)
    protected String longDescription;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "FulfillmentOptionTranslationImpl_locale", order = 4, group = "FulfillmentOptionTranslationImpl_LongDescription", prominent = true, groupOrder = 1)
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
    public String getLongDescription() {
        return longDescription;
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
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}

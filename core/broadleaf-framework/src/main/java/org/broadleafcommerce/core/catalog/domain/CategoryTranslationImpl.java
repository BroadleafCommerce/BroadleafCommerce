/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 * @author priyeshpatel
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_TRANSLATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "SearchRedirectImpl_friendyName")
public class CategoryTranslationImpl implements java.io.Serializable,
        CategoryTranslation, LocaleIf {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final Log LOG = LogFactory
            .getLog(CategoryTranslationImpl.class);
    @Id
    @GeneratedValue(generator = "CategoryTranslationID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CategoryTranslationID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CategoryTranslationID", allocationSize = 50)
    @Column(name = "TRANSLATION_ID")
    @AdminPresentation(friendlyName = "CategoryTranslationImpl_ID", order = 1, group = "CategoryTranslationImpl_description", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable = false)
    @AdminPresentation(friendlyName = "CategoryImpl_Category_name", order = 3, group = "CategoryTranslationImpl_description", prominent = true, groupOrder = 1)
    protected String name;

    @Column(name = "DESCRIPTION", nullable = false)
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Description", order = 3, group = "CategoryTranslationImpl_description", prominent = true, groupOrder = 1)
    protected String description;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "CategoryImpl_Category_Long_Description", order=6, group = "CategoryTranslationImpl_description", largeEntry=true,fieldType=SupportedFieldType.HTML_BASIC)
    protected String longDescription;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "CategoryTranslationImpl_locale", order = 3, group = "CategoryTranslationImpl_description", prominent = true, groupOrder = 1)
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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
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
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}

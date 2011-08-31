/*
 * Copyright 2008-2011 the original author or authors.
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
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.field.domain.FieldData;
import org.broadleafcommerce.cms.page.domain.Locale;
import org.broadleafcommerce.cms.page.domain.LocaleImpl;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFieldImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET_DESCRIPTION")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StaticAssetDescriptionImpl implements StaticAssetDescription {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StaticAssetDescriptionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StaticAssetDescriptionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StaticAssetDescriptionId", allocationSize = 10)
    @Column(name = "STATIC_ASSET_DESC_ID")
    protected Long id;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_ID")
    protected Locale locale;

    @Column (name = "DESCRIPTION")
    protected String description;

    @Column (name = "LONG_DESCRIPTION")
    protected String longDescription;

    @ManyToOne (targetEntity = StaticAssetImpl.class)
    @JoinColumn (name = "STATIC_ASSET_ID")
    protected StaticAsset staticAsset;

    @Column (name = "FIELD_KEY")
    protected String fieldKey;

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
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public StaticAsset getStaticAsset() {
        return staticAsset;
    }

    @Override
    public void setStaticAsset(StaticAsset staticAsset) {
        this.staticAsset = staticAsset;
    }

    @Override
    public String getFieldKey() {
        return fieldKey;
    }

    @Override
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
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
    public StaticAssetDescription cloneEntity() {
        StaticAssetDescriptionImpl newAssetDescription = new StaticAssetDescriptionImpl();
        newAssetDescription.locale = locale;
        newAssetDescription.description = description;
        newAssetDescription.longDescription = longDescription;
        newAssetDescription.staticAsset = staticAsset;

        return newAssetDescription;
    }
}


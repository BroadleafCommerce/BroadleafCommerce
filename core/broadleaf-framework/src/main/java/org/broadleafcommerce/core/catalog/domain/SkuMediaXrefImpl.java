/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.service.UnknownUnwrapTypeException;

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

@Entity
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_MEDIA_MAP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@AdminPresentationClass(excludeFromPolymorphism = false, populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class SkuMediaXrefImpl implements SkuMediaXref, Media {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    public SkuMediaXrefImpl(Sku sku, Media media, String key) {
        this.sku = sku;
        this.media = media;
        this.key = key;
    }

    public SkuMediaXrefImpl() {
        //support default constructor for Hibernate
    }

    @Id
    @GeneratedValue(generator= "SkuMediaId")
    @GenericGenerator(
        name="SkuMediaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SkuMediaXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.SkuMediaXrefImpl")
        }
    )
    @Column(name = "SKU_MEDIA_ID")
    protected Long id;

    //for the basic collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = SkuImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "BLC_SKU_SKU_ID")
    @AdminPresentation(excluded = true)
    protected Sku sku;

    //for the basic collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = MediaImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "MEDIA_ID")
    @ClonePolicy
    protected Media media;

    @Column(name = "MAP_KEY", nullable=false)
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String key;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Sku getSku() {
        return sku;
    }

    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    @Override
    public Media getMedia() {
        return media;
    }

    @Override
    public void setMedia(Media media) {
        this.media = media;
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
    public String getUrl() {
        createEntityInstance();
        return media.getUrl();
    }

    @Override
    public void setUrl(String url) {
        createEntityInstance();
        media.setUrl(url);
    }

    @Override
    public String getTitle() {
        createEntityInstance();
        return media.getTitle();
    }

    @Override
    public void setTitle(String title) {
        createEntityInstance();
        media.setTitle(title);
    }

    @Override
    public String getAltText() {
        createEntityInstance();
        return media.getAltText();
    }

    @Override
    public void setAltText(String altText) {
        createEntityInstance();
        media.setAltText(altText);
    }

    @Override
    public String getTags() {
        createEntityInstance();
        return media.getTags();
    }

    @Override
    public void setTags(String tags) {
        createEntityInstance();
        media.setTags(tags);
    }

    protected void createEntityInstance() {
        if (media == null) {
            media = new MediaImpl();
        }
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return Media.class.equals(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) media;
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }
}

/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
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
import org.broadleafcommerce.common.util.UnknownUnwrapTypeException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_MEDIA_MAP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCategories")
@AdminPresentationClass(excludeFromPolymorphism = false, populateToOneFields = PopulateToOneFieldsEnum.TRUE)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class CategoryMediaXrefImpl implements CategoryMediaXref, Media, MultiTenantCloneable<CategoryMediaXrefImpl> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    public CategoryMediaXrefImpl(Category category, Media media, String key) {
        this.category = category;
        this.media = media;
        this.key = key;
    }

    public CategoryMediaXrefImpl() {
        //support default constructor for Hibernate
    }

    @Id
    @GeneratedValue(generator= "CategoryMediaId")
    @GenericGenerator(
        name="CategoryMediaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="CategoryMediaXrefImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.CategoryMediaXrefImpl")
        }
    )
    @Column(name = "CATEGORY_MEDIA_ID")
    protected Long id;

    //for the basic collection join entity - don't pre-instantiate the reference (i.e. don't do myField = new MyFieldImpl())
    @ManyToOne(targetEntity = CategoryImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "BLC_CATEGORY_CATEGORY_ID")
    @AdminPresentation(excluded = true)
    protected Category category;

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
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CategoryMediaXrefImpl that = (CategoryMediaXrefImpl) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (media != null ? !media.equals(that.media) : that.media != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (media != null ? media.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public <G extends CategoryMediaXrefImpl> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CategoryMediaXrefImpl cloned = createResponse.getClone();
        if (media != null) {
            cloned.setMedia(((MediaImpl) media).createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setAltText(getAltText());
        cloned.setKey(key);
        if (category != null) {
            cloned.setCategory(category.createOrRetrieveCopyInstance(context).getClone());
        }
        cloned.setTags(getTags());
        cloned.setUrl(getUrl());
        cloned.setTitle(getTitle());
        return createResponse;
    }
}

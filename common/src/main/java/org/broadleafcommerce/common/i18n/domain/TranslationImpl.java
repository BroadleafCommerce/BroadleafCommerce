/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.Length;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_TRANSLATION", indexes = {@Index(name = "TRANSLATION_INDEX", columnList = "ENTITY_TYPE, ENTITY_ID, FIELD_NAME, LOCALE_CODE")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blTranslationElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "TranslationImpl_baseTranslation")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class TranslationImpl implements Serializable, Translation {

    @Serial
    private static final long serialVersionUID = -122818474469147685L;

    @Id
    @GeneratedValue(generator = "TranslationId")
    @GenericGenerator(
            name = "TranslationId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "TranslationImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.common.i18n.domain.TranslationImpl")
            })
    @Column(name = "TRANSLATION_ID")
    protected Long id;

    @Column(name = "ENTITY_TYPE")
    @AdminPresentation(friendlyName = "TranslationImpl_EntityType", prominent = true)
    protected String entityType;

    @Column(name = "ENTITY_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected String entityId;

    @Column(name = "FIELD_NAME")
    @AdminPresentation(friendlyName = "TranslationImpl_FieldName", prominent = true)
    protected String fieldName;

    @Column(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "TranslationImpl_LocaleCode", prominent = true)
    protected String localeCode;

    @Lob
    @JdbcType(LongVarcharJdbcType.class)
    @Column(name = "TRANSLATED_VALUE", length = Length.LONG32 - 1)
    @AdminPresentation(friendlyName = "TranslationImpl_TranslatedValue", prominent = true, requiredOverride = RequiredOverride.REQUIRED)
    protected String translatedValue;

    /* ************************ */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************ */

    @Override
    public TranslatedEntity getEntityType() {
        return TranslatedEntity.getInstanceFromFriendlyType(entityType);
    }

    @Override
    public void setEntityType(TranslatedEntity entityType) {
        this.entityType = entityType.getFriendlyType();
    }

    /* ************************** */
    /* STANDARD GETTERS / SETTERS */
    /* ************************** */

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getLocaleCode() {
        return localeCode;
    }

    @Override
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    @Override
    public String getTranslatedValue() {
        return translatedValue;
    }

    @Override
    public void setTranslatedValue(String translatedValue) {
        this.translatedValue = translatedValue;
    }

    @Override
    public <G extends Translation> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Translation cloned = createResponse.getClone();

        //this assumes that TranslationImpl copying occurs last after all other entity copying
        Object referenceClone;
        try {
            referenceClone = context.getPreviousClone(Class.forName(getEntityType().getType()), Long.parseLong(entityId));
        } catch (ClassNotFoundException e) {
            throw ExceptionHelper.refineException(e);
        }
        String convertedId = entityId;
        if (referenceClone != null) {
            convertedId = String.valueOf(context.getIdentifier(referenceClone));
        }
        cloned.setEntityId(convertedId);
        cloned.setFieldName(fieldName);
        cloned.setLocaleCode(localeCode);
        cloned.setTranslatedValue(translatedValue);
        cloned.setEntityType(getEntityType());
        return createResponse;
    }

}

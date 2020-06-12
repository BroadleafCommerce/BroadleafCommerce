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

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicyCollectionOverride;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class ProductOptionImpl implements ProductOption, AdminMainEntity, ProductOptionAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionId")
    @GenericGenerator(
        name="ProductOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_ID")
    protected Long id;

    @Column(name = "NAME")
    @Index(name="PRODUCT_OPTION_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "productOption_name",
        group = GroupName.General, order = FieldOrder.name,
        prominent = true, gridOrder = 1000)
    protected String name;

    @Column(name = "OPTION_TYPE")
    @AdminPresentation(friendlyName = "productOption_Type",
        group = GroupName.General, order = FieldOrder.type,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionType",
        prominent = true, gridOrder = 2000)
    protected String type;
    
    @Column(name = "ATTRIBUTE_NAME")
    @AdminPresentation(friendlyName = "productOption_attributeName",
        group = GroupName.Details, order = FieldOrder.attributeName,
        tooltip = "productOption_attributeNameTip",
        requiredOverride = RequiredOverride.REQUIRED)
    protected String attributeName;
    
    @Column(name = "LABEL")
    @AdminPresentation(friendlyName = "productOption_Label",
        group = GroupName.General, order = FieldOrder.label,
        tooltip = "productOption_labelTip",
        translatable = true)
    protected String label;

    @Column(name = "REQUIRED")
    @AdminPresentation(friendlyName = "productOption_Required",
        group = GroupName.Validation, order = FieldOrder.required,
        prominent = true, gridOrder = 3000)
    protected Boolean required;

    @Column(name = "USE_IN_SKU_GENERATION")
    @AdminPresentation(friendlyName = "productOption_UseInSKUGeneration",
        group = GroupName.Details, order = FieldOrder.useInSkuGeneration,
        tooltip = "productOption_useInSkuGenerationTip",
        defaultValue = "false")
    private Boolean useInSkuGeneration = Boolean.FALSE;

    @Column(name = "DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "productOption_displayOrder",
        group = GroupName.Details, order = FieldOrder.displayOrder,
        tooltip = "productOption_displayOrderTip")
    protected Integer displayOrder;

    @Column(name = "VALIDATION_STRATEGY_TYPE")
    @AdminPresentation(friendlyName = "productOption_validationStrategyType",
        group = GroupName.Validation, order = FieldOrder.validationStrategyType,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType",
        defaultValue = "NONE")
    private String productOptionValidationStrategyType;

    @Column(name = "VALIDATION_TYPE")
    @AdminPresentation(friendlyName = "productOption_validationType",
        group = GroupName.Validation, order = FieldOrder.validationType,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType",
        defaultValue = "REGEX",
        visibility = VisibilityEnum.HIDDEN_ALL)
    private String productOptionValidationType;

    @Column(name = "VALIDATION_STRING")
    @AdminPresentation(friendlyName = "productOption_validationSring",
        group = GroupName.Validation, order = FieldOrder.validationString)
    protected String validationString;

    @Column(name = "ERROR_CODE")
    @AdminPresentation(friendlyName = "productOption_errorCode",
        group = GroupName.Validation, order = FieldOrder.errorCode)
    protected String errorCode;

    @Column(name = "ERROR_MESSAGE")
    @AdminPresentation(friendlyName = "productOption_errorMessage",
        group = GroupName.Validation, order = FieldOrder.errorMessage,
        translatable = true)
    protected String errorMessage;

    @OneToMany(mappedBy = "productOption", targetEntity = ProductOptionValueImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @OrderBy(value = "displayOrder")
    @AdminPresentationCollection(friendlyName = "ProductOptionImpl_Allowed_Values",
        group = GroupName.General,
        addType = AddMethodType.PERSIST)
    protected List<ProductOptionValue> allowedValues = new ArrayList<>();

    @OneToMany(targetEntity = ProductOptionXrefImpl.class, mappedBy = "productOption")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    @ClonePolicyCollectionOverride
    protected List<ProductOptionXref> products = new ArrayList<>();
    
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
        return name == null || name.isEmpty() ? getLabel() : name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ProductOptionType getType() {
        return ProductOptionType.getInstance(type);
    }

    @Override
    public void setType(ProductOptionType type) {
        this.type = type == null ? null : type.getType();
    }
    
    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    public String getLabel() {
        return DynamicTranslationProvider.getValue(this, "label", label);
    }
    
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Boolean getRequired() {
        return required;
    }

    @Override
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public List<ProductOptionXref> getProductXrefs() {
        return products;
    }

    @Override
    public void setProductXrefs(List<ProductOptionXref> xrefs) {
        this.products = xrefs;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> response = new ArrayList<>();
        for (ProductOptionXref xref : products) {
            response.add(xref.getProduct());
        }
        return Collections.unmodifiableList(response);
    }

    @Override
    public void setProducts(List<Product> products){
        throw new UnsupportedOperationException("Use setProductOptionXrefs(..) instead");
    }

    @Override
    public List<ProductOptionValue> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public void setAllowedValues(List<ProductOptionValue> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public Boolean getUseInSkuGeneration() {
        return (useInSkuGeneration == null) ? true : useInSkuGeneration;
    }

    @Override
    public void setUseInSkuGeneration(Boolean useInSkuGeneration) {
        this.useInSkuGeneration = useInSkuGeneration;
    }

    @Override
    public ProductOptionValidationStrategyType getProductOptionValidationStrategyType() {
        return ProductOptionValidationStrategyType.getInstance(productOptionValidationStrategyType);
    }

    @Override
    public void setProductOptionValidationStrategyType(ProductOptionValidationStrategyType productOptionValidationStrategyType) {
        this.productOptionValidationStrategyType = productOptionValidationStrategyType == null ? null : productOptionValidationStrategyType.getType();
    }

    @Override
    public ProductOptionValidationType getProductOptionValidationType() {
        if (productOptionValidationType == null || productOptionValidationType.isEmpty()) {
            return ProductOptionValidationType.REGEX;
        }

        return ProductOptionValidationType.getInstance(productOptionValidationType);
    }

    @Override
    public void setProductOptionValidationType(ProductOptionValidationType productOptionValidationType) {
        this.productOptionValidationType = productOptionValidationType == null ? null : productOptionValidationType.getType();
    }

    @Override
    public String getValidationString() {
        return validationString;
    }

    @Override
    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorMessage() {
        return DynamicTranslationProvider.getValue(this, "errorMessage", errorMessage);
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMainEntityName() {
        return getLabel();
    }

    @Override
    public <G extends ProductOption> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ProductOption cloned = createResponse.getClone();
        cloned.setName(name);
        cloned.setAttributeName(attributeName);
        cloned.setDisplayOrder(displayOrder);
        cloned.setErrorMessage(errorMessage);
        cloned.setErrorCode(errorCode);
        cloned.setLabel(label);
        cloned.setRequired(getRequired());
        cloned.setUseInSkuGeneration(getUseInSkuGeneration());
        cloned.setValidationString(validationString);
        cloned.setType(getType());
        cloned.setProductOptionValidationStrategyType(getProductOptionValidationStrategyType());
        cloned.setProductOptionValidationType(getProductOptionValidationType());
        for(ProductOptionValue entry : allowedValues){
            ProductOptionValue clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getAllowedValues().add(clonedEntry);
        }
        for(ProductOptionXref entry : products){
            ProductOptionXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getProductXrefs().add(clonedEntry);
        }

        return createResponse;
    }
}

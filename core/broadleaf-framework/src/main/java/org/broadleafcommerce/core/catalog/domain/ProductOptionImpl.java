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

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.presentation.*;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    
    @Column(name = "OPTION_TYPE")
    @AdminPresentation(friendlyName = "productOption_Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionType")
    protected String type;
    
    @Column(name = "ATTRIBUTE_NAME")
    @AdminPresentation(friendlyName = "productOption_name", helpText = "productOption_nameHelp", requiredOverride = RequiredOverride.REQUIRED)
    protected String attributeName;
    
    @Column(name = "LABEL")
    @AdminPresentation(friendlyName = "productOption_Label", helpText = "productOption_labelHelp", 
        prominent = true,
        translatable = true)
    protected String label;

    @Column(name = "REQUIRED")
    @AdminPresentation(friendlyName = "productOption_Required", prominent = true)
    protected Boolean required;

    @Column(name = "USE_IN_SKU_GENERATION")
    @AdminPresentation(friendlyName = "productOption_UseInSKUGeneration")
    private Boolean useInSkuGeneration;

    @Column(name = "DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "productOption_displayOrder")
    protected Integer displayOrder;

    @Column(name = "VALIDATION_STRATEGY_TYPE")
    @AdminPresentation(friendlyName = "productOption_validationStrategyType", group = GroupName.Validation, fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationStrategyType")
    private String productOptionValidationStrategyType;

    @Column(name = "VALIDATION_TYPE")
    @AdminPresentation(friendlyName = "productOption_validationType", group = GroupName.Validation, fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType")
    private String productOptionValidationType;

    @Column(name = "VALIDATION_STRING")
    @AdminPresentation(friendlyName = "productOption_validationSring", group = GroupName.Validation)
    protected String validationString;

    @Column(name = "ERROR_CODE")
    @AdminPresentation(friendlyName = "productOption_errorCode", group = GroupName.Validation)
    protected String errorCode;

    @Column(name = "ERROR_MESSAGE")
    @AdminPresentation(friendlyName = "productOption_errorMessage", group = GroupName.Validation, translatable = true)
    protected String errorMessage;

    @OneToMany(mappedBy = "productOption", targetEntity = ProductOptionValueImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @OrderBy(value = "displayOrder")
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "ProductOptionImpl_Allowed_Values")
    protected List<ProductOptionValue> allowedValues = new ArrayList<ProductOptionValue>();

    @OneToMany(targetEntity = ProductOptionXrefImpl.class, mappedBy = "productOption")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<ProductOptionXref> products = new ArrayList<ProductOptionXref>();
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
        List<Product> response = new ArrayList<Product>();
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

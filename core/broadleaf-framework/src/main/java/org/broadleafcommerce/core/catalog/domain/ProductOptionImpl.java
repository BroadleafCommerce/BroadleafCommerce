package org.broadleafcommerce.core.catalog.domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Base Product Option")
public class ProductOptionImpl implements ProductOption {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionId")
    @GenericGenerator(
        name="ProductOptionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="ProductOptionImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_ID")
    protected Long id;
    
    @Column(name = "TYPE")
    @AdminPresentation(friendlyName = "Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.catalog.service.type.ProductOptionType")
    protected String type;
    
    @Column(name = "OPTION_LABEL")
    @AdminPresentation(friendlyName = "Option Label")
    protected String optionLabel;
    
    @Column(name = "VALUE")
    @AdminPresentation(friendlyName = "Value")
    protected String value;
    
    @Column(name = "REQUIRED")
    @AdminPresentation(friendlyName = "Required")
    protected Boolean required;
    
    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;
    
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
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getOptionLabel() {
        return optionLabel;
    }

    @Override
    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
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
    public Product getProduct() {
        return product;
    }

    @Override
    public void setProduct(Product product){
        this.product = product;
    }

}

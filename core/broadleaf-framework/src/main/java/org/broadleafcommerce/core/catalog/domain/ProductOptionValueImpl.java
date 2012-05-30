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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_VALUE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Product Option Value")
public class ProductOptionValueImpl implements ProductOptionValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "ProductOptionValueId")
    @GenericGenerator(
        name="ProductOptionValueId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="ProductOptionValueImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl")
        }
    )
    @Column(name = "PRODUCT_OPTION_VALUE_ID")
    protected Long id;

    @Column(name = "VALUE")
    @AdminPresentation(friendlyName = "Value")
    protected String value;

    @Column(name ="DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "Display Order")
    protected Long displayOrder;

    @ManyToOne(targetEntity = ProductOptionImpl.class)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    protected ProductOption productOption;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
    public ProductOption getProductOption() {
        return productOption;
    }

    @Override
    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }

    @Override
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }
}

package org.broadleafcommerce.core.catalog.domain.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.presentation.AdminPresentation;

@MappedSuperclass
public abstract class CrossSaleProductMappedSuperclass implements RelatedProduct {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(generator = "CrossSaleProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CrossSaleProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CrossSaleProductImpl", allocationSize = 50)
    @Column(name = "CROSS_SALE_PRODUCT_ID")
    private Long id;
	
	@Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName="Cross Sale Promotion Message", largeEntry=true)
    private String promotionMessage;

    @Column(name = "SEQUENCE")
    private Long sequence;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPromotionMessage() {
        return promotionMessage;
    }
    
    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }
    
    public Long getSequence() {
        return sequence;
    }
    
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}

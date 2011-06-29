package org.broadleafcommerce.core.catalog.domain.common;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.presentation.AdminPresentation;

@MappedSuperclass
public abstract class FeaturedProductMappedSuperclass implements FeaturedProduct {

	private static final long serialVersionUID = 1L;

	/** The id. */
    @Id
    @GeneratedValue(generator = "FeaturedProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FeaturedProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxFeaturedProductImpl", allocationSize = 50)
    @Column(name = "FEATURED_PRODUCT_ID")
    protected Long id;
    
    @Column(name = "SEQUENCE")
    protected Long sequence;

    @Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName="Featured Product Promotion Message", largeEntry=true)
    protected String promotionMessage;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
    
    public Long getSequence() {
    	return this.sequence;
    }

    public String getPromotionMessage() {
        return promotionMessage;
    }

    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }
}

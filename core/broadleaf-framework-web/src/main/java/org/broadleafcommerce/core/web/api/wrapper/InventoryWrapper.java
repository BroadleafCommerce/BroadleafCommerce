package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Sku;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inventory")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class InventoryWrapper extends BaseWrapper {

    @XmlElement
    protected Long skuId;
    
    @XmlElement(nillable = true)
    protected Integer quantityAvailable;
    
    @XmlElement(nillable = true)
    protected String inventoryType;

    public void wrapDetails(Sku sku, Integer quantityAvailable, HttpServletRequest request) {
        if (sku != null) {
            this.skuId = sku.getId();
            if (sku.getInventoryType() != null) {
                this.inventoryType = sku.getInventoryType().getType();
            }
        }
        this.quantityAvailable = quantityAvailable;
    }

    public void wrapSummary(Sku sku, Integer quantity, HttpServletRequest request) {
        wrapDetails(sku, quantityAvailable, request);
    }
}

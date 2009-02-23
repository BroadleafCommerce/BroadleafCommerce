package org.broadleafcommerce.tcs;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.BroadleafSku;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "TCS_SKU")
public class TCSSku extends BroadleafSku {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "sku")
    @IndexColumn(name = "SEQUENCE")
    private List<TCSComponentSku> componentSkus;

    public List<TCSComponentSku> getComponentSkus() {
        return componentSkus;
    }

    public void setComponentSkus(List<TCSComponentSku> componentSkus) {
        this.componentSkus = componentSkus;
    }

    public boolean isKit() {
        return getComponentSkus().size() > 0;
    }
}

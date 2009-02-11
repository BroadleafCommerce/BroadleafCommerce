package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Set;

//@Entity
public class SkuBundle extends Sku implements Serializable {

    private static final long serialVersionUID = 1L;
    
//    @ManyToMany
//    @JoinTable(name = "BUNDLED_ITEMS", joinColumns = @JoinColumn(name = "BUNDLE_ID", referencedColumnName = "SKU_ID"), inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"))
    private Set<Sku> bundledSkus;

    public Set<Sku> getBundledSkus() {
        return bundledSkus;
    }

    public void setBundledSkus(Set<Sku> bundledSkus) {
        this.bundledSkus = bundledSkus;
    }

}

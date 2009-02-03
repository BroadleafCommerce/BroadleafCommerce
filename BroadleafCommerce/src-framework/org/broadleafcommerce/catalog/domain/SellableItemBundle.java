package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class SellableItemBundle extends SellableItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ManyToMany
    @JoinTable(name = "BUNDLED_ITEMS", joinColumns = @JoinColumn(name = "BUNDLE_ID", referencedColumnName = "SELLABLE_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "SELLABLE_ITEM_ID", referencedColumnName = "SELLABLE_ITEM_ID"))
    private Set<SellableItem> bundledItems;

    public Set<SellableItem> getBundledItems() {
        return bundledItems;
    }

    public void setBundledItems(Set<SellableItem> bundledItems) {
        this.bundledItems = bundledItems;
    }

}

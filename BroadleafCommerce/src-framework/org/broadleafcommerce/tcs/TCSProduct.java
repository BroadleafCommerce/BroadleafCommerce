package org.broadleafcommerce.tcs;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "TCS_PRODUCT")
public class TCSProduct extends ProductImpl {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "product")
    @OrderBy(clause = "SEQUENCE")
    private Set<TCSAccessorySku> accessorySkus;

    public Set<TCSAccessorySku> getAccessorySkus() {
        return accessorySkus;
    }

    public void setAccessorySkus(Set<TCSAccessorySku> accessorySkus) {
        this.accessorySkus = accessorySkus;
    }
}

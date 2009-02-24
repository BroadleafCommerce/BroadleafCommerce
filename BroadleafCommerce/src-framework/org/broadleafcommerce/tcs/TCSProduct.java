package org.broadleafcommerce.tcs;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "TCS_PRODUCT")
public class TCSProduct extends ProductImpl {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "product")
    @IndexColumn(name = "SEQUENCE")
    private Set<TCSAccessorySku> accessorySkus;
}

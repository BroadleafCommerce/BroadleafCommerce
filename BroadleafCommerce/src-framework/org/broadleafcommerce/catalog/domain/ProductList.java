package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Set;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "TYPE")
//@Table(name = "BLC_PRODUCT_LIST")
public class ProductList implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "PRODUCT_LIST_ID")
    private Long id;

//    @OneToMany(mappedBy = "productList")
    private Set<ProductListAssociation> productListAssociations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ProductListAssociation> getProductListAssociations() {
        return productListAssociations;
    }

    public void setProductListAssociations(Set<ProductListAssociation> productListAssociations) {
        this.productListAssociations = productListAssociations;
    }

}

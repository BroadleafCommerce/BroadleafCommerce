package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.common.domain.Auditable;

//@Entity
//@Table(name = "BLC_PRODUCT")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "PRODUCT_ID")
    private Long id;
    
//    @Embedded
    private Auditable auditable;

//    @OneToMany(mappedBy = "product")
//    @MapKey(name="name")
    private Map<String, ItemAttribute> itemAttributes;

//    @OneToMany(mappedBy = "product")
    private Set<ProductListAssociation> productListAssociations;

//    @Column(name = "DESCRIPTION")
    private String description;

//    @Column(name = "NAME")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public Set<ProductListAssociation> getProductListAssociations() {
        return productListAssociations;
    }

    public void setProductListAssociations(Set<ProductListAssociation> productListAssociations) {
        this.productListAssociations = productListAssociations;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }
}

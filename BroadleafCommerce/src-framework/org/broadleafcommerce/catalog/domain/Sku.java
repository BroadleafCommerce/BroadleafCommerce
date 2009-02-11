package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Map;

import org.broadleafcommerce.common.domain.Auditable;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "TYPE")
//@Table(name = "BLC_SKU")
public class Sku implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "SKU_ID")
    private Long id;

//    @Column(name="PRICE", nullable = false)
    private double price;

//    @Embedded
    private Auditable auditable;

//    @ManyToOne
//    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

//    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL)
//    @MapKey(name="name")
    private Map<String,ItemAttribute> itemAttributes;

//    @Column(name = "NAME")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public Map<String, ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

}

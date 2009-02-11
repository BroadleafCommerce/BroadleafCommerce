package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

//@Entity
//@Table(name = "BLC_PRODUCT_LIST_ASSOCIATION")
public class ProductListAssociation implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "ASSOCIATION_ID")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

//    @ManyToOne
//    @JoinColumn(name = "PRODUCT_LIST_ID")
    private ProductList productList;

//    @Column(name = "RANK")
    private Integer rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductList getProductList() {
        return productList;
    }

    public void setProductList(ProductList productList) {
        this.productList = productList;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}

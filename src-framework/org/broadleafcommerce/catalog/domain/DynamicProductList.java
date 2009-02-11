package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

//@Entity
public class DynamicProductList extends ProductList implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Column(name="QUERY")
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}

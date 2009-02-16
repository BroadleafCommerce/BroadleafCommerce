package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafProductList implements ProductList, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private boolean dynamicFlag;

    private String dynamicQuery;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDynamicFlag() {
        return dynamicFlag;
    }

    public void setDynamicFlag(boolean dynamicFlag) {
        this.dynamicFlag = dynamicFlag;
    }

    public String getDynamicQuery() {
        return dynamicQuery;
    }

    public void setDynamicQuery(String dynamicQuery) {
        this.dynamicQuery = dynamicQuery;
    }
}

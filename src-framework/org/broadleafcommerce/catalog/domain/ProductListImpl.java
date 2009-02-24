package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_LIST")
public class ProductListImpl implements ProductList, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "PRODUCT_LIST_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DYNAMIC_FLAG")
    private boolean dynamicFlag;

    @Column(name = "DYNAMIC_QUERY")
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

package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafCategoryImage implements CategoryImage, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

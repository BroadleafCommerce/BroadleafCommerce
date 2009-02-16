package org.broadleafcommerce.catalog.domain;

public interface ProductList {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public boolean isDynamicFlag();

    public void setDynamicFlag(boolean dynamicFlag);

    public String getDynamicQuery();

    public void setDynamicQuery(String dynamicQuery);
}

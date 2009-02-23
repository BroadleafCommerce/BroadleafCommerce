package org.broadleafcommerce.catalog.domain;

public interface SkuAttribute {

    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public Boolean getSearchable();

    public void setSearchable(Boolean searchable);

    public Sku getSku();

    public void setSku(Sku sku);

    public String getName();

    public void setName(String name);

}

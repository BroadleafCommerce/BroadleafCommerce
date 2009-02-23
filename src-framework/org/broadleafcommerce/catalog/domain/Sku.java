package org.broadleafcommerce.catalog.domain;

import java.math.BigDecimal;
import java.util.Date;

public interface Sku {

    public Long getId();

    public void setId(Long id);

    // TODO fix
    // public Set<Sku> getChildSkus();
    //
    // public void setChildSkus(Set<Sku> childSkus);

    public BigDecimal getSalePrice();

    public void setSalePrice(BigDecimal salePrice);

    public BigDecimal getListPrice();

    public void setListPrice(BigDecimal listPrice);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public boolean isTaxable();

    public void setTaxable(boolean taxable);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);

    // public Map<String, ItemAttribute> getItemAttributes();
    //
    // public void setItemAttributes(Map<String, ItemAttribute> itemAttributes);

}

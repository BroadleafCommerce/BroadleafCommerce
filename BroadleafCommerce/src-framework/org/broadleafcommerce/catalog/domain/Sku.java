package org.broadleafcommerce.catalog.domain;

import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.common.domain.Auditable;

public interface Sku {

    public Long getId();

    public void setId(Long id);

    public Set<Sku> getChildSkus();

    public void setChildSkus(Set<Sku> childSkus);

    public double getPrice();

    public void setPrice(double price);

    public Product getProduct();

    public void setProduct(Product product);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);

    public String getName();

    public void setName(String name);

    public Map<String, ItemAttribute> getItemAttributes();

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes);
}

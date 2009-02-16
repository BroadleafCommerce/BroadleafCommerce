package org.broadleafcommerce.catalog.domain;

import java.util.Map;

public interface Product {

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public Long getId();

    public void setId(Long id);

    public Map<String, ItemAttribute> getItemAttributes();

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes);
}

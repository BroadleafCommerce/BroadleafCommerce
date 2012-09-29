package org.broadleafcommerce.core.catalog.domain;

public interface SkuTranslation extends LocaleIf {

    public Long getId();

    public void setId(Long id);

    public String getDescription();

    public void setDescription(String description);

    public String getName();

    public void setName(String name);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

}

package org.broadleafcommerce.core.catalog.domain;

public interface ProductOptionTranslation extends LocaleIf {

    public Long getId();

    public void setId(Long id);

    /**
     * Returns the associated ProductOption
     * 
     * @return
     */
    public ProductOption getProductOption();

    /**
     * Sets the associated product option.
     * 
     * @param productOption
     */
    public void setProductOption(ProductOption productOption);

    public String getLabel();

    public void setLabel(String Label);


}

package org.broadleafcommerce.cms.file.domain;

/**
 * Created by bpolster.
 */
public interface StaticAssetDescription {
    public Long getId();

    public void setId(Long id);

    public String getLanguageCode();

    public void setLanguageCode(String languageCode);

    public String getDescription();

    public void setDescription(String description);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public StaticAsset getStaticAsset();

    public void setStaticAsset(StaticAsset staticAsset);
}

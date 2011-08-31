package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.page.domain.Locale;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface StaticAssetDescription extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getDescription();

    public void setDescription(String description);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public StaticAsset getStaticAsset();

    public void setStaticAsset(StaticAsset staticAsset);

    public StaticAssetDescription cloneEntity();

    public String getFieldKey();

    public void setFieldKey(String fieldKey);

    public Locale getLocale();

    public void setLocale(Locale locale);

}

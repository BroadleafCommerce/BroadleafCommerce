package org.broadleafcommerce.cms.file.domain;

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

    public StaticAssetDescription cloneEntity();

}

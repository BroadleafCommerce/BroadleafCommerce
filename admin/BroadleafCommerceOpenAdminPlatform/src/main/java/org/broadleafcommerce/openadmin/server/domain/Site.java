package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface Site extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getSiteIdentifierType();

    public void setSiteIdentifierType(String siteIdentifierType);

    public String getSiteIdentifierValue();

    public void setSiteIdentifierValue(String siteIdentifierValue);

    public SandBox getProductionSandbox();

    public void setProductionSandbox(SandBox sandbox);
}

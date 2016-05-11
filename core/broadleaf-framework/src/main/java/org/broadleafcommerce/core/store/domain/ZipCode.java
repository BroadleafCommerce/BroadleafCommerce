/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.store.domain;

public interface ZipCode {

    public String getId();

    public void setId(String id);

    public Integer getZipcode();

    public void setZipcode(Integer zipcode);

    public String getZipState();

    public void setZipState(String zipState);

    public String getZipCity();

    public void setZipCity(String zipCity);

    public double getZipLongitude();

    public void setZipLongitude(double zipLongitude);

    public double getZipLatitude();

    public void setZipLatitude(double zipLatitude);

}

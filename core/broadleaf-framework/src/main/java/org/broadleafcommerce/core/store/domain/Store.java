/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.profile.core.domain.Address;

import java.io.Serializable;

public interface Store extends Status, Serializable {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getStoreNumber();

    void setStoreNumber(String storeNumber);

    Boolean getOpen();

    void setOpen(Boolean open);

    String getStoreHours();

    void setStoreHours(String storeHours);

    Address getAddress();

    void setAddress(Address address);

    Double getLongitude();

    void setLongitude(Double longitude);

    Double getLatitude();

    void setLatitude(Double latitude);

}

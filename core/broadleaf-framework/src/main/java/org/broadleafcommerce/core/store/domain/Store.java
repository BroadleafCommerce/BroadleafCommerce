/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.store.domain;

import java.io.Serializable;

public interface Store extends Serializable{

    public Long getId();
    public void setId(Long id);

    public String getName();
    public void setName(String name);

    public String getAddress1();
    public void setAddress1(String address1);

    public String getAddress2();
    public void setAddress2(String address2);

    public String getCity();
    public void setCity(String city);

    public String getZip();
    public void setZip(String zip);

    public String getCountry();
    public void setCountry(String country);

    public String getPhone();
    public void setPhone(String phone);

    public Double getLongitude();
    public void setLongitude(Double longitude);

    public Double getLatitude();
    public void setLatitude(Double latitude);

    public void setState(String state);
    public String getState();

}

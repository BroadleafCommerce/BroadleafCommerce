/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.locale.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

import java.io.Serializable;

/**
 * Created by jfischer
 */
public interface Locale extends Serializable {

    String getLocaleCode();

    void setLocaleCode(String localeCode);

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

    public void setDefaultFlag(Boolean defaultFlag);

    public Boolean getDefaultFlag();

    public BroadleafCurrency getDefaultCurrency();

    public void setDefaultCurrency(BroadleafCurrency currency);

    public Boolean getUseInSearchIndex();
    
    public void setUseInSearchIndex(Boolean useInSearchIndex);

}

/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.index;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class IndexStatusInfoImpl implements IndexStatusInfo {

    private Date lastIndexDate;
    private Map<String, String> additionalInfo = new HashMap<String, String>();

    @Override
    public Date getLastIndexDate() {
        return lastIndexDate;
    }

    @Override
    public void setLastIndexDate(Date lastIndexDate) {
        this.lastIndexDate = lastIndexDate;
    }

    @Override
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}

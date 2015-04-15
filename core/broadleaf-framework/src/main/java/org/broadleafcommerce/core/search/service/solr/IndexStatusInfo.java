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
package org.broadleafcommerce.core.search.service.solr;

import java.util.Date;
import java.util.Map;

/**
 * General information about the current status of a (embedded) Solr instance's index
 *
 * @author Jeff Fischer
 */
public interface IndexStatusInfo {

    /**
     * The most recent index date
     *
     * @return
     */
    Date getLastIndexDate();

    void setLastIndexDate(Date lastIndexDate);

    /**
     * Arbitrary information about the index.
     *
     * @return
     */
    Map<String, String> getAdditionalInfo();

    void setAdditionalInfo(Map<String, String> additionalInfo);

}

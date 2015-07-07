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
package org.broadleafcommerce.core.search.redirect.domain;

import java.io.Serializable;
import java.util.Date;

public interface SearchRedirect extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getSearchTerm();

    public void setSearchTerm(String searchTerm);

    public String getUrl();

    public void setUrl(String url);

    public Integer getSearchPriority() ;
    
    public void setSearchPriority(Integer searchPriority);

    public Date getActiveStartDate() ;

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate() ;

    public void setActiveEndDate(Date activeEndDate);

    public boolean isActive();
}

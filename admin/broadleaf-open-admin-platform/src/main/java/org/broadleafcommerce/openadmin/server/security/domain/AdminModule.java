/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.domain;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author elbertbautista
 *
 */
public interface AdminModule extends Serializable {

    public Long getId();

    public String getName();

    public void setName(String name);

    public String getModuleKey();

    public void setModuleKey(String moduleKey);

    public String getIcon();

    public void setIcon(String icon);

    public List<AdminSection> getSections();

    public void setSections(List<AdminSection> sections);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);

}

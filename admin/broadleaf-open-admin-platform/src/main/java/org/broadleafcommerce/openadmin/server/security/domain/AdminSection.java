/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.security.domain;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author elbertbautista
 *
 */
public interface AdminSection extends Serializable {

    public Long getId();

    public String getName();

    public void setName(String name);

    public String getSectionKey();

    public void setSectionKey(String sectionKey);

    public String getUrl();

    public void setUrl(String url);

    public List<AdminPermission> getPermissions();

    public void setPermissions(List<AdminPermission> permissions);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public void setDisplayController(String displayController);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public String getDisplayController();

    public AdminModule getModule();

    public void setModule(AdminModule module);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public Boolean getUseDefaultHandler();

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public void setUseDefaultHandler(Boolean useDefaultHandler);

    public String getCeilingEntity();

    public void setCeilingEntity(String ceilingEntity);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);
}

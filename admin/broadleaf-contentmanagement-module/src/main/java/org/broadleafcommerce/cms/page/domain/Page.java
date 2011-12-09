/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.page.domain;

import java.io.Serializable;
import java.util.Map;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.Site;

/**
 * Created by bpolster.
 */
public interface Page extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getFullUrl();

    public void setFullUrl(String fullUrl);

    public String getDescription();

    public void setDescription(String description);

    public PageTemplate getPageTemplate();

    public void setPageTemplate(PageTemplate pageTemplate);

    public Map<String, PageField> getPageFields();

    public void setPageFields(Map<String, PageField> pageFields);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public Site getSite();

    public void setSite(Site site);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);

    public Long getOriginalPageId();

    public void setOriginalPageId(Long originalPageId);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Page cloneEntity();

}

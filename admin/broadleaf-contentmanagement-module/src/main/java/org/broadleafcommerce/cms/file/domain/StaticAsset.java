/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StaticAsset extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Site getSite();

    public void setSite(Site site);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public String getName();

    public void setName(String name);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);

    public String getFullUrl();

    public Long getFileSize();

    public void setFullUrl(String fullUrl);

    public void setFileSize(Long fileSize);

    public Map<String, StaticAssetDescription> getContentMessageValues();

    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public Long getOriginalAssetId();

    public void setOriginalAssetId(Long originalPageId);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public StaticAsset cloneEntity();

    public String getMimeType();

    public void setMimeType(String mimeType);

    public String getFileExtension();

    public void setFileExtension(String fileExtension);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);

}

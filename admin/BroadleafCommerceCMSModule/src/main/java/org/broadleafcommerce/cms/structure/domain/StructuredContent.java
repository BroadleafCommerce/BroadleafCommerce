/*
 * Copyright 2008-2011 the original author or authors.
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StructuredContent extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getContentName();

    public void setContentName(String contentName);

    public Locale getLocale();

    public void setLocale(Locale locale);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public StructuredContentType getStructuredContentType();

    public void setStructuredContentType(StructuredContentType structuredContentType);

    public Map<String, StructuredContentField> getStructuredContentFields();

    public void setStructuredContentFields(Map<String, StructuredContentField> structuredContentFields);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public Boolean getOfflineFlag();

    public void setOfflineFlag(Boolean offlineFlag);

    public Integer getPriority();

    public void setPriority(Integer priority);

    public String getDisplayRule();

    public void setDisplayRule(String displayRule);

    public Long getOriginalItemId();

    public void setOriginalItemId(Long originalItemId);

    public StructuredContent cloneEntity();

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public Boolean getLockedFlag();

    public void setLockedFlag(Boolean lockedFlag);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);
}

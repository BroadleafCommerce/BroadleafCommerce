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

import org.broadleafcommerce.openadmin.server.domain.Site;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StructuredContent {
    public Long getId();

    public void setId(Long id);

    public String getContentName();

    public void setContentName(String contentName);

    public String getDescription();

    public void setDescription(String description);

    public String getLanguageCode();

    public void setLanguageCode(String languageCode);

    public Site getSite();

    public void setSite(Site site);

    public StructuredContentType getStructuredContentType();

    public void setStructuredContentType(StructuredContentType structuredContentType);

    public Map<String, StructuredContentField> getStructuredContentFields();

    public void setStructuredContentFields(Map<String, StructuredContentField> structuredContentFields);

    public List<ContentDisplayRule> getContentDisplayRules();

    public void setContentDisplayRules(List<ContentDisplayRule> contentDisplayRules);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public Boolean getOnlineFlag();

    public void setOnlineFlag(Boolean onlineFlag);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);
}

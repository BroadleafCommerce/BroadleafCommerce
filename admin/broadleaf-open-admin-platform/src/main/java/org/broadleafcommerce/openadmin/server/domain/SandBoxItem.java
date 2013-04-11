/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.List;

public interface SandBoxItem extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Long getSandBoxId();

    public void setSandBoxId(Long sandBoxId);

    public Long getOriginalSandBoxId();

    public void setOriginalSandBoxId(Long sandBox);

    public SandBoxItemType getSandBoxItemType();

    public void setSandBoxItemType(SandBoxItemType itemType);

    public SandBoxOperationType getSandBoxOperationType();

    public void setSandBoxOperationType(SandBoxOperationType type);

    public String getDescription();

    public void setDescription(String description);

    public Long getTemporaryItemId();

    public void setTemporaryItemId(Long id);

    public Long getOriginalItemId();

    public void setOriginalItemId(Long id);

    public List<SandBoxAction> getSandBoxActions();

    public void setSandBoxActions(List<SandBoxAction> actionList);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public void addSandBoxAction(SandBoxAction action);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);

    public String getGroupDescription();

    public void setGroupDescription(String groupDescription);

}
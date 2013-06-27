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

package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType;

import java.util.List;

public interface SandBoxItemDao {

    public SandBoxItem retrieveById(Long id);

    public SandBoxItem retrieveBySandboxAndTemporaryItemId(Long sandBoxId, SandBoxItemType type, Long tempItemId);

    public SandBoxItem addSandBoxItem(Long sandBox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, Long temporaryId, Long originalId);

    public SandBoxItem addSandBoxItem(Long sbox, SandBoxOperationType operationType, SandBoxItemType itemType, String description, String groupDescription, Long temporaryId, Long originalId);

    public SandBoxItem updateSandBoxItem(SandBoxItem sandBoxItem);

    public List<SandBoxItem> retrieveSandBoxItemsForSandbox(Long sandBox);

    public void delete(SandBoxItem sandBoxItem);

    public List<SandBoxItem> retrieveSandBoxItemsByTypeForSandbox(Long sandBox, SandBoxItemType itemType);

    public List<SandBoxItem> retrieveByGroupName(Long sandBoxId, String groupName);

    public List<SandBoxItem> retrieveSandBoxItemsByTypesForSandbox(Long sandBox, List<SandBoxItemType> sandBoxItemTypes);

}
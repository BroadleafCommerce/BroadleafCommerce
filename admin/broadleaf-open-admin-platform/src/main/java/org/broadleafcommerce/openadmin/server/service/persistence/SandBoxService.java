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

package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

import java.util.Calendar;
import java.util.List;

public interface SandBoxService {

    public SandBox retrieveSandBoxById(Long id);

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param adminUser
     * @return
     */
    public SandBox retrieveUserSandBox(Site site, AdminUser adminUser);

    public void promoteAllSandBoxItems(SandBox sandBox, String comment);

    public void promoteSelectedItems(SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    public void schedulePromotionForSandBox(SandBox sandBox, Calendar calendar);

    public void schedulePromotionForSandBoxItems(List<SandBoxItem> sandBoxItems, Calendar calendar);

    public void revertAllSandBoxItems(SandBox originalSandBox, SandBox sandBox);

    public void revertSelectedSandBoxItems(SandBox sandBox, List<SandBoxItem> sandBoxItems);

    public void rejectAllSandBoxItems(SandBox originalSandBox, SandBox sandBox, String comment);

    public void rejectSelectedSandBoxItems(SandBox sandBox, String comment, List<SandBoxItem> sandBoxItems);

    public SandBox retrieveApprovalSandBox(SandBox sandBox);

    public SandBox createSandBox(Site site, String sandBoxName, SandBoxType sandBoxType) throws Exception;

    public SandBox retrieveSandBox(Site site, String sandBoxName, SandBoxType sandBoxType);

}
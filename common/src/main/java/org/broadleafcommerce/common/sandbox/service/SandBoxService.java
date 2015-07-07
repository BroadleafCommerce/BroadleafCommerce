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
package org.broadleafcommerce.common.sandbox.service;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SandBoxService {

    public SandBox retrieveSandBoxById(Long id);
    
    public List<SandBox> retrieveAllSandBoxes();

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param adminUser
     * @return
     */
    public SandBox retrieveUserSandBox(Long authorId, Long overrideSandBoxId, String sandBoxName);
    
    public SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId);
    
    /**
     * Returns the SandBox by id but only if the SandBox is associated with the current site.
     * @param sandBoxId
     * @return
     */
    public SandBox retrieveSandBoxManagementById(Long sandBoxId);

    public List<SandBox> retrievePreviewSandBoxes(Long authorId);
    
    public List<SandBox> retrieveSandBoxesByType(SandBoxType type);
    
    public Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds);

    public SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) throws Exception;

    public SandBox createUserSandBox(Long authorId, SandBox approvalSandBox);
    
    public SandBox retrieveSandBox(String sandBoxName, SandBoxType sandBoxType);

    public SandBox createDefaultSandBox();

    /**
     * Returns true if an existing sandboxName exists with the passed in name.  
     * @param sandboxName
     * @return
     */
    boolean checkForExistingApprovalSandboxWithName(String sandboxName);

    /**
     * @deprecated Not used in BLC.   May return incorrect results in MT installations.
     * Reads all SandBoxes that are of type {@link SandBoxType.USER} and belong to the given
     * user.
     * 
     * @param authorId
     * @return a list of SandBox belonging to the user
     */
    public List<SandBox> retrieveAllUserSandBoxes(Long authorId);

    public void archiveChildSandboxes(Long parentSandBoxId);

    public List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId);
}

/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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

    /**
     * Returns a map of SanboxId and Sandbox name
     *
     * @param sandBoxIds
     * @return
     */
    public Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds);

    public SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) throws Exception;

    public SandBox createUserSandBox(Long authorId, SandBox approvalSandBox);

    Long createUserSandBox(Long authorId, Long approvalSandbox);
    
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

    public boolean checkForExistingSandbox(SandBoxType sandBoxType, String sandboxName, Long authorId);
}

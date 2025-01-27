/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

    SandBox retrieveSandBoxById(Long id);

    List<SandBox> retrieveAllSandBoxes();

    /**
     * Returns the sandbox currently associated with the passed in userId.
     * If one is not associated, it uses (or creates) a default user sandbox with the
     * name:   user:username.
     *
     * @param authorId
     * @param overrideSandBoxId
     * @param sandBoxName
     * @return
     */
    SandBox retrieveUserSandBox(Long authorId, Long overrideSandBoxId, String sandBoxName);

    SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId);

    /**
     * Returns the SandBox by id but only if the SandBox is associated with the current site.
     *
     * @param sandBoxId
     * @return
     */
    SandBox retrieveSandBoxManagementById(Long sandBoxId);

    List<SandBox> retrievePreviewSandBoxes(Long authorId);

    List<SandBox> retrieveSandBoxesByType(SandBoxType type);

    Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds);

    /**
     * Returns a map of SanboxId and Sandbox name
     *
     * @param sandBoxIds
     * @return
     */
    Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds);

    SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) throws Exception;

    SandBox createUserSandBox(Long authorId, SandBox approvalSandBox);

    Long createUserSandBox(Long authorId, Long approvalSandbox);

    SandBox retrieveSandBox(String sandBoxName, SandBoxType sandBoxType);

    SandBox createDefaultSandBox();

    /**
     * Returns true if an existing sandboxName exists with the passed in name.
     *
     * @param sandboxName
     * @return
     */
    boolean checkForExistingApprovalSandboxWithName(String sandboxName);

    /**
     * @param authorId
     * @return a list of SandBox belonging to the user
     * @deprecated Not used in BLC.   May return incorrect results in MT installations.
     * Reads all SandBoxes that are of type {@link SandBoxType.USER} and belong to the given
     * user.
     */
    List<SandBox> retrieveAllUserSandBoxes(Long authorId);

    void archiveChildSandboxes(Long parentSandBoxId);

    List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId);

    boolean checkForExistingSandbox(SandBoxType sandBoxType, String sandboxName, Long authorId);

}

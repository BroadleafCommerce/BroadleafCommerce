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
package org.broadleafcommerce.common.sandbox.dao;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SandBoxDao {

    SandBox retrieve(Long id);

    List<SandBox> retrieveAllSandBoxes();

    List<SandBox> retrieveSandBoxesByType(SandBoxType sandboxType);

    List<SandBox> retrieveSandBoxesForAuthor(Long authorId);

    SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId);

    SandBox retrieveSandBoxManagementById(Long sandBoxId);

    SandBox retrieveNamedSandBox(SandBoxType sandboxType, String sandboxName);

    Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds);

    Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds);

    List<SandBox> retrieveSandBoxesForAuthor(Long authorId, SandBoxType sandBoxType);

    SandBox persist(SandBox entity);

    SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType);

    SandBox createUserSandBox(Long authorId, SandBox approvalSandBox);

    SandBox createDefaultSandBox();

    /**
     * @param authorId
     * @return a list of SandBox belonging to the user
     * @deprecated Not used in BLC.   In a Multi-site context, may return results outside of a given tenant.
     * Reads all SandBoxes that are of type {@link SandBoxType.USER} and belong to the given
     * user.
     */
    @Deprecated
    List<SandBox> retrieveAllUserSandBoxes(Long authorId);

    SandBox merge(SandBox userSandBox);

    List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId);

    SandBox retrieveNamedSandBox(SandBoxType sandBoxType, String sandboxName, Long authorId);

}

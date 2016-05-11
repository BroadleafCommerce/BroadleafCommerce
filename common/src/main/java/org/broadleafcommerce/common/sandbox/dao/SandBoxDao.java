/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.sandbox.dao;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SandBoxDao {

    public SandBox retrieve(Long id);
    
    public List<SandBox> retrieveAllSandBoxes();

    public List<SandBox> retrieveSandBoxesByType(SandBoxType sandboxType);

    public List<SandBox> retrieveSandBoxesForAuthor(Long authorId);

    public SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId);

    public SandBox retrieveSandBoxManagementById(Long sandBoxId);

    public SandBox retrieveNamedSandBox(SandBoxType sandboxType, String sandboxName);

    public Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds);

    public Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds);

    List<SandBox> retrieveSandBoxesForAuthor(Long authorId, SandBoxType sandBoxType);

    public SandBox persist(SandBox entity);

    public SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType);

    public SandBox createUserSandBox(Long authorId, SandBox approvalSandBox);

    public SandBox createDefaultSandBox();

    /**
     * @deprecated Not used in BLC.   In a Multi-site context, may return results outside of a given tenant.
     * Reads all SandBoxes that are of type {@link SandBoxType.USER} and belong to the given
     * user.
     * 
     * @param authorId
     * @return a list of SandBox belonging to the user
     */
    @Deprecated
    List<SandBox> retrieveAllUserSandBoxes(Long authorId);

    SandBox merge(SandBox userSandBox);

    List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId);

    SandBox retrieveNamedSandBox(SandBoxType sandBoxType, String sandboxName, Long authorId);
}

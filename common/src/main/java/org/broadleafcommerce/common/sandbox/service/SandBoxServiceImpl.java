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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sandbox.dao.SandBoxDao;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

@Service(value = "blSandBoxService")
public class SandBoxServiceImpl implements SandBoxService {
    
    private static final Log LOG = LogFactory.getLog(SandBoxServiceImpl.class);

    private Map<String, Long> approvalToUserSandboxes = new ConcurrentHashMap<>();

    @Resource(name = "blSandBoxDao")
    protected SandBoxDao sandBoxDao;

    @Override
    public SandBox retrieveSandBoxById(Long sandboxId) {
        return sandBoxDao.retrieve(sandboxId);
    }
    
    @Override
    public List<SandBox> retrieveAllSandBoxes() {
        return sandBoxDao.retrieveAllSandBoxes();
    }
    
    @Override
    public List<SandBox> retrieveSandBoxesByType(SandBoxType type) {
        return sandBoxDao.retrieveSandBoxesByType(type);
    }
    
    @Override
    public SandBox retrieveUserSandBoxForParent(Long authorId, Long parentSandBoxId) {
        return sandBoxDao.retrieveUserSandBoxForParent(authorId, parentSandBoxId);
    }
    
    @Override
    public SandBox retrieveSandBoxManagementById(Long sandBoxId) {
        return sandBoxDao.retrieveSandBoxManagementById(sandBoxId);
    }

    @Override
    public List<SandBox> retrievePreviewSandBoxes(Long authorId) {
        List<SandBox> returnList = new ArrayList<SandBox>();
        List<SandBox> authorBoxes = sandBoxDao.retrieveSandBoxesForAuthor(authorId, SandBoxType.USER);
        List<SandBox> approvalBoxes = sandBoxDao.retrieveSandBoxesByType(SandBoxType.APPROVAL);
        List<SandBox> defaultBoxes = sandBoxDao.retrieveSandBoxesByType(SandBoxType.DEFAULT);

        List<SandBox> candidateBoxes = new ArrayList<SandBox>();
        candidateBoxes.addAll(approvalBoxes);
        candidateBoxes.addAll(defaultBoxes);
        
        returnList.addAll(authorBoxes);

        for (SandBox cb : candidateBoxes) {
            boolean match = false;
            for (SandBox authorBox : authorBoxes) {
                if (authorBox.getId().equals(cb.getId()) || 
                        (authorBox.getParentSandBox() != null && authorBox.getParentSandBox().getId().equals(cb.getId()))) {
                    match = true;
                }
            }
            if (!match) {
                returnList.add(cb);
            }
        }
        
        return returnList;
    }

    @Override
    public SandBox retrieveUserSandBox(Long authorId, Long overrideSandBoxId, String sandBoxName) {
        SandBox userSandbox;
        if (overrideSandBoxId != null) {
            userSandbox = retrieveSandBoxById(overrideSandBoxId);
        } else {
            userSandbox = retrieveSandBox(sandBoxName, SandBoxType.USER);
            if (userSandbox == null) {
                userSandbox = createSandBox(sandBoxName, SandBoxType.USER);
            }
        }

        return userSandbox;
    }
    
    @Override
    public Map<Long, String> retrieveAuthorNamesForSandBoxes(Set<Long> sandBoxIds) {
        return sandBoxDao.retrieveAuthorNamesForSandBoxes(sandBoxIds);
    }

    @Override
    public Map<Long, String> retrieveSandboxNamesForSandBoxes(Set<Long> sandBoxIds) {
        return sandBoxDao.retrieveSandboxNamesForSandBoxes(sandBoxIds);
    }

    @Override
    public synchronized SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) {
        return sandBoxDao.createSandBox(sandBoxName, sandBoxType);
    }
    
    @Override
    public synchronized SandBox createUserSandBox(Long authorId, SandBox approvalSandBox) {
        if (checkForExistingSandbox(SandBoxType.USER, approvalSandBox.getName(), authorId)) {
            return sandBoxDao.createUserSandBox(authorId, approvalSandBox);
        }

        return sandBoxDao.retrieveNamedSandBox(SandBoxType.USER, approvalSandBox.getName(), authorId);
    }

    @Override
    public synchronized Long createUserSandBox(Long authorId, Long approvalSandbox) {
        String key = authorId + "_" + approvalSandbox;
        if (!approvalToUserSandboxes.containsKey(key)) {
            SandBox approval = sandBoxDao.retrieve(approvalSandbox);
            SandBox user;
            if (checkForExistingSandbox(SandBoxType.USER, approval.getName(), authorId)) {
                user = sandBoxDao.createUserSandBox(authorId, approval);
            } else {
                user = sandBoxDao.retrieveNamedSandBox(SandBoxType.USER, approval.getName(), authorId);
            }
            approvalToUserSandboxes.put(key, user.getId());
        }
        return approvalToUserSandboxes.get(key);
    }

    @Override
    public synchronized SandBox createDefaultSandBox() {
        return sandBoxDao.createDefaultSandBox();
    }

    @Override
    public SandBox retrieveSandBox(String sandBoxName, SandBoxType sandBoxType) {
        return sandBoxDao.retrieveNamedSandBox(sandBoxType, sandBoxName);
    }

    @Override
    @Deprecated
    public List<SandBox> retrieveAllUserSandBoxes(Long authorId) {
        return sandBoxDao.retrieveAllUserSandBoxes(authorId);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void archiveChildSandboxes(Long parentSandBoxId) {
        List<SandBox> childSandBoxes = retrieveChildSandBoxesByParentId(parentSandBoxId);
        for (SandBox sandbox : childSandBoxes) {
            sandbox.setArchived('Y');
            sandBoxDao.merge(sandbox);
        }
    }

    public List<SandBox> retrieveChildSandBoxesByParentId(Long parentSandBoxId) {
        return sandBoxDao.retrieveChildSandBoxesByParentId(parentSandBoxId);
    }

    @Override
    public boolean checkForExistingApprovalSandboxWithName(String sandboxName) {
        return checkForExistingSandbox(SandBoxType.APPROVAL, sandboxName, null);
    }

    @Override
    public boolean checkForExistingSandbox(SandBoxType sandBoxType, String sandboxName, Long authorId) {
        SandBox sb = sandBoxDao.retrieveNamedSandBox(sandBoxType, sandboxName, authorId);
        return sb == null;
    }

}

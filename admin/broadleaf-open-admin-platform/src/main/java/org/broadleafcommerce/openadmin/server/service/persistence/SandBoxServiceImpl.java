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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sandbox.dao.SandBoxDao;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.SandBoxAction;
import org.broadleafcommerce.openadmin.server.domain.SandBoxActionImpl;
import org.broadleafcommerce.openadmin.server.domain.SandBoxActionType;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItemListener;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

@Service(value = "blSandBoxService")
public class SandBoxServiceImpl implements SandBoxService {
    
    private static final Log LOG = LogFactory.getLog(SandBoxServiceImpl.class);

    @Resource(name="blSandboxItemListeners")
    protected List<SandBoxItemListener> sandboxItemListeners = new ArrayList<SandBoxItemListener>();

    @Resource(name="blSandBoxDao")
    protected SandBoxDao sandBoxDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Override
    public SandBox retrieveSandBoxById(Long sandboxId) {
        return sandBoxDao.retrieve(sandboxId);
    }
    
    @Override
    public List<SandBox> retrieveAllSandBoxes() {
        return sandBoxDao.retrieveAllSandBoxes();
    }

    @Override
    public SandBox retrieveUserSandBox(AdminUser adminUser) {
        SandBox userSandbox;
        if (adminUser.getOverrideSandBox() != null) {
            userSandbox = adminUser.getOverrideSandBox();
        } else {
            userSandbox = retrieveSandBox(adminUser.getLogin(), SandBoxType.USER);
            if (userSandbox == null) {
                userSandbox = createSandBox(adminUser.getLogin(), SandBoxType.USER);
            }
        }

        return userSandbox;
    }

    @Override
    @Transactional("blTransactionManager")
    public void promoteAllSandBoxItems(SandBox fromSandBox, String comment) {
        promoteSelectedItems(fromSandBox, comment, new ArrayList<SandBoxItem>(sandBoxItemDao.retrieveSandBoxItemsForSandbox(fromSandBox.getId())));
    }

    @Override
    @Transactional("blTransactionManager")
    public void promoteSelectedItems(SandBox fromSandBox, String comment, List<SandBoxItem> sandBoxItems) {
        SandBox destinationSandBox = determineNextSandBox(fromSandBox);
        SandBoxAction action = createSandBoxAction(SandBoxActionType.PROMOTE, comment);

        for(SandBoxItem sandBoxItem : sandBoxItems) {
            action.addSandBoxItem(sandBoxItem);

            if (destinationSandBox == null || SandBoxType.PRODUCTION.equals(destinationSandBox)) {
                sandBoxItem.setArchivedFlag(true);
            }
            if (destinationSandBox != null) {
                sandBoxItem.setSandBoxId(destinationSandBox.getId());
            } else {
                sandBoxItem.setSandBoxId(null);
            }
            if (sandBoxItem.getOriginalSandBoxId() == null) {
                sandBoxItem.setOriginalSandBoxId(fromSandBox.getId());
            }
            sandBoxItem.addSandBoxAction(action);

            for (SandBoxItemListener listener : sandboxItemListeners) {
                listener.itemPromoted(sandBoxItem, destinationSandBox);
            }
        }
    }

    @Override
    @Transactional("blTransactionManager")
    public void revertAllSandBoxItems(SandBox originalSandBox, SandBox sandBox) {
         List<SandBoxItem> items = new ArrayList<SandBoxItem>();
         List<SandBoxItem> sandBoxItems = sandBoxItemDao.retrieveSandBoxItemsForSandbox(sandBox.getId());
         for (SandBoxItem item : sandBoxItems) {             
             if (originalSandBox.equals(sandBox) || (item.getOriginalSandBoxId() != null && originalSandBox != null && item.getOriginalSandBoxId().equals(originalSandBox.getId()))) {
                 items.add(item);
             }
         }
        revertSelectedSandBoxItems(sandBox, items);
    }

    @Override
    @Transactional("blTransactionManager")
    public void revertSelectedSandBoxItems(SandBox fromSandBox, List<SandBoxItem> sandBoxItems) {
        for (SandBoxItem item : sandBoxItems) {
            if (item.getArchivedFlag()) {
                throw new IllegalArgumentException("Cannot revert an archived SandBoxItem");
            }
        }

        SandBoxAction action = createSandBoxAction(SandBoxActionType.REVERT, null);

        for(SandBoxItem sandBoxItem : sandBoxItems) {
            action.addSandBoxItem(sandBoxItem);
            for (SandBoxItemListener listener : sandboxItemListeners) {
                listener.itemReverted(sandBoxItem);
            }

            // We're done with this sandBoxItem
            sandBoxItem.setArchivedFlag(true);
            sandBoxItem.addSandBoxAction(action);
        }
    }

    @Override
    @Transactional("blTransactionManager")
    public void rejectAllSandBoxItems(SandBox originalSandBox, SandBox sandBox, String comment) {        
        List<SandBoxItem> items = new ArrayList<SandBoxItem>();
        List<SandBoxItem> currentItems = sandBoxItemDao.retrieveSandBoxItemsForSandbox(sandBox.getId());
        for (SandBoxItem item : currentItems) {
            if (item.getOriginalSandBoxId().equals(originalSandBox.getId())) {
                items.add(item);
            }
        }
        rejectSelectedSandBoxItems(sandBox, comment, items);
    }

    @Override
    @Transactional("blTransactionManager")
    public void rejectSelectedSandBoxItems(SandBox fromSandBox, String comment, List<SandBoxItem> sandBoxItems) {
        for (SandBoxItem item : sandBoxItems) {
            if (item.getOriginalSandBoxId() == null) {
                throw new IllegalArgumentException("Cannot reject a SandBoxItem whose originalSandBox member is null");
            }
        }

        SandBoxAction action = createSandBoxAction(SandBoxActionType.REJECT, comment);

        SandBox originalSandBox = null;
        for(SandBoxItem sandBoxItem : sandBoxItems) {
            action.addSandBoxItem(sandBoxItem);

            if (sandBoxItem.getOriginalSandBoxId() != null) {
                if (originalSandBox != null && ! originalSandBox.getId().equals(sandBoxItem.getOriginalItemId())) {
                    originalSandBox = sandBoxDao.retrieve(sandBoxItem.getOriginalItemId());
                }
            } else {
                originalSandBox = null;
            }
            
            for (SandBoxItemListener listener : sandboxItemListeners) {                
                listener.itemRejected(sandBoxItem, originalSandBox);
            }

            sandBoxItem.addSandBoxAction(action);
            sandBoxItem.setSandBoxId(sandBoxItem.getOriginalSandBoxId());
            sandBoxItem.setOriginalSandBoxId(null);
            
            
        }
    }


    @Override
    public void schedulePromotionForSandBox(SandBox sandBox, Calendar calendar) {

    }

    @Override
    public void schedulePromotionForSandBoxItems(List<SandBoxItem> sandBoxItems, Calendar calendar) {

    }

    public List<SandBoxItemListener> getSandboxItemListeners() {
        return sandboxItemListeners;
    }

    public void setSandboxItemListeners(List<SandBoxItemListener> sandboxItemListeners) {
        this.sandboxItemListeners = sandboxItemListeners;
    }

    protected SandBoxAction createSandBoxAction(SandBoxActionType type, String comment) {
        SandBoxAction action = new SandBoxActionImpl();
        action.setActionType(type);
        action.setComment(comment);
        return action;
    }

    protected SandBox determineNextSandBox(SandBox sandBox) {
        if (SandBoxType.USER.equals(sandBox.getSandBoxType())) {
            return retrieveApprovalSandBox(sandBox);
        } else if (SandBoxType.APPROVAL.equals(sandBox.getSandBoxType())) {
            // null is the production sandbox for a single tenant application
            return null;
        }
        throw new IllegalArgumentException("Unable to determine next sandbox for " + sandBox);
    }

    public SandBox retrieveApprovalSandBox(SandBox sandBox) {
        final String APPROVAL_SANDBOX_NAME = "Approval";
        SandBox approvalSandbox = retrieveSandBox(APPROVAL_SANDBOX_NAME, SandBoxType.APPROVAL);

        // If the approval sandbox doesn't exist, create it.
        if (approvalSandbox == null) {
            approvalSandbox = createSandBox(APPROVAL_SANDBOX_NAME, SandBoxType.APPROVAL);
        }
        
        return approvalSandbox;
    }

    public synchronized SandBox createSandBox(String sandBoxName, SandBoxType sandBoxType) {
        return sandBoxDao.createSandBox(sandBoxName, sandBoxType);
    }

    public SandBox retrieveSandBox(String sandBoxName, SandBoxType sandBoxType) {
        return sandBoxDao.retrieveNamedSandBox(sandBoxType, sandBoxName);
    }
}

/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.content.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.core.content.ContentDaoDataProvider;
import org.broadleafcommerce.core.content.dao.ContentDao;
import org.broadleafcommerce.core.content.dao.ContentDetailsDao;
import org.broadleafcommerce.core.content.domain.Content;
import org.broadleafcommerce.core.content.domain.ContentDetails;
import org.broadleafcommerce.core.content.service.ContentService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

/**
 * @author btaylor
 *
 */
public class ContentServiceTest extends BaseTest {

    @Resource
    private ContentService contentService;

    @Resource
    private ContentDao contentDao;

    @Resource
    private ContentDetailsDao contentDetailsDao;

    private Integer contentId;
    private Integer checkedOutContentId;
    private String readyForApprovalSandbox;

    @Test(groups = {"testContentService"})
    public void testCanary() {
        assert contentService != null;
    }

    @Test(groups = {"testSaveContent"}, dataProvider = "basicContentAndDetail", dataProviderClass = ContentDaoDataProvider.class)
    @Transactional
    @Rollback(false)
    public void testSaveContent(Content content, ContentDetails contentDetails){
        Content contentCreated = contentDao.saveContent(content);
        assert contentCreated.getId() != null;

        contentDetails.setId(contentCreated.getId());
        ContentDetails contentDetailsCreated = contentDetailsDao.save(contentDetails);

        Content contentFromDB = contentDao.readContentById(contentCreated.getId());
        ContentDetails contentDetailsFromDB = contentDetailsDao.readContentDetailsById(contentCreated.getId());

        assert contentFromDB != null;
        assert contentDetailsFromDB != null;

        contentId = contentCreated.getId();
    }

    @Test(groups = {"testCheckoutContentToSandbox"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    @Rollback(false)
    public void testCheckoutContentToSandbox() {
        List<Integer> contentIds = new ArrayList<Integer>();
        contentIds.add(contentId);

        List<Content> newContent = contentService.checkoutContentToSandbox(contentIds, "UserSandBox");

        assert newContent != null && !(newContent.isEmpty());
        assert newContent.get(0).getId() != null;

        checkedOutContentId = newContent.get(0).getId();
    }

    @Test(groups = {"testSubmitContent"}, dependsOnGroups = {"testCheckoutContentToSandbox"})
    @Transactional
    @Rollback(false)
    public void testSubmitContent() {
        assert checkedOutContentId != null;
        assert checkedOutContentId != contentId;

        List<Integer> contentIds = new ArrayList<Integer>();
        contentIds.add(checkedOutContentId);

        contentService.submitContentFromSandbox(contentIds, "UserSandBox", "NumeroUno", "NoteTest");

        List<Content> awaitingApproval = contentDao.readContentAwaitingApproval();
        assert awaitingApproval != null && !awaitingApproval.isEmpty();

        readyForApprovalSandbox = awaitingApproval.get(0).getSandbox();
    }

    @Test(groups = {"testApproveContent"}, dependsOnGroups = {"testSubmitContent"})
    @Transactional
    public void testApproveContent() {
        List<Content> awaitingApproval = contentDao.readContentAwaitingApproval();
        assert awaitingApproval != null && !awaitingApproval.isEmpty();

        List<Integer> contentIds = new ArrayList<Integer>();
        contentIds.add(checkedOutContentId);

        contentService.approveContent(contentIds, readyForApprovalSandbox, "NumeroUno");

        awaitingApproval = contentDao.readContentAwaitingApproval();
        assert awaitingApproval == null || awaitingApproval.isEmpty();
    }

}

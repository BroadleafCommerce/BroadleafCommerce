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
package org.broadleafcommerce.content.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.content.ContentDaoDataProvider;
import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.content.domain.ContentDetails;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.time.SystemTime;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

/**
 * @author btaylor
 *
 */
public class ContentDaoTest extends BaseTest {

    @Resource
    private ContentDao contentDao;

    @Resource
    private ContentDetailsDao contentDetailsDao;

    private Integer contentId;

    @Test(groups = {"testSaveContent"}, dataProvider = "basicContent", dataProviderClass = ContentDaoDataProvider.class)
    @Rollback(false)
    @Transactional
    public void testSaveContent(Content content){
        assert content.getId() == null;
        Content newContent = contentDao.saveContent(content);
        assert newContent.getId() != null;
        assert (newContent.getActiveEndDate() == null && content.getActiveEndDate() == null) || newContent.getActiveEndDate().equals(content.getActiveEndDate());
        assert (newContent.getActiveStartDate() == null && content.getActiveStartDate() == null) || newContent.getActiveStartDate().equals(content.getActiveStartDate());
        assert (newContent.getTitle() == null && content.getTitle() == null) || newContent.getTitle().equals(content.getTitle());
        assert (newContent.getDisplayRule() == null && content.getDisplayRule() == null) || newContent.getDisplayRule().equals(content.getDisplayRule());
        assert (newContent.getSandbox() == null && content.getSandbox() == null) || newContent.getSandbox().equals(content.getSandbox());
        assert (newContent.getContentType() == null && content.getContentType() == null) || newContent.getContentType().equals(content.getContentType());
        contentId = newContent.getId();
    }

    @Test(groups = {"testReadContentById"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentById(){
        Content content = contentDao.readContentById(contentId);
        assert content != null;
        assert content.getId() == contentId;
    }

    @Test(groups = {"testReadContentByVersionSandboxFile"}, dataProvider = "basicContent", dataProviderClass=ContentDaoDataProvider.class, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentByVersionSandboxFile(Content content){
        List<Content> newContents = contentDao.readContentSpecified(content.getSandbox(), content.getContentType(), SystemTime.asDate());
        assert newContents != null && !newContents.isEmpty();
        Content newContent = newContents.get(0);

        assert newContent != null;
        assert newContent.getId() != null;
        assert newContent.getId().equals(contentId);
    }

    @Test(groups = {"testUpdateContent"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testUpdateContent(){
        String title = "/new/file/path";
        Content content = contentDao.readContentById(contentId);
        String oldtitle = content.getTitle();
        content.setTitle(title);
        contentDao.saveContent(content);
        Content newContent = contentDao.readContentById(contentId);
        assert newContent != null;
        assert newContent.getId().equals(content.getId());
        assert !newContent.getTitle().equals(oldtitle);
        assert newContent.getTitle().equals(title);
    }

    @Test(groups = {"testRreadContentByIdsAndSandbox"}, dataProvider = "basicContent", dataProviderClass=ContentDaoDataProvider.class, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentByIdsAndSandbox(Content content) {
        List<Integer> contentIds = new ArrayList<Integer>();
        contentIds.add(contentId);
        List<Content> contentList = contentDao.readContentByIdsAndSandbox(contentIds, content.getSandbox());

        assert contentList != null && !contentList.isEmpty();
    }

    @Test(groups = {"testReadContentBySandbox"}, dataProvider = "basicContent", dataProviderClass=ContentDaoDataProvider.class, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentBySandbox(Content content) {
        List<Content> newContents = contentDao.readContentBySandbox(content.getSandbox());
        assert newContents != null && !newContents.isEmpty();
        Content newContent = newContents.get(0);

        assert newContent != null;
        assert newContent.getId() != null;
        assert newContent.getId().equals(contentId);
    }


    @Test(groups = {"testReadContentBySandbox"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentAwaitingApproval() {
        List<Content> newContents = contentDao.readContentAwaitingApproval();
        assert newContents != null && !newContents.isEmpty();
        Content newContent = newContents.get(0);

        assert newContent != null;
        assert newContent.getApprovedBy() == null;
        assert newContent.getApprovedDate() == null;
    }

    @Test(groups = {"testReadContentBySandboxAndType"}, dataProvider = "basicContent", dataProviderClass=ContentDaoDataProvider.class, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadContentBySandboxAndType(Content content) {
        List<Content> newContents = contentDao.readContentBySandboxAndType(content.getSandbox(), content.getContentType());
        assert newContents != null && !newContents.isEmpty();
        Content newContent = newContents.get(0);

        assert newContent != null;
        assert newContent.getSandbox().equals(content.getSandbox());
        assert newContent.getContentType().equals(content.getContentType());
    }

    @Test(groups = {"testDeleteContent"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testDeleteContent() {
        Content content = contentDao.readContentById(contentId);
        contentDao.delete(content);

        Content newContent = contentDao.readContentById(contentId);

        assert newContent == null;

        ContentDetails contentDetails = contentDetailsDao.readContentDetailsById(contentId);
        assert contentDetails == null;
    }

    @Test(groups = {"testDeleteContent"}, dependsOnGroups = {"testSaveContent"})
    @Transactional
    public void testReadStagedContent() {
        Content content = contentDao.readContentById(contentId);
        content.setSandbox(null);
        contentDao.saveContent(content);

        List<Content> newContents = contentDao.readStagedContent();

        assert newContents != null && !newContents.isEmpty();
    }
}

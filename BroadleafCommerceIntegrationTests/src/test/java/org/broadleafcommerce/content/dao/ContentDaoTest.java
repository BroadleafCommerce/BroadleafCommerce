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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.content.ContentDaoDataProvider;
import org.broadleafcommerce.content.domain.Content;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.util.DateUtil;
import org.testng.annotations.Test;

/**
 * @author btaylor
 *
 */
public class ContentDaoTest extends BaseTest {

	@Resource
	private ContentDao contentDao;
	
	private Long contentId;
	
	@Test(groups = {"testSaveContent"}, dataProvider = "basicContent", dataProviderClass = ContentDaoDataProvider.class)
	public void testSaveContent(Content content){
		assert content.getId() == null;
		Content newContent = contentDao.saveContent(content);
		assert newContent.getId() != null;
		assert newContent.getActiveEndDate() == content.getActiveEndDate();
		assert newContent.getActiveStartDate() == content.getActiveStartDate();
		assert newContent.getFilePathName() == content.getFilePathName();
		assert newContent.getDisplayRule() == content.getDisplayRule();
		assert newContent.getSandbox() == content.getSandbox();
		assert newContent.getContentType() == content.getContentType();
		contentId = content.getId();
	}
	
	@Test(groups = {"testReadContentById"}, dependsOnGroups = {"testSaveContent"})
	public void testReadContentById(){
		Content content = contentDao.readContentById(contentId);
		assert content != null;
		assert content.getId() == contentId;
	}
	
	@Test(groups = {"testReadContentByVersionSandboxFile"}, dataProvider = "basicContent", dataProviderClass=ContentDaoDataProvider.class, dependsOnGroups = {"testSaveContent"})
	public void testReadContentByVersionSandboxFile(Content content){
		List<Content> newContents = contentDao.readContentSpecified(content.getSandbox(), content.getContentType(), new Date(DateUtil.getNow()));
		assert newContents != null;
		Content newContent = newContents.get(0);
		
		assert newContent != null;
		assert newContent.getId() != null;
		assert newContent.getId() == contentId;
	}
	
	@Test(groups = {"testUpdateContent"}, dependsOnGroups = {"testSaveContent"})
	public void testUpdateContent(){
		String newFilePath = "/new/file/path";
		Content content = contentDao.readContentById(contentId);
		content.setFilePathName(newFilePath);
		contentDao.saveContent(content);
		Content newContent = contentDao.readContentById(contentId);
		assert newContent.getId() == content.getId();
		assert newContent.getFilePathName() != content.getFilePathName();
		assert newContent.getFilePathName() == newFilePath;
	}
	
}

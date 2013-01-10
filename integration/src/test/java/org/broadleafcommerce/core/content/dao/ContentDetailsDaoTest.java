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

package org.broadleafcommerce.core.content.dao;

import javax.annotation.Resource;

import org.broadleafcommerce.core.content.ContentDetailsDaoProvider;
import org.broadleafcommerce.core.content.dao.ContentDetailsDao;
import org.broadleafcommerce.core.content.domain.ContentDetails;
import org.broadleafcommerce.test.BaseTest;
import org.testng.annotations.Test;

/**
 * @author btaylor
 *
 */
public class ContentDetailsDaoTest extends BaseTest {

    @Resource
    private ContentDetailsDao contentDetailsDao;

    private Integer contentDetailsId;

    @Test(groups = {"testSaveContentDetails"}, dataProvider = "basicContentDetails", dataProviderClass = ContentDetailsDaoProvider.class, dependsOnGroups = {"testSaveContent"})
    public void testSaveContentDetails(ContentDetails contentDetails){
        ContentDetails newContentDetails = contentDetailsDao.save(contentDetails);
        assert newContentDetails != null;
        assert newContentDetails.getId() != null;
        contentDetailsId = newContentDetails.getId();
    }

    @Test(groups = {"testReadContentDetailsById"}, dependsOnGroups = {"testSaveContentDetails"})
    public void testReadContentDetailsById(){
        ContentDetails contentDetails = contentDetailsDao.readContentDetailsById(contentDetailsId);
        assert contentDetails != null;
        assert contentDetails.getId() != null;
        assert contentDetails.getXmlContent() != null;
    }
}

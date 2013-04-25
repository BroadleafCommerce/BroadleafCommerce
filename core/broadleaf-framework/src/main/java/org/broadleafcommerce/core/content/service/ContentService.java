/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.content.service;

import org.broadleafcommerce.core.content.domain.Content;
import org.broadleafcommerce.core.content.domain.ContentDetails;
import org.broadleafcommerce.core.content.domain.ContentPageInfo;
import org.broadleafcommerce.core.content.domain.ContentXmlData;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author btaylor
 *
 */
public interface ContentService {

    public Content findContentById(Integer id);

    public ContentDetails findContentDetailsById(Integer id);

    public String findContentDetailsXmlById(Integer id);

    public Map<String, Object> findContentDetailsMapById(Integer id) throws Exception;

    public List<ContentXmlData> findContentDetailsListById(Integer id) throws Exception;

    public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters);

    public List<ContentDetails> findContentDetails(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate);

    public List<Content> findContent(String sandbox, String contentType, Map<String, Object> mvelParameters, Date displayDate);

    public String renderedContentDetails(String styleSheet, List<ContentDetails> contentDetails) throws Exception;

    public String renderedContentDetails(String styleSheetString, List<ContentDetails> contentDetails, int rowCount) throws Exception;

    public String renderedContent(String styleSheetString, List<Content> contentList, int rowCount) throws Exception;
    
    public List<Content> checkoutContentToSandbox(List<Integer> contentIds, String sandboxName);

    public void submitContentFromSandbox(List<Integer> contentIds, String sandboxName, String username, String note);

    public void approveContent(List<Integer> contentIds,  String sandboxName, String username);

    public void removeContentFromSandbox(List<Integer> contentIds, String sandbox);

    public void rejectContent(List<Integer> contentIds, String sandbox, String username);

    public List<Content> readContentForSandbox(String sandbox);

    public List<Content> readContentForSandboxAndType(String sandbox, String contentType);

    public List<Content> readContentAwaitingApproval();

    public Content saveContent(Content content, List<ContentXmlData> details);

    public List<ContentPageInfo> readAllContentPageInfos();

    public Map<Integer, String> constructParentUrlMap();
}

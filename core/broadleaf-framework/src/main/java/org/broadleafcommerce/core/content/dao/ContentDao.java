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

package org.broadleafcommerce.core.content.dao;

import java.util.Date;
import java.util.List;

import org.broadleafcommerce.core.content.domain.Content;

/**
* DOCUMENT ME!
*
* @author btaylor
 */
public interface ContentDao {
    public void delete(Content content);

    public void delete(List<Content> contentList);

    public List<Content> readContentAwaitingApproval();

    public Content readContentById(Integer id);

    public List<Content> readContentByIdsAndSandbox(List<Integer> ids, String sandbox);

    public List<Content> readContentBySandbox(String sandbox);

    public List<Content> readContentBySandboxAndType(String sandbox, String contentType);

    public List<Content> readContentSpecified(String sandbox, String contentType, Date date);

    public Content saveContent(Content content);

    public List<Content> saveContent(List<Content> contentList);

    public List<Content> readStagedContent();

    public List<Content> readAllContent();

}

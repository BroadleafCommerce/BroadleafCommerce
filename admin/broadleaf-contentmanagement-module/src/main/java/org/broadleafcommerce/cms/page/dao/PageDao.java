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

package org.broadleafcommerce.cms.page.dao;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;

import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface PageDao {

    public Page readPageById(Long id);

    public PageTemplate readPageTemplateById(Long id);

    public Map<String, PageField> readPageFieldsByPage(Page page);

    public Page updatePage(Page page);

    public void delete(Page page);

    public Page addPage(Page clonedPage);

    public List<Page> findPageByURI(SandBox sandBox, Locale locale, String uri);

    public void detachPage(Page page);
}

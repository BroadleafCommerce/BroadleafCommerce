/*
 * Copyright 2008-20011 the original author or authors.
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
package org.broadleafcommerce.cms.structure.dao;

import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public interface StructuredContentDao {

    public StructuredContent findStructuredContentById(Long contentId);

    public StructuredContentType findStructuredContentTypeById(Long contentTypeId);

    public List<StructuredContentType> retrieveAllStructuredContentTypes();

    public Map<String,StructuredContentField> readFieldsForStructuredContentItem(StructuredContent sc);

    public StructuredContent addOrUpdateContentItem(StructuredContent content, boolean clearLevel1Cache);

    public void delete(StructuredContent content);

    public List<StructuredContent> findActiveStructuredContentByType(SandBox sandBox, StructuredContentType type, Locale locale);

    public List<StructuredContent> findActiveStructuredContentByNameAndType(SandBox sandBox, StructuredContentType type, String name, Locale locale);

    public StructuredContentType findStructuredContentTypeByName(String name);
}

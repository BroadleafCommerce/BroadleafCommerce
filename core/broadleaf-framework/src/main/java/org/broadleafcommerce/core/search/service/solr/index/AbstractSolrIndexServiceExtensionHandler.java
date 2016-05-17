/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Implementors of the SolrIndexServiceExtensionHandler interface should extend this class so that if 
 * additional extension points are added which they don't care about, their code will not need to be
 * modified.
 * 
 * @author bpolster, Phillip Verheyden (phillipuniverse)
 */                                      
public abstract class AbstractSolrIndexServiceExtensionHandler extends AbstractExtensionHandler
        implements SolrIndexServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType addPropertyValues(Indexable indexable, Field field, FieldType fieldType,
            Map<String, Object> values, String propertyName, List<Locale> locales) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType attachAdditionalBasicFields(Indexable indexable, SolrInputDocument document, SolrHelperService shs) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override public ExtensionResultStatusType populateDocumentForIndexField(SolrInputDocument document, IndexField field, FieldType fieldType, Map<String, Object> propertyValues) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override public ExtensionResultStatusType attachAdditionalDocumentFields(Indexable indexable, SolrInputDocument document) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyBuiltDocuments(Collection<SolrInputDocument> documents, List<? extends Indexable> products, List<IndexField> fields, List<Locale> locales) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType startBatchEvent(List<? extends Indexable> products) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType endBatchEvent(List<? extends Indexable> products) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType getIndexableId(Indexable indexable, Long[] returnContainer) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType getCategoryId(Long category, Long[] returnContainer) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}

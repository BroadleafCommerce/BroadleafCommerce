/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Extension handler for indexing operations in Solr
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface SolrIndexServiceExtensionHandler extends ExtensionHandler {

    /**
     * In certain scenarios, the requested category id might not be the one that should be used in Solr.
     * If this method returns {@link ExtensionResultStatusType#HANDLED}, the value placed in the 0th element
     * in the returnContainer should be used.
     *
     * @param category
     * @param returnContainer
     * @return the extension result status type
     */
    public ExtensionResultStatusType getCategoryId(Long category, Long[] returnContainer);

    /**
     * In certain scenarios, the requested product id might not be the one that should be used in Solr.
     * If this method returns {@link ExtensionResultStatusType#HANDLED}, the value placed in the 0th element
     * in the returnContainer should be used.
     * 
     * @param product
     * @param returnContainer
     * @return the extension result status type
     */
    public ExtensionResultStatusType getIndexableId(Indexable indexable, Long[] returnContainer);
    
    public ExtensionResultStatusType modifyBuiltDocuments(Collection<SolrInputDocument> documents, List<? extends Indexable> items, List<Field> fields, List<Locale> locales);

    /**
     * Perform actions at the start of a batch to improve performance of Solr search for the list of batch products.  
     * For example we want to get, in bulk, the SkuPriceData for each product and save these in memory by default.
     * 
     * @param products
     * @return
     */
    public ExtensionResultStatusType startBatchEvent(List<? extends Indexable> indexables);

    /**
     * Perform actions to end a batch event, such as closing any Contexts that have been previously created.
     * 
     * @return
     */
    public ExtensionResultStatusType endBatchEvent();

    /**
     * Given the input field, populates the values array with the fields needed for the 
     * passed in field.   
     * 
     * For example, a handler might create multiple fields for the given passed in field.
     * @param product
     * @param field
     * @param values
     * @param propertyName
     * @param locales
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public ExtensionResultStatusType addPropertyValues(Indexable indexable, Field field, FieldType fieldType,
            Map<String, Object> values, String propertyName, List<Locale> locales)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    /**
     * Allows the extension additional fields to the document that are not configured via the DB.
     */
    public ExtensionResultStatusType attachAdditionalBasicFields(Indexable indexable, SolrInputDocument document, SolrHelperService shs);
    
}

/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Extension handler for indexing operations in Solr. Implementors should extend from {@link AbstractSolrIndexServiceExtensionHandler}
 * to protect from API changes to this interface
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
    
    public ExtensionResultStatusType modifyBuiltDocuments(Collection<SolrInputDocument> documents, List<? extends Indexable> items, List<IndexField> fields, List<Locale> locales);

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
    public ExtensionResultStatusType endBatchEvent(List<? extends Indexable> indexables);

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

    /**
     * This is used to populate any fields for the given parameters as well as adding any property names to the added properties list.
     *
     * @param document the document we are populating
     * @param field the field we are populating the document with
     * @param fieldType the field type of the field
     * @param propertyValues the property values for the given Field
     * @param addedProperties the properties that have been added to this document so far
     * @return the result of this handler, if NOT_HANDLED, no fields were populated
     */
    public ExtensionResultStatusType populateDocumentForIndexField(SolrInputDocument document, IndexField field, FieldType fieldType, Map<String, Object> propertyValues);

    public ExtensionResultStatusType attachAdditionalDocumentFields(Indexable indexable, SolrInputDocument document);

    /**
     * This extension point allows other modules to contribute child documents to this document.
     *
     * @param indexable
     * @param document
     * @param fields
     * @param locales
     * @return either {@link ExtensionResultStatusType#NOT_HANDLED} or {@link ExtensionResultStatusType#HANDLED_CONTINUE}.
     */
    public ExtensionResultStatusType attachChildDocuments(Indexable indexable, SolrInputDocument document, List<IndexField> fields, List<Locale> locales);
}

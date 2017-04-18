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
    public ExtensionResultStatusType attachChildDocuments(Indexable indexable, SolrInputDocument document, List<IndexField> fields, List<Locale> locales) {
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

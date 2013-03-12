/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.cto.FilterCriterionProviders;
import org.w3c.dom.DOMException;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/**
 * 
 * @author jfischer
 *
 */
public interface RecordHelper {

    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties) throws ClassNotFoundException;

    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, FilterCriterionProviders criterionProviders) throws ClassNotFoundException;

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException;

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException;
    
    public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException;
    
    public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException;
    
    public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException;

    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException;

    /**
     * Returns the count criteria representation that should be used to count the result set. This is an advanced use case
     * and should only be used when you need to have explicit control over the Hibernate criteria that can be created from
     * the result of this method (like if you are using table aliases in the {@link BaseCtoConverter#getFilterCriterionProviders()}).
     * @param persistencePackage
     * @param cto
     * @param ctoConverter
     * @return
     * @throws ClassNotFoundException
     */
    public PersistentEntityCriteria getCountCriteria(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException;

    public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException;
    
    public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedProperties) throws NumberFormatException;
    
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException;
    
    public FieldManager getFieldManager();

    public PersistenceModule getCompatibleModule(OperationType operationType);
    
    /**
     * @return the date formatter suitable for converting Date, Calendar and Timestamp to their String representations
     */
    public SimpleDateFormat getDateFormatter();

    /**
     * @return the decimal formatter suitable for converting Double and BigDecimal to their String representations
     */
    public DecimalFormat getDecimalFormatter();

}

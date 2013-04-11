package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public interface PersistenceProvider {

    void populateValue(Serializable instance, Boolean setId, FieldManager fieldManager, Property property,
                       BasicFieldMetadata metadata,
                       Class<?> returnType, String value, PersistenceManager persistenceManager,
                       DataFormatProvider dataFormatProvider) throws PersistenceException;

    void extractValue(List<Property> props, FieldManager fieldManager, MVELToDataWrapperTranslator translator,
                      ObjectMapper mapper,
                      BasicFieldMetadata metadata, Object value, String strVal, Property propertyItem,
                      String displayVal,
                      PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) throws
            PersistenceException;

    boolean canHandlePersistence(Object instance, BasicFieldMetadata metadata);

    boolean canHandleFilterMapping(BasicFieldMetadata metadata);

    boolean canHandleFilterProperties(Entity entity, Map<String, FieldMetadata> unfilteredProperties);

    void addFilterMapping(PersistencePerspective persistencePerspective, CriteriaTransferObject cto,
                          String ceilingEntityFullyQualifiedClassname, Map<String,
            FieldMetadata> mergedProperties, BaseCtoConverter ctoConverter, String propertyName,
                          FieldManager fieldManager);

    void filterProperties(Entity entity, Map<String, FieldMetadata> mergedProperties);

}

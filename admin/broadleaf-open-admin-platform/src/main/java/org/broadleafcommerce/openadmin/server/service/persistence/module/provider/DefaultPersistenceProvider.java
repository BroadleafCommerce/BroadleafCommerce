package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultPersistenceProvider")
@Scope("prototype")
public class DefaultPersistenceProvider extends AbstractPersistenceProvider {

    @Override
    public boolean canHandlePersistence(Object instance, BasicFieldMetadata metadata) {
        return true;
    }

    @Override
    public boolean canHandleFilterMapping(BasicFieldMetadata metadata) {
        return false;
    }

    @Override
    public boolean canHandleFilterProperties(Entity entity, Map<String, FieldMetadata> unfilteredProperties) {
        return false;
    }

    public void populateValue(Serializable instance, Boolean setId, FieldManager fieldManager, Property property,
                              BasicFieldMetadata metadata, Class<?> returnType, String value, PersistenceManager persistenceManager,
                              DataFormatProvider dataFormatProvider) throws PersistenceException {
        try {
            fieldManager.setFieldValue(instance, property.getName(), value);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void extractValue(List<Property> props, FieldManager fieldManager, MVELToDataWrapperTranslator translator, ObjectMapper mapper, BasicFieldMetadata metadata, Object value, String strVal, Property propertyItem, String displayVal, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) throws PersistenceException {
        if (value != null) {
            strVal = value.toString();
            propertyItem.setValue(strVal);
            propertyItem.setDisplayValue(displayVal);
        }
    }

    @Override
    public void addFilterMapping(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String
            ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, BaseCtoConverter
                                             ctoConverter, String propertyName, FieldManager fieldManager) {
        //do nothing
    }

    @Override
    public void filterProperties(Entity entity, Map<String, FieldMetadata> mergedProperties) {
        //do nothing
    }
}

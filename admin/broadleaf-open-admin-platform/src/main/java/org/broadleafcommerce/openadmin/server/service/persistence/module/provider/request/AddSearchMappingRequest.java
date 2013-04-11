package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

import java.util.Map;

/**
 * Contains the requested ctoConverter, cto and support classes.
 *
 * @author Jeff Fischer
 */
public class AddSearchMappingRequest {

    private final PersistencePerspective persistencePerspective;
    private final CriteriaTransferObject requestedCto;
    private final String ceilingEntityFullyQualifiedClassname;
    private final Map<String, FieldMetadata> mergedProperties;
    private final BaseCtoConverter requestedCtoConverter;
    private final String propertyName;
    private final FieldManager fieldManager;

    public AddSearchMappingRequest(PersistencePerspective persistencePerspective, CriteriaTransferObject
            requestedCto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties,
                                   BaseCtoConverter requestedCtoConverter, String propertyName, FieldManager fieldManager) {
        this.persistencePerspective = persistencePerspective;
        this.requestedCto = requestedCto;
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
        this.mergedProperties = mergedProperties;
        this.requestedCtoConverter = requestedCtoConverter;
        this.propertyName = propertyName;
        this.fieldManager = fieldManager;
    }

    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }

    public CriteriaTransferObject getRequestedCto() {
        return requestedCto;
    }

    public String getCeilingEntityFullyQualifiedClassname() {
        return ceilingEntityFullyQualifiedClassname;
    }

    public Map<String, FieldMetadata> getMergedProperties() {
        return mergedProperties;
    }

    public BaseCtoConverter getRequestedCtoConverter() {
        return requestedCtoConverter;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }
}

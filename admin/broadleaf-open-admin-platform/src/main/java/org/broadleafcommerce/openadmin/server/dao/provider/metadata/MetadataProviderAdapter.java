package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;

import java.lang.reflect.Field;

/**
 * @author Jeff Fischer
 */
public class MetadataProviderAdapter extends AbstractMetadataProvider {

    @Override
    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        //do nothing
    }

    @Override
    public boolean canHandleField(Field field) {
        return false;
    }

    @Override
    public boolean canHandleAnnotationOverride(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey) {
        return false;
    }

    @Override
    public void overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest) {
        //do nothing
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //do nothing
    }

    @Override
    public void addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest) {
        //do nothing
    }
}

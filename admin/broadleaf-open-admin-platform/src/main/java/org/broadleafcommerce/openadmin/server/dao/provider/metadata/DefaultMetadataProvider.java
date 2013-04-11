package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultMetadataProvider")
@Scope("prototype")
public class DefaultMetadataProvider extends MetadataProviderAdapter {

    private static final Log LOG = LogFactory.getLog(DefaultMetadataProvider.class);

    @Override
    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.setName(addMetadataRequest.getRequestedField().getName());
        metadata.setExcluded(false);
        addMetadataRequest.getRequestedMetadata().put(addMetadataRequest.getRequestedField().getName(), metadata);
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), info);
    }

    @Override
    public boolean canHandleField(Field field) {
        return true;
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        //override any and all exclusions derived from xml
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                Boolean excluded = localMetadata.getExcluded();
                for (String key : overrideViaXmlRequest.getRequestedMetadata().keySet()) {
                    String testKey = overrideViaXmlRequest.getPrefix() + key;
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("setExclusionsBasedOnParents:Excluding " + key + "because an override annotation declared "+ testKey + " to be excluded");
                        }
                        metadata.setExcluded(true);
                        continue;
                    }
                    if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded != null && !excluded) {
                        FieldMetadata metadata = overrideViaXmlRequest.getRequestedMetadata().get(key);
                        if (!overrideViaXmlRequest.getParentExcluded()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("setExclusionsBasedOnParents:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                            }
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

}

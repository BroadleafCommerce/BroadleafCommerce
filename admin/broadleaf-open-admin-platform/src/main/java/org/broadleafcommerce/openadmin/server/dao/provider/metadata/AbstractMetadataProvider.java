package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;

import javax.annotation.Resource;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public abstract class AbstractMetadataProvider implements MetadataProvider {

    protected Map<String, Map<String, FieldMetadataOverride>> metadataOverrides;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blMetadataOverrides")
    public void setMetadataOverrides(Map metadataOverrides) {
        try {
            this.metadataOverrides = metadataOverrides;
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    "Unable to assign metadataOverrides. You are likely using an obsolete spring application context " +
                    "configuration for this value. Please utilize the xmlns:mo=\"http://schema.broadleafcommerce.org/mo\" namespace " +
                    "and http://schema.broadleafcommerce.org/mo http://schema.broadleafcommerce.org/mo/mo.xsd schemaLocation " +
                    "in the xml schema config for your app context. This will allow you to use the appropriate <mo:override> element to configure your overrides.", e);
        }
    }

    protected void setClassOwnership(Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes, FieldInfo field) {
        FieldMetadata metadata = attributes.get(field.getName());
        if (metadata != null) {
            AdminPresentationClass adminPresentationClass;
            if (parentClass != null) {
                metadata.setOwningClass(parentClass.getName());
                adminPresentationClass = parentClass.getAnnotation(AdminPresentationClass.class);
            } else {
                adminPresentationClass = targetClass.getAnnotation(AdminPresentationClass.class);
            }
            if (adminPresentationClass != null) {
                String friendlyName = adminPresentationClass.friendlyName();
                if (!StringUtils.isEmpty(friendlyName) && StringUtils.isEmpty(metadata.getOwningClassFriendlyName())) {
                    metadata.setOwningClassFriendlyName(friendlyName);
                }
            }
        }
    }

    protected FieldInfo buildFieldInfo(Field field) {
        FieldInfo info = new FieldInfo();
        info.setName(field.getName());
        info.setGenericType(field.getGenericType());
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (manyToMany != null) {
            info.setManyToManyMappedBy(manyToMany.mappedBy());
            info.setManyToManyTargetEntity(manyToMany.targetEntity().getName());
        }
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            info.setOneToManyMappedBy(oneToMany.mappedBy());
            info.setOneToManyTargetEntity(oneToMany.targetEntity().getName());
        }
        return info;
    }

    protected Map<String, FieldMetadataOverride> getTargetedOverride(String configurationKey, String ceilingEntityFullyQualifiedClassname) {
        if (metadataOverrides != null && (configurationKey != null || ceilingEntityFullyQualifiedClassname != null)) {
            return metadataOverrides.containsKey(configurationKey)?metadataOverrides.get(configurationKey):metadataOverrides.get(ceilingEntityFullyQualifiedClassname);
        }
        return null;
    }

}

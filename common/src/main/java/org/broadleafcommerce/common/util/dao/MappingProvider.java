package org.broadleafcommerce.common.util.dao;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.mapping.PersistentClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jeff Fischer
 */
public class MappingProvider implements SessionFactoryBuilderFactory {

    private static final Map<String, MetadataImplementor> metadataMap = new ConcurrentHashMap<>();

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadata, SessionFactoryBuilderImplementor defaultBuilder) {
        String key = metadata.getUUID().toString();
        if (!metadataMap.containsKey(key)) {
            metadataMap.put(key, metadata);
        }
        return defaultBuilder;
    }

    public static PersistentClass getMapping(String entityClass) {
        return null;
    }

}

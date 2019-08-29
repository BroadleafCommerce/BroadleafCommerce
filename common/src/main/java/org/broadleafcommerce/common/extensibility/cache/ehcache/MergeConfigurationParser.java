/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.cache.ehcache;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.ConfigurationBuilder.newConfigurationBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;
import static org.ehcache.xml.XmlConfiguration.getClassForName;

import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.core.internal.util.ClassLoading;
import org.ehcache.xml.ConfigurationParser;
import org.ehcache.xml.ResourceConfigurationParser;
import org.ehcache.xml.ServiceCreationConfigurationParser;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.CacheDefinition;
import org.ehcache.xml.model.CacheTemplate;
import org.ehcache.xml.model.ConfigType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
// TODO: Determine if this is required...  First thought is that it is not.
public class MergeConfigurationParser extends ConfigurationParser {

    protected static final String SCHEMA = "schema";
    protected static final String SERVICE_CREATION_CONFIGURATION_PARSER = "serviceCreationConfigurationParser";
    protected static final String RESOURCE_CONFIGURATION_PARSER = "resourceConfigurationParser";
    protected static final String CORE_SCHEMA_ROOT_ELEMENT = "CORE_SCHEMA_ROOT_ELEMENT";
    protected static final String CORE_SCHEMA_NAMESPACE = "CORE_SCHEMA_NAMESPACE";

    protected static final String SUBSTITUTE_SYSTEM_PROPERTIES = "substituteSystemProperties";
    protected static final String PARSE_SERVICE_CONFIGURATIONS = "parseServiceConfigurations";
    protected static final String PARSE_SERVICE_CREATION_CONFIGURATION = "parseServiceCreationConfiguration";

    public MergeConfigurationParser() throws IOException, SAXException, JAXBException, ParserConfigurationException {
        super();
    }

    public XmlConfigurationWrapper parseConfiguration(InputStream inputStream, ClassLoader classLoader, Map<String, ClassLoader> cacheClassLoaders)
            throws IOException, SAXException, JAXBException, ParserConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ConfigType configType = parseXml(inputStream);

        ConfigurationBuilder managerBuilder = newConfigurationBuilder().withClassLoader(classLoader);
        managerBuilder = parseServiceCreationConfiguration(configType, classLoader, managerBuilder);

        for (CacheDefinition cacheDefinition : getCacheElements(configType)) {
            String alias = cacheDefinition.id();
            if(managerBuilder.containsCache(alias)) {
                throw new XmlConfigurationException("Two caches defined with the same alias: " + alias);
            }

            ClassLoader cacheClassLoader = cacheClassLoaders.get(alias);
            boolean classLoaderConfigured = false;
            if (cacheClassLoader != null) {
                classLoaderConfigured = true;
            }

            if (cacheClassLoader == null) {
                if (classLoader != null) {
                    cacheClassLoader = classLoader;
                } else {
                    cacheClassLoader = ClassLoading.getDefaultClassLoader();
                }
            }

            Class<?> keyType = getClassForName(cacheDefinition.keyType(), cacheClassLoader);
            Class<?> valueType = getClassForName(cacheDefinition.valueType(), cacheClassLoader);

            ResourcePools resourcePools = getResourceConfigurationParser().parseResourceConfiguration(cacheDefinition, newResourcePoolsBuilder());

            CacheConfigurationBuilder<?, ?> cacheBuilder = newCacheConfigurationBuilder(keyType, valueType, resourcePools);
            if (classLoaderConfigured) {
                cacheBuilder = cacheBuilder.withClassLoader(cacheClassLoader);
            }

            cacheBuilder = invokeParseServiceConfigurations(cacheBuilder, cacheClassLoader, cacheDefinition);
            managerBuilder = managerBuilder.addCache(alias, cacheBuilder.build());
        }

        Map<String, CacheTemplate> templates = getTemplates(configType);

        return new XmlConfigurationWrapper(managerBuilder.build(), templates);
    }

    public ConfigType parseXml(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException, JAXBException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setSchema(getSchema());

        DocumentBuilder domBuilder = factory.newDocumentBuilder();
        domBuilder.setErrorHandler(new FatalErrorHandler());

        // BLC Override to parse an Input Stream instead of a URI
        Document document = domBuilder.parse(inputStream);
        // End BLC Override

        Element dom = document.getDocumentElement();

        invokeSubstituteSystemProperties(dom);

        if (!getCoreSchemaRootElement().equals(dom.getLocalName()) || !getCoreSchemaNamespace().equals(dom.getNamespaceURI())) {
            throw new XmlConfigurationException("Expecting {" + getCoreSchemaNamespace() + "}" + getCoreSchemaRootElement()
                    + " element; found {" + dom.getNamespaceURI() + "}" + dom.getLocalName());
        }

        Class<ConfigType> configTypeClass = ConfigType.class;
        Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
        return unmarshaller.unmarshal(dom, configTypeClass).getValue();
    }

    protected Schema getSchema() throws NoSuchFieldException, IllegalAccessException {
        return (Schema) getPrivateSuperField(SCHEMA);
    }

    protected String getCoreSchemaRootElement() throws NoSuchFieldException, IllegalAccessException {
        return (String) getPrivateSuperField(CORE_SCHEMA_ROOT_ELEMENT);
    }

    protected String getCoreSchemaNamespace() throws NoSuchFieldException, IllegalAccessException {
        return (String) getPrivateSuperField(CORE_SCHEMA_NAMESPACE);
    }

    protected JAXBContext getJaxbContext() throws NoSuchFieldException, IllegalAccessException {
        return (JAXBContext) getPrivateSuperField("jaxbContext");
    }

    protected ServiceCreationConfigurationParser getServiceCreationConfigurationParser() throws NoSuchFieldException, IllegalAccessException {
        return (ServiceCreationConfigurationParser) getPrivateSuperField(SERVICE_CREATION_CONFIGURATION_PARSER);
    }

    protected ResourceConfigurationParser getResourceConfigurationParser() throws NoSuchFieldException, IllegalAccessException {
        return (ResourceConfigurationParser) getPrivateSuperField(RESOURCE_CONFIGURATION_PARSER);
    }

    protected Object getPrivateSuperField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(this);
    }

    protected void invokeSubstituteSystemProperties(Element dom) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        invokePrivateSuperMethod(SUBSTITUTE_SYSTEM_PROPERTIES, dom);
    }

    protected CacheConfigurationBuilder invokeParseServiceConfigurations(CacheConfigurationBuilder cacheBuilder, ClassLoader cacheClassLoader, CacheDefinition cacheDefinition) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (CacheConfigurationBuilder) invokePrivateSuperMethod(PARSE_SERVICE_CONFIGURATIONS, cacheBuilder, cacheClassLoader, cacheDefinition);
    }

    protected ConfigurationBuilder parseServiceCreationConfiguration(ConfigType configType, ClassLoader classLoader, ConfigurationBuilder managerBuilder) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ResourceConfigurationParser resourceConfigurationParser = getResourceConfigurationParser();
        Method method = resourceConfigurationParser.getClass().getDeclaredMethod(PARSE_SERVICE_CREATION_CONFIGURATION, ConfigType.class, ClassLoader.class, ConfigurationBuilder.class);
        method.setAccessible(true);
        return (ConfigurationBuilder) method.invoke(resourceConfigurationParser, configType, classLoader, managerBuilder);
    }

    protected Object invokePrivateSuperMethod(String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?>[] argTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        // TODO doesn't work because of subclasses
        Method method = getClass().getSuperclass().getDeclaredMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(this, args);
    }

    static class FatalErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
}

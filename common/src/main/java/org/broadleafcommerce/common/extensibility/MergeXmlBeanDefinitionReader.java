/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.extensibility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows XML beans to be merged together with existing bean definitions rather than always doing a wholesale replacement
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class MergeXmlBeanDefinitionReader extends XmlBeanDefinitionReader {

    public MergeXmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        setDocumentReaderClass(MergeBeanDefinitionDocumentReader.class);
    }
    
    protected boolean isXMLBean(BeanDefinition beanDefinition) {
        return BeanUtil.isXMLBean(beanDefinition);
    }
    
    public static class BeanUtil {
        public static boolean isXMLBean(BeanDefinition beanDefinition) {
            return beanDefinition instanceof GenericBeanDefinition
                    && ((GenericBeanDefinition) beanDefinition).getResource() != null
                    && ((GenericBeanDefinition) beanDefinition).getResource().getFilename().endsWith(".xml");
        } 
    }
    
       
    /**
     * When beans are discovered using this reader this will take the previous definition and merge it with the new definition
     * using {@link AbstractBeanDefinition#overrideFrom(BeanDefinition)}. Useful for defining a default set of properties, constructor
     * args, etc in 1 place and only overriding pieces of it when reading later files.
     * 
     * @author Phillip Verheyden (phillipuniverse)
     */
    public static class MergeBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {

        private static final Log LOG = LogFactory.getLog(MergeBeanDefinitionDocumentReader.class);
        
        /**
         * This is very similar to the parent method except that instead of always registering the new bean definition on top of
         * the old one, this takes the originally-def
         * 
         * 
         * SPRING-UPGRADE-CHECK
         */
        @Override
        protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
            String beanId = getBeanId(ele);
            
            if (!StringUtils.hasText(beanId)) {
                LOG.debug("No explicit bean id defined, skipping any attempts at bean merging");
                super.processBeanDefinition(ele, delegate);
                return;
            }
            
            BeanDefinition existingDefinition = null;
            try {
                existingDefinition = getReaderContext().getRegistry().getBeanDefinition(beanId);
            } catch (NoSuchBeanDefinitionException e) {
                // do nothing, keep going with null for the existing definition
            }
            
            // Not merging beans that were originally defined in @Configuration classes because the behavior is
            // weird specifically with things like overriding the "class" attribute in XML. In the @Configuration bean
            // you will say something like new ExampleBean() but if you say <bean id="example" class="ExampleBeanSubclass" />
            // then it won't work because Spring invokes the @Bean method first as a factory
            if (existingDefinition != null && isXMLBean(existingDefinition)) {
                // merge the bean definitions together, with the new bean overriding anything previously defined
                // in the existing
                BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
                if (bdHolder != null) {
                    bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
                    BeanDefinition finalDefinition = ((AbstractBeanDefinition) existingDefinition).cloneBeanDefinition();
                    BeanDefinition overridingDefinition = bdHolder.getBeanDefinition();
                    
                    ((AbstractBeanDefinition) finalDefinition).overrideFrom(overridingDefinition);
                    bdHolder = new BeanDefinitionHolder(finalDefinition, bdHolder.getBeanName(), bdHolder.getAliases());
                    
                    LOG.info(String.format("Merged the original definition of %s defined as %s with a new definition defined as %s", bdHolder.getBeanName(), existingDefinition, overridingDefinition));
                    
                    try {
                        // Register the final decorated instance.
                        BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
                    }
                    catch (BeanDefinitionStoreException ex) {
                        getReaderContext().error("Failed to register bean definition with name '" +
                                bdHolder.getBeanName() + "'", ele, ex);
                    }
                    // Send registration event.
                    getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
                }
            } else {
                LOG.debug(String.format("Skipping the merging of bean %s and delegating to the default Spring behavior", beanId));
                super.processBeanDefinition(ele, delegate);
            }
        }
        
        /**
         * Most of this method comes from {@link BeanDefinitionParserDelegate#parseBeanDefinitionElement(Element)}. Copied here
         * so that figuring out the bean id doesn't modify the document parsing
         */
        protected String getBeanId(Element ele) {
            String id = ele.getAttribute(BeanDefinitionParserDelegate.ID_ATTRIBUTE);
            String nameAttr = ele.getAttribute(BeanDefinitionParserDelegate.NAME_ATTRIBUTE);
            
            List<String> aliases = new ArrayList<>();
            if (StringUtils.hasLength(nameAttr)) {
                String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
                aliases.addAll(Arrays.asList(nameArr));
            }

            String beanName = id;
            if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
                beanName = aliases.remove(0);
                if (logger.isDebugEnabled()) {
                    logger.debug("No XML 'id' specified - using '" + beanName +
                            "' as bean name and " + aliases + " as aliases");
                }
            }
            return beanName;
        }
        
        protected boolean isXMLBean(BeanDefinition beanDefinition) {
            return BeanUtil.isXMLBean(beanDefinition);
        }
    }
}

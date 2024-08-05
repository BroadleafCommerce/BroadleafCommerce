/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.dto.override.GroupMetadataOverride;
import org.broadleafcommerce.openadmin.dto.override.MetadataOverride;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class MetadataOverrideBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder overallMapBuilder = BeanDefinitionBuilder.rootBeanDefinition(MapFactoryBean.class);
        Map<String, BeanDefinition> overallMap = new ManagedMap<String, BeanDefinition>();
        List<Element> overrideItemElements = DomUtils.getChildElementsByTagName(element, "overrideItem");
        for (Element overrideItem : overrideItemElements) {
            String configKey = overrideItem.getAttribute("configurationKey");
            String ceilingEntity = overrideItem.getAttribute("ceilingEntity");

            if (StringUtils.isEmpty(configKey) && StringUtils.isEmpty(ceilingEntity)) {
                throw new IllegalArgumentException("Must specify either a configurationKey or a ceilingEntity attribute for the overrideItem element");
            }

            BeanDefinitionBuilder mapBuilder = BeanDefinitionBuilder.rootBeanDefinition(MapFactoryBean.class);
            Map<String, BeanDefinition> overrideItemMap = new ManagedMap<String, BeanDefinition>();
            List<Element> fieldElements = DomUtils.getChildElementsByTagName(overrideItem, "field");
            for (Element fieldElement : fieldElements) {
                String fieldName = fieldElement.getAttribute("name");
                BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(FieldMetadataOverride.class);
                overrideItemMap.put(fieldName, metadataBuilder.getBeanDefinition());
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(fieldElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("name");
                        String propValue = propElement.getAttribute("value");
                        metadataBuilder.addPropertyValue(propName, propValue);
                    }
                }

                {
                    List<Element> validationElements = DomUtils.getChildElementsByTagName(fieldElement, "validation");
                    Map<String, Map<String, String>> validationConfigMap = new ManagedMap<String, Map<String, String>>();
                    for (Element validationElement : validationElements) {
                        Map<String, String> validationMap = new ManagedMap<String, String>();
                        List<Element> valPropElements = DomUtils.getChildElementsByTagName(validationElement, "property");
                        for (Element valPropElement : valPropElements) {
                            String valPropName = valPropElement.getAttribute("name");
                            String valPropValue = valPropElement.getAttribute("value");
                            validationMap.put(valPropName, valPropValue);
                        }
                        String className = validationElement.getAttribute("className");
                        validationConfigMap.put(className, validationMap);
                    }
                    if (!validationConfigMap.isEmpty()) {
                        metadataBuilder.addPropertyValue("validationConfigurations", validationConfigMap);
                    }
                }

                {
                    List<Element> showIfFieldEqualsElements = DomUtils.getChildElementsByTagName(fieldElement, "showIfFieldEquals");
                    Map<String, List<String>> valueConfigMap = new ManagedMap<String, List<String>>();
                    for (Element valueElement : showIfFieldEqualsElements) {
                        List<String> validationMap = new ArrayList<String>();
                        List<Element> valPropElements = DomUtils.getChildElementsByTagName(valueElement, "property");
                        for (Element valPropElement : valPropElements) {
                            String valPropValue = valPropElement.getAttribute("value");
                            validationMap.add(valPropValue);
                        }
                        String className = valueElement.getAttribute("fieldName");
                        valueConfigMap.put(className, validationMap);
                    }
                    if (!valueConfigMap.isEmpty()) {
                        metadataBuilder.addPropertyValue("showIfFieldEquals", valueConfigMap);
                    }
                }

                {
                    List<Element> optionElements = DomUtils.getChildElementsByTagName(fieldElement, "optionFilterValue");
                    String[][] optionFilterValues = new String[optionElements.size()][3];
                    int j = 0;
                    for (Element optionElement : optionElements) {
                        optionFilterValues[j][0] = optionElement.getAttribute("name");
                        optionFilterValues[j][1] = optionElement.getAttribute("value");
                        optionFilterValues[j][2] = optionElement.getAttribute("type");
                        j++;
                    }
                    if (!ArrayUtils.isEmpty(optionFilterValues)) {
                        metadataBuilder.addPropertyValue("optionFilterValues", optionFilterValues);
                    }
                }

                {
                    List<Element> keyElements = DomUtils.getChildElementsByTagName(fieldElement, "mapKey");
                    String[][] keyValues = new String[keyElements.size()][2];
                    int j = 0;
                    for (Element keyElement : keyElements) {
                        keyValues[j][0] = keyElement.getAttribute("value");
                        keyValues[j][1] = keyElement.getAttribute("displayValue");
                        j++;
                    }
                    if (!ArrayUtils.isEmpty(keyValues)) {
                        metadataBuilder.addPropertyValue("keys", keyValues);
                    }
                }

                {
                    List<Element> children = DomUtils.getChildElementsByTagName(fieldElement, "customCriteria");
                    String[] values = new String[children.size()];
                    int j = 0;
                    for (Element childElem : children) {
                        values[j] = childElem.getAttribute("value");
                        j++;
                    }
                    if (!ArrayUtils.isEmpty(values)) {
                        metadataBuilder.addPropertyValue("customCriteria", values);
                    }
                }

                {
                    List<Element> children = DomUtils.getChildElementsByTagName(fieldElement, "maintainedAdornedTargetField");
                    String[] values = new String[children.size()];
                    int j = 0;
                    for (Element childElem : children) {
                        values[j] = childElem.getAttribute("value");
                        j++;
                    }
                    if (!ArrayUtils.isEmpty(values)) {
                        metadataBuilder.addPropertyValue("maintainedAdornedTargetFields", values);
                    }
                }

                {
                    List<Element> children = DomUtils.getChildElementsByTagName(fieldElement, "gridVisibleField");
                    String[] values = new String[children.size()];
                    int j = 0;
                    for (Element childElem : children) {
                        values[j] = childElem.getAttribute("value");
                        j++;
                    }
                    if (!ArrayUtils.isEmpty(values)) {
                        metadataBuilder.addPropertyValue("gridVisibleFields", values);
                    }
                }

                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(fieldElement, "selectizeVisibleField");
                    for (Element propElement : propElements) {
                        String propValue = propElement.getAttribute("value");
                        metadataBuilder.addPropertyValue("selectizeVisibleField", propValue);
                    }
                }
            }

            List<Element> tabElements = DomUtils.getChildElementsByTagName(overrideItem, "tab");
            for (Element tabElement : tabElements) {
                String overrideName = tabElement.getAttribute("tabName");
                BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(MetadataOverride.class);
                overrideItemMap.put(overrideName, metadataBuilder.getBeanDefinition());
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(tabElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("property");
                        String propValue = propElement.getAttribute("value");
                        metadataBuilder.addPropertyValue(propName, propValue);
                    }
                }
            }

            List<Element> groupElements = DomUtils.getChildElementsByTagName(overrideItem, "group");
            for (Element groupElement : groupElements) {
                String overrideName = groupElement.getAttribute("tabName");
                overrideName += "-@-" + groupElement.getAttribute("groupName");
                BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroupMetadataOverride.class);
                overrideItemMap.put(overrideName, metadataBuilder.getBeanDefinition());
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(groupElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("property");
                        String propValue = propElement.getAttribute("value");
                        metadataBuilder.addPropertyValue(propName, propValue);
                    }
                }
            }
            mapBuilder.addPropertyValue("sourceMap", overrideItemMap);

            overallMap.put(StringUtils.isEmpty(configKey) ? ceilingEntity : configKey, mapBuilder.getBeanDefinition());
        }

        overallMapBuilder.addPropertyValue("sourceMap", overallMap);

        return overallMapBuilder.getBeanDefinition();
    }
}

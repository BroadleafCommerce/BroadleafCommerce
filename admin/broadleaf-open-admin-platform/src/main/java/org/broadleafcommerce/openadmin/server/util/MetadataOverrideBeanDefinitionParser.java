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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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

    public static final String ROOT_METADATA_OVERRIDE_BEAN = "blMetadataOverrides";

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        AbstractBeanDefinition response = null;
        if (parserContext.getRegistry().containsBeanDefinition(ROOT_METADATA_OVERRIDE_BEAN)) {
            response = (AbstractBeanDefinition) parserContext.getRegistry().getBeanDefinition(ROOT_METADATA_OVERRIDE_BEAN);
        }
        Map<String, BeanDefinition> overallMap;
        if (response != null) {
            overallMap = (Map<String, BeanDefinition>) response.getPropertyValues().get("sourceMap");
        } else {
            overallMap = new ManagedMap<>();
        }
        List<Element> overrideItemElements = DomUtils.getChildElementsByTagName(element, "overrideItem");
        for (Element overrideItem : overrideItemElements) {
            String configKey = overrideItem.getAttribute("configurationKey");
            String ceilingEntity = overrideItem.getAttribute("ceilingEntity");

            if (StringUtils.isEmpty(configKey) && StringUtils.isEmpty(ceilingEntity)) {
                throw new IllegalArgumentException("Must specify either a configurationKey or a ceilingEntity attribute for the overrideItem element");
            }

            BeanDefinition overrideItemDefinition = null;
            if (overallMap.containsKey(StringUtils.isEmpty(configKey) ? ceilingEntity : configKey)) {
                overrideItemDefinition = overallMap.get(StringUtils.isEmpty(configKey) ? ceilingEntity : configKey);
            }
            Map<String, BeanDefinition> overrideItemMap;
            if (overrideItemDefinition != null) {
                overrideItemMap = (Map<String, BeanDefinition>) overrideItemDefinition.getPropertyValues().get("sourceMap");
            } else {
                overrideItemMap = new ManagedMap<>();
            }

            List<Element> fieldElements = DomUtils.getChildElementsByTagName(overrideItem, "field");
            for (Element fieldElement : fieldElements) {
                String fieldName = fieldElement.getAttribute("name");
                BeanDefinition metadataDefinition;
                if (overrideItemMap.containsKey(fieldName)) {
                    metadataDefinition = overrideItemMap.get(fieldName);
                } else {
                    BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(FieldMetadataOverride.class);
                    metadataDefinition = metadataBuilder.getBeanDefinition();
                    overrideItemMap.put(fieldName, metadataDefinition);
                }
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(fieldElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("name");
                        String propValue = propElement.getAttribute("value");
                        metadataDefinition.getPropertyValues().addPropertyValue(propName, propValue);
                    }
                }

                {
                    List<Element> validationElements = DomUtils.getChildElementsByTagName(fieldElement, "validation");
                    Map<String, Map<String, String>> validationConfigMap = new ManagedMap<>();
                    for (Element validationElement : validationElements) {
                        Map<String, String> validationMap = new ManagedMap<>();
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
                        metadataDefinition.getPropertyValues().addPropertyValue("validationConfigurations", validationConfigMap);
                    }
                }

                {
                    List<Element> showIfFieldEqualsElements = DomUtils.getChildElementsByTagName(fieldElement, "showIfFieldEquals");
                    Map<String, List<String>> valueConfigMap = new ManagedMap<>();
                    for (Element valueElement : showIfFieldEqualsElements) {
                        List<String> validationMap = new ArrayList<>();
                        List<Element> valPropElements = DomUtils.getChildElementsByTagName(valueElement, "property");
                        for (Element valPropElement : valPropElements) {
                            String valPropValue = valPropElement.getAttribute("value");
                            validationMap.add(valPropValue);
                        }
                        String className = valueElement.getAttribute("fieldName");
                        valueConfigMap.put(className, validationMap);
                    }
                    if (!valueConfigMap.isEmpty()) {
                        metadataDefinition.getPropertyValues().addPropertyValue("showIfFieldEquals", valueConfigMap);
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
                        metadataDefinition.getPropertyValues().addPropertyValue("optionFilterValues", optionFilterValues);
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
                        metadataDefinition.getPropertyValues().addPropertyValue("keys", keyValues);
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
                        metadataDefinition.getPropertyValues().addPropertyValue("customCriteria", values);
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
                        metadataDefinition.getPropertyValues().addPropertyValue("maintainedAdornedTargetFields", values);
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
                        metadataDefinition.getPropertyValues().addPropertyValue("gridVisibleFields", values);
                    }
                }

                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(fieldElement, "selectizeVisibleField");
                    for (Element propElement : propElements) {
                        String propValue = propElement.getAttribute("value");
                        metadataDefinition.getPropertyValues().addPropertyValue("selectizeVisibleField", propValue);
                    }
                }
            }

            List<Element> tabElements = DomUtils.getChildElementsByTagName(overrideItem, "tab");
            for (Element tabElement : tabElements) {
                String overrideName = tabElement.getAttribute("tabName");
                BeanDefinition metadataDefinition;
                if (overrideItemMap.containsKey(overrideName)) {
                    metadataDefinition = overrideItemMap.get(overrideName);
                } else {
                    BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(MetadataOverride.class);
                    metadataDefinition = metadataBuilder.getBeanDefinition();
                    overrideItemMap.put(overrideName, metadataDefinition);
                }
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(tabElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("property");
                        String propValue = propElement.getAttribute("value");
                        metadataDefinition.getPropertyValues().addPropertyValue(propName, propValue);
                    }
                }
            }

            List<Element> groupElements = DomUtils.getChildElementsByTagName(overrideItem, "group");
            for (Element groupElement : groupElements) {
                String overrideName = groupElement.getAttribute("tabName");
                overrideName += "-@-" + groupElement.getAttribute("groupName");
                BeanDefinition metadataDefinition;
                if (overrideItemMap.containsKey(overrideName)) {
                    metadataDefinition = overrideItemMap.get(overrideName);
                } else {
                    BeanDefinitionBuilder metadataBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroupMetadataOverride.class);
                    metadataDefinition = metadataBuilder.getBeanDefinition();
                    overrideItemMap.put(overrideName, metadataDefinition);
                }
                {
                    List<Element> propElements = DomUtils.getChildElementsByTagName(groupElement, "property");
                    for (Element propElement : propElements) {
                        String propName = propElement.getAttribute("property");
                        String propValue = propElement.getAttribute("value");
                        metadataDefinition.getPropertyValues().addPropertyValue(propName, propValue);
                    }
                }
            }

            if (overrideItemDefinition == null) {
                BeanDefinitionBuilder mapBuilder = BeanDefinitionBuilder.rootBeanDefinition(MapFactoryBean.class);
                mapBuilder.addPropertyValue("sourceMap", overrideItemMap);
                overallMap.put(StringUtils.isEmpty(configKey) ? ceilingEntity : configKey, mapBuilder.getBeanDefinition());
            }
        }

        if (response == null) {
            BeanDefinitionBuilder overallMapBuilder = BeanDefinitionBuilder.rootBeanDefinition(MapFactoryBean.class);
            overallMapBuilder.addPropertyValue("sourceMap", overallMap);
            response = overallMapBuilder.getBeanDefinition();
        }

        return response;
    }
}

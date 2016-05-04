/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author apazzolini
 * 
 * Wrapper class around Thymeleaf's AbstractElementProcessor that facilitates adding Objects
 * to the current evaluation context (model) for processing in the remainder of the page.
 *
 */
public abstract class AbstractModelVariableModifierProcessor extends AbstractElementProcessor {
    
    public AbstractModelVariableModifierProcessor(String elementName) {
        super(elementName);
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

    /**
     * This method will handle calling the modifyModelAttributes abstract method and return
     * an "OK" processor result
     */
    @Override
    protected ProcessorResult processElement(final Arguments arguments, final Element element) {
        modifyModelAttributes(arguments, element);
        
        // Remove the tag from the DOM
        final NestableNode parent = element.getParent();
        parent.removeChild(element);
        
        return ProcessorResult.OK;
    }
    
    /**
     * Helper method to add a value to the expression evaluation root (model) Map
     * @param key the key to add to the model
     * @param value the value represented by the key
     */
    @SuppressWarnings("unchecked")
    protected void addToModel(Arguments arguments, String key, Object value) {
        ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> void addCollectionToExistingSet(Arguments arguments, String key, Collection<T> value) {
        Set<T> items = (Set<T>) ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).get(key);
        if (items == null) {
            items = new HashSet<T>();
            ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put(key, items);
        }
        items.addAll(value);
    }

    @SuppressWarnings("unchecked")
    protected <T> void addItemToExistingSet(Arguments arguments, String key, Object value) {
        Set<T> items = (Set<T>) ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).get(key);
        if (items == null) {
            items = new HashSet<T>();                         
            ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put(key, items);
        }
        items.add((T) value);
    }
    
    /**
     * This method must be overriding by a processor that wishes to modify the model. It will
     * be called by this abstract processor in the correct precendence in the evaluation chain.
     * @param arguments
     * @param element
     */
    protected abstract void modifyModelAttributes(Arguments arguments, Element element);
}

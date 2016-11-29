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
package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Contains useful processing code for merge bean post processors. The BeanPostProcessor instances can
 * be used to merge additional collection members into collections declared elsewhere. In effect, this allows
 * an implementer to only declare the collection members they're currently interested in cause those members
 * to be merged into a larger, pre-existing list. This is more desirable than a traditional, comprehensive
 * override that would require re-declaring the original bean and all of its members in addition to the current
 * members being considered.
 * </p>
 * <p>
 * This code demonstrates using one of the concrete implementations, {@link LateStageMergeBeanPostProcessor}. The
 * basic usage pattern is to specify the id of the collection you want to merge (collectionRef) and the id
 * of the pre-existing, target collection (targetRef) that should receive the merge collection. The collection
 * can be represented using ListFactoryBean, SetFactoryBean or MapFactoryBean.
 * </p>
 * <pre>
 * {@code
 * <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 *  <property name="collectionRef" value="blPriceListRuleBuilderFieldServices"/>
 *  <property name="targetRef" value="blRuleBuilderFieldServices"/>
 * </bean>
 *
 * <bean id="blPriceListRuleBuilderFieldServices" class="org.springframework.beans.factory.config.ListFactoryBean">
 *  <property name="sourceList">
 *      <list>
 *          <ref bean="blPricingContextFieldService"/>
 *      </list>
 *  </property>
 * </bean>
 * }
 * </pre>
 *
 * @see LateStageMergeBeanPostProcessor
 * @see EarlyStageMergeBeanPostProcessor
 * @author Jeff Fischer
 */
public abstract class AbstractMergeBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    public static class BeanPackage {

        protected String sourceRef;
        protected String targetRef;
        protected Placement placement = Placement.APPEND;
        protected int position;
        protected MergeBeanStatusProvider statusProvider;
        protected boolean bySource = false;

        public String getSourceRef() {
            return sourceRef;
        }

        public void setSourceRef(String sourceRef) {
            this.sourceRef = sourceRef;
        }

        public String getTargetRef() {
            return targetRef;
        }

        public void setTargetRef(String targetRef) {
            this.targetRef = targetRef;
        }

        public Placement getPlacement() {
            return placement;
        }

        public void setPlacement(Placement placement) {
            this.placement = placement;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public MergeBeanStatusProvider getStatusProvider() {
            return statusProvider;
        }

        public void setStatusProvider(MergeBeanStatusProvider statusProvider) {
            this.statusProvider = statusProvider;
        }

    }

    protected static final Log LOG = LogFactory.getLog(AbstractMergeBeanPostProcessor.class);

    protected ApplicationContext applicationContext;
    protected BeanPackage defaultBeanPackage = new BeanPackage();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return processPackage(defaultBeanPackage, bean, beanName);
    }

    protected BeanPackage constructBeanPackage(String beanName, Map<String, Object> methodAnnotationAttributes) {
        BeanPackage beanPackage = new BeanPackage();
        beanPackage.setSourceRef(beanName);
        beanPackage.setTargetRef((String) methodAnnotationAttributes.get("targetRef"));
        beanPackage.setPlacement((Placement) methodAnnotationAttributes.get("placement"));
        beanPackage.setPosition((Integer) methodAnnotationAttributes.get("position"));
        Class<MergeBeanStatusProvider> clazz = (Class<MergeBeanStatusProvider>) methodAnnotationAttributes.get("statusProvider");
        if (MergeBeanStatusProvider.class != clazz) {
            try {
                beanPackage.setStatusProvider(clazz.newInstance());
            } catch (InstantiationException e) {
                throw ExceptionHelper.refineException(e);
            } catch (IllegalAccessException e) {
                throw ExceptionHelper.refineException(e);
            }
        }
        return beanPackage;
    }

    protected Object processPackage(BeanPackage beanPackage, Object bean, String beanName) {
        String sourceRef = beanPackage.getSourceRef();
        String targetRef = beanPackage.getTargetRef();
        Placement placement = beanPackage.getPlacement();
        int position = beanPackage.getPosition();
        MergeBeanStatusProvider statusProvider = beanPackage.getStatusProvider();
        Object sourceItem = null;
        Object targetItem = null;
        if (beanName.equals(targetRef)){
            targetItem = bean;
            if (!StringUtils.isEmpty(sourceRef)) {
                sourceItem = applicationContext.getBean(sourceRef);
            } else {
                throw new IllegalArgumentException("Must declare an source reference value. See #setCollectionRef()");
            }
        }
        if (sourceItem != null && targetItem != null) {
            if (statusProvider != null && !statusProvider.isProcessingEnabled(targetItem, beanName, applicationContext)) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(String.format("Not performing post-processing on targetRef [%s] because the registered " +
                            "status provider [%s] returned false", targetRef, statusProvider.getClass().getSimpleName()));
                }

                return bean;
            }

            if (targetItem instanceof ListFactoryBean || targetItem instanceof List) {
                try {
                    if (sourceItem instanceof List) {
                        addListToList(targetItem, sourceItem, placement, position);
                    } else {
                        if (sourceItem instanceof Collection) {
                            throw new IllegalArgumentException(String.format("Attempting to merge a collection of type " +
                                    "%s into a target list. Only source collections of type ListFactoryBean or List " +
                                    "may be used.", sourceItem.getClass().getName()));
                        }
                        addItemToList(targetItem, sourceItem, placement, position);
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (targetItem instanceof SetFactoryBean || targetItem instanceof Set) {
                try {
                    if (sourceItem instanceof Set) {
                        addSetToSet(targetItem, sourceItem, placement, position);
                    } else {
                        if (sourceItem instanceof Collection) {
                            throw new IllegalArgumentException(String.format("Attempting to merge a collection of type " +
                                    "%s into a target set. Only source collections of type SetFactoryBean or Set " +
                                    "may be used.", sourceItem.getClass().getName()));
                        }
                        addItemToSet(targetItem, sourceItem, placement, position);
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (targetItem instanceof MapFactoryBean || targetItem instanceof Map) {
                try {
                    if (sourceItem instanceof Map) {
                        addMapToMap(targetItem, (Map) sourceItem, placement, position);
                    } else {
                        throw new IllegalArgumentException(String.format("Attempting to merge an item of type " +
                                    "%s into a target map. Only source items of type MapFactoryBean or Map " +
                                    "may be used.", sourceItem.getClass().getName()));
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Bean (" + beanName + ") is specified as a merge target, " +
                        "but is not" +
                        " of type ListFactoryBean, SetFactoryBean or MapFactoryBean");
            }
        }

        return bean;
    }

    protected Object processPackage(Map<String, Object> methodAnnotationAttributes, Object bean, String beanName) {
        BeanPackage beanPackage = constructBeanPackage(beanName, methodAnnotationAttributes);
        return processPackage(beanPackage, bean, beanName);
    }

    protected void addMapToMap(Object bean, Map sourceItem, Placement placement, int position) throws NoSuchFieldException, IllegalAccessException {
        Map sourcerMap = sourceItem;
        Map targetMap;
        if (bean instanceof MapFactoryBean) {
            Field field = MapFactoryBean.class.getDeclaredField("sourceMap");
            field.setAccessible(true);
            targetMap = (Map) field.get(bean);
        } else {
            targetMap = (Map) bean;
        }
        LinkedHashMap tempMap = new LinkedHashMap();
        switch (placement) {
            case APPEND:
                tempMap.putAll(targetMap);
                tempMap.putAll(sourcerMap);
                break;
            case PREPEND:
                tempMap.putAll(sourcerMap);
                tempMap.putAll(targetMap);
                break;
            case SPECIFIC:
                boolean added = false;
                int j = 0;
                for (Object key : targetMap.keySet()) {
                    if (j == position) {
                        tempMap.putAll(sourcerMap);
                        added = true;
                    }
                    tempMap.put(key, targetMap.get(key));
                    j++;
                }
                if (!added) {
                    tempMap.putAll(sourcerMap);
                }
                break;
        }
        targetMap.clear();
        targetMap.putAll(tempMap);
    }

    protected void addSetToSet(Object bean, Object sourceItem, Placement placement, int position) throws NoSuchFieldException, IllegalAccessException {
        Set sourceSet = (Set) sourceItem;
        Set targetSet;
        if (bean instanceof SetFactoryBean) {
            Field field = SetFactoryBean.class.getDeclaredField("sourceSet");
            field.setAccessible(true);
            targetSet = (Set) field.get(bean);
        } else {
            targetSet = (Set)bean;
        }
        List tempList = new ArrayList(targetSet);
        switch (placement) {
            case APPEND:
                tempList.addAll(sourceSet);
                break;
            case PREPEND:
                tempList.addAll(0, sourceSet);
                break;
            case SPECIFIC:
                tempList.addAll(position, sourceSet);
                break;
        }
        targetSet.clear();
        targetSet.addAll(tempList);
    }

    protected void addItemToSet(Object bean, Object sourceItem, Placement placement, int position) throws NoSuchFieldException, IllegalAccessException {
        Set targetSet;
        if (bean instanceof SetFactoryBean) {
            Field field = SetFactoryBean.class.getDeclaredField("sourceSet");
            field.setAccessible(true);
            targetSet = (Set) field.get(bean);
        } else {
            targetSet = (Set)bean;
        }
        List tempList = new ArrayList(targetSet);
        switch (placement) {
            case APPEND:
                tempList.add(sourceItem);
                break;
            case PREPEND:
                tempList.add(0, sourceItem);
                break;
            case SPECIFIC:
                tempList.add(position, sourceItem);
                break;
        }
        targetSet.clear();
        targetSet.addAll(tempList);
    }

    protected void addListToList(Object bean, Object sourceItem, Placement placement, int position) throws NoSuchFieldException, IllegalAccessException {
        List sourceList = (List) sourceItem;
        List targetList;
        if (bean instanceof ListFactoryBean) {
            Field field = ListFactoryBean.class.getDeclaredField("sourceList");
            field.setAccessible(true);
            targetList = (List) field.get(bean);
        } else {
            targetList = (List) bean;
        }
        switch (placement) {
            case APPEND:
                targetList.addAll(sourceList);
                break;
            case PREPEND:
                targetList.addAll(0, sourceList);
                break;
            case SPECIFIC:
                targetList.addAll(position, sourceList);
                break;
        }
    }

    protected void addItemToList(Object bean, Object sourceItem, Placement placement, int position) throws NoSuchFieldException, IllegalAccessException {
        List targetList;
        if (bean instanceof ListFactoryBean) {
            Field field = ListFactoryBean.class.getDeclaredField("sourceList");
            field.setAccessible(true);
            targetList = (List) field.get(bean);
        } else {
            targetList = (List) bean;
        }
        switch (placement) {
            case APPEND:
                targetList.add(sourceItem);
                break;
            case PREPEND:
                targetList.add(0, sourceItem);
                break;
            case SPECIFIC:
                targetList.add(position, sourceItem);
                break;
        }
    }

    /**
     * Retrieve the id of the collection to be merged
     *
     * @deprecated use {@link #getSourceRef()} instead
     * @return the id of the collection to be merged
     */
    @Deprecated
    public String getCollectionRef() {
        return defaultBeanPackage.getSourceRef();
    }

    /**
     * Set the id of the collection to be merged
     *
     * @deprecated use {@link #setSourceRef(String)} instead
     * @param collectionRef the id of the collection to be merged
     */
    @Deprecated
    public void setCollectionRef(String collectionRef) {
        defaultBeanPackage.setSourceRef(collectionRef);
    }

    /**
     * Retrieve the id of the collection (or individual bean) to be merged
     *
     * @return the id of the item to be merged
     */
    public String getSourceRef() {
        return defaultBeanPackage.getSourceRef();
    }

    /**
     * Set the id of the collection (or individual bean) to be merged
     *
     * @param sourceRef the id of the item to be merged
     */
    public void setSourceRef(String sourceRef) {
        defaultBeanPackage.setSourceRef(sourceRef);
    }

    /**
     * Retrieve the id of the collection to receive the merge
     *
     * @return the id of the collection receiving the merge
     */
    public String getTargetRef() {
        return defaultBeanPackage.getTargetRef();
    }

    /**
     * Set the id of the collection to receive the merge
     *
     * @param targetRef the id of the collection receiving the merge
     */
    public void setTargetRef(String targetRef) {
        defaultBeanPackage.setTargetRef(targetRef);
    }

    /**
     * The position in the target collection to place the merge. This can be at the beginning,
     * end or at an explicit position.
     *
     * @return the position in the target collection to place the merge
     */
    public Placement getPlacement() {
        return defaultBeanPackage.getPlacement();
    }

    /**
     * The position in the target collection to place the merge. This can be at the beginning,
     * end or at an explicit position.
     *
     * @param placement the position in the target collection to place the merge
     */
    public void setPlacement(Placement placement) {
        defaultBeanPackage.setPlacement(placement);
    }

    /**
     * If a placement of type Placement.SPECIFIC is used, then this is the integer position in the target
     * target collection at which the merge will be performed.
     *
     * @return the specific position in the target collection
     */
    public int getPosition() {
        return defaultBeanPackage.getPosition();
    }

    /**
     * If a placement of type Placement.SPECIFIC is used, then this is the integer position in the target
     * target collection at which the merge will be performed.
     *
     * @param position the specific position in the target collection
     */
    public void setPosition(int position) {
        defaultBeanPackage.setPosition(position);
    }

    /**
     * Gets the status provider that is configured for this post processor
     * 
     * @return the MergeStatusBeanProvider
     */
    public MergeBeanStatusProvider getStatusProvider() {
        return defaultBeanPackage.getStatusProvider();
    }
    
    /**
     * Sets the MergeBeanStatusProvider, which controls whether or not this post processor is activated.
     * If no statusProvider is set, then we will always execute.
     * 
     * @param statusProvider
     */
    public void setStatusProvider(MergeBeanStatusProvider statusProvider) {
        defaultBeanPackage.setStatusProvider(statusProvider);
    }

}

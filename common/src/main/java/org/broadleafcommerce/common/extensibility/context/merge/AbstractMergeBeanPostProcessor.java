/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
    protected static final Log LOG = LogFactory.getLog(AbstractMergeBeanPostProcessor.class);

    protected String collectionRef;
    protected String targetRef;
    protected Placement placement = Placement.APPEND;
    protected int position;
    protected ApplicationContext applicationContext;
    protected MergeBeanStatusProvider statusProvider;

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
        if (statusProvider != null && !statusProvider.isProcessingEnabled(bean, beanName, applicationContext)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("Not performing post-processing on targetRef [%s] because the registered " +
                		"status provider [%s] returned false", targetRef, statusProvider.getClass().getSimpleName()));
            }
            
            return bean;
        }
        
        if (beanName.equals(targetRef)) {
            Object mergeCollection = applicationContext.getBean(collectionRef);
            if (bean instanceof ListFactoryBean || bean instanceof List) {
                try {
                    List mergeList = (List) mergeCollection;
                    List sourceList;
                    if (bean instanceof ListFactoryBean) {
                        Field field = ListFactoryBean.class.getDeclaredField("sourceList");
                        field.setAccessible(true);
                        sourceList = (List) field.get(bean);
                    } else {
                        sourceList = (List) bean;
                    }
                    switch (placement) {
                        case APPEND:
                            sourceList.addAll(mergeList);
                            break;
                        case PREPEND:
                            sourceList.addAll(0, mergeList);
                            break;
                        case SPECIFIC:
                            sourceList.addAll(position, mergeList);
                            break;
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (bean instanceof SetFactoryBean || bean instanceof Set) {
                try {
                    Set mergeSet = (Set) mergeCollection;
                    Set sourceSet;
                    if (bean instanceof SetFactoryBean) {
                        Field field = SetFactoryBean.class.getDeclaredField("sourceSet");
                        field.setAccessible(true);
                        sourceSet = (Set) field.get(bean);
                    } else {
                        sourceSet = (Set)bean;
                    }
                    List tempList = new ArrayList(sourceSet);
                    switch (placement) {
                        case APPEND:
                            tempList.addAll(mergeSet);
                            break;
                        case PREPEND:
                            tempList.addAll(0, mergeSet);
                            break;
                        case SPECIFIC:
                            tempList.addAll(position, mergeSet);
                            break;
                    }
                    sourceSet.clear();
                    sourceSet.addAll(tempList);
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (bean instanceof MapFactoryBean || bean instanceof Map) {
                try {
                    Map mergeMap = (Map) mergeCollection;
                    Map sourceMap;
                    if (bean instanceof MapFactoryBean) {
                        Field field = MapFactoryBean.class.getDeclaredField("sourceMap");
                        field.setAccessible(true);
                        sourceMap = (Map) field.get(bean);
                    } else {
                        sourceMap = (Map) bean;
                    }
                    LinkedHashMap tempMap = new LinkedHashMap();
                    switch (placement) {
                        case APPEND:
                            tempMap.putAll(sourceMap);
                            tempMap.putAll(mergeMap);
                            break;
                        case PREPEND:
                            tempMap.putAll(mergeMap);
                            tempMap.putAll(sourceMap);
                            break;
                        case SPECIFIC:
                            boolean added = false;
                            int j = 0;
                            for (Object key : sourceMap.keySet()) {
                                if (j == position) {
                                    tempMap.putAll(mergeMap);
                                    added = true;
                                }
                                tempMap.put(key, sourceMap.get(key));
                                j++;
                            }
                            if (!added) {
                                tempMap.putAll(mergeMap);
                            }
                            break;
                    }
                    sourceMap.clear();
                    sourceMap.putAll(tempMap);
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

    /**
     * Retrieve the id of the collection to be merged
     *
     * @return the id of the collection to be merged
     */
    public String getCollectionRef() {
        return collectionRef;
    }

    /**
     * Set the id of the collection to be merged
     *
     * @param collectionRef the id of the collection to be merged
     */
    public void setCollectionRef(String collectionRef) {
        this.collectionRef = collectionRef;
    }

    /**
     * Retrieve the id of the collection to receive the merge
     *
     * @return the id of the collection receiving the merge
     */
    public String getTargetRef() {
        return targetRef;
    }

    /**
     * Set the id of the collection to receive the merge
     *
     * @param targetRef the id of the collection receiving the merge
     */
    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    /**
     * The position in the target collection to place the merge. This can be at the beginning,
     * end or at an explicit position.
     *
     * @return the position in the target collection to place the merge
     */
    public Placement getPlacement() {
        return placement;
    }

    /**
     * The position in the target collection to place the merge. This can be at the beginning,
     * end or at an explicit position.
     *
     * @param placement the position in the target collection to place the merge
     */
    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    /**
     * If a placement of type Placement.SPECIFIC is used, then this is the integer position in the target
     * target collection at which the merge will be performed.
     *
     * @return the specific position in the target collection
     */
    public int getPosition() {
        return position;
    }

    /**
     * If a placement of type Placement.SPECIFIC is used, then this is the integer position in the target
     * target collection at which the merge will be performed.
     *
     * @param position the specific position in the target collection
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets the status provider that is configured for this post processor
     * 
     * @return the MergeStatusBeanProvider
     */
    public MergeBeanStatusProvider getStatusProvider() {
        return statusProvider;
    }
    
    /**
     * Sets the MergeBeanStatusProvider, which controls whether or not this post processor is activated.
     * If no statusProvider is set, then we will always execute.
     * 
     * @param statusProvider
     */
    public void setStatusProvider(MergeBeanStatusProvider statusProvider) {
        this.statusProvider = statusProvider;
    }
    
}

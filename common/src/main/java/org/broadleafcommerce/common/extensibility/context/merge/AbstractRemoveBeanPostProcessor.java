/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.context.merge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>
 * Contains useful processing code for merge bean post processors. The BeanPostProcessor instances can
 * be used to remove collection members from collections declared elsewhere. In effect, this allows
 * an implementer to remove a bean that was declared in a collection (list, set or map) or previously merged
 * via LateStageMergeBeanPostProcessor or EarlyStageMergeBeanPostProcessor.
 * </p>
 * <p>
 * This code demonstrates using one of the concrete implementations, {@link LateStageRemoveBeanPostProcessor}. The
 * basic usage pattern is to specify the id of the member you want to remove (beanRef) and the id
 * of the pre-existing, target collection (targetRef) that should receive the removal. The collection
 * can be represented using ListFactoryBean, SetFactoryBean or MapFactoryBean. For MapFactoryBeans, use either the
 * mapKey or mapKeyRef property instead to reference the map item to remove.
 * </p>
 * <pre>
 * {@code
 * <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageRemoveBeanPostProcessor">
 *  <property name="beanRef" value="myBean"/>
 *  <property name="targetRef" value="targetCollection"/>
 * </bean>
 * }
 * </pre>
 *
 * @see LateStageRemoveBeanPostProcessor
 * @see EarlyStageRemoveBeanPostProcessor
 * @author Jeff Fischer
 */
public abstract class AbstractRemoveBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    protected String beanRef;
    protected String targetRef;
    protected String mapKey;
    protected String mapKeyRef;
    protected ApplicationContext applicationContext;

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
        if (beanName.equals(targetRef)) {
            if (bean instanceof ListFactoryBean || bean instanceof List) {
                Object beanToRemove = applicationContext.getBean(beanRef);
                try {
                    List sourceList;
                    if (bean instanceof ListFactoryBean) {
                        Field field = ListFactoryBean.class.getDeclaredField("sourceList");
                        field.setAccessible(true);
                        sourceList = (List) field.get(bean);
                    } else {
                        sourceList = (List) bean;
                    }
                    Iterator itr = sourceList.iterator();
                    while (itr.hasNext()) {
                        Object member = itr.next();
                        if (member.equals(beanToRemove)) {
                            itr.remove();
                        }
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (bean instanceof SetFactoryBean || bean instanceof Set) {
                Object beanToRemove = applicationContext.getBean(beanRef);
                try {
                    Set sourceSet;
                    if (bean instanceof SetFactoryBean) {
                        Field field = SetFactoryBean.class.getDeclaredField("sourceSet");
                        field.setAccessible(true);
                        sourceSet = (Set) field.get(bean);
                    } else {
                        sourceSet = (Set)bean;
                    }
                    List tempList = new ArrayList(sourceSet);
                    Iterator itr = tempList.iterator();
                    while (itr.hasNext()) {
                        Object member = itr.next();
                        if (member.equals(beanToRemove)) {
                            itr.remove();
                        }
                    }
                    sourceSet.clear();
                    sourceSet.addAll(tempList);
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (bean instanceof MapFactoryBean || bean instanceof Map) {
                try {
                    Map sourceMap;
                    if (bean instanceof MapFactoryBean) {
                        Field field = MapFactoryBean.class.getDeclaredField("sourceMap");
                        field.setAccessible(true);
                        sourceMap = (Map) field.get(bean);
                    } else {
                        sourceMap = (Map) bean;
                    }
                    Object key;
                    if (mapKey != null) {
                        key = mapKey;
                    } else {
                        key = applicationContext.getBean(mapKeyRef);
                    }
                    Map referenceMap = new LinkedHashMap(sourceMap);
                    for (Object sourceKey : referenceMap.keySet()) {
                        if (sourceKey.equals(key)) {
                            sourceMap.remove(key);
                        }
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

    public String getBeanRef() {
        return beanRef;
    }

    public void setBeanRef(String beanRef) {
        this.beanRef = beanRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getMapKeyRef() {
        return mapKeyRef;
    }

    public void setMapKeyRef(String mapKeyRef) {
        this.mapKeyRef = mapKeyRef;
    }
}

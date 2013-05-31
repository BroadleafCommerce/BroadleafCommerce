package org.broadleafcommerce.common.extensibility.context.merge;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jeff Fischer
 */
public class MergeBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    protected List mergeList = new ArrayList();
    protected Map mergeMap = new HashMap();
    protected Set mergeSet = new HashSet();
    protected String targetRef;
    protected int order = Integer.MIN_VALUE;
    protected Placement placement = Placement.APPEND;
    protected int position;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals(targetRef)) {
            if (bean instanceof ListFactoryBean) {
                try {
                    Field field = ListFactoryBean.class.getDeclaredField("sourceList");
                    field.setAccessible(true);
                    List sourceList = (List) field.get(bean);
                    switch(placement) {
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
            } else if (bean instanceof SetFactoryBean) {
                try {
                    Field field = SetFactoryBean.class.getDeclaredField("sourceSet");
                    field.setAccessible(true);
                    Set sourceSet = (Set) field.get(bean);
                    List tempList = new ArrayList(sourceSet);
                    switch(placement) {
                        case APPEND:
                            tempList.addAll(mergeList);
                            break;
                        case PREPEND:
                            tempList.addAll(0, mergeList);
                            break;
                        case SPECIFIC:
                            tempList.addAll(position, mergeList);
                            break;
                    }
                    sourceSet.clear();
                    sourceSet.addAll(tempList);
                } catch (Exception e) {
                    throw new BeanCreationException(e.getMessage());
                }
            } else if (bean instanceof MapFactoryBean) {
                try {
                    Field field = MapFactoryBean.class.getDeclaredField("sourceMap");
                    field.setAccessible(true);
                    Map sourceMap = (Map) field.get(bean);
                    LinkedHashMap tempMap = new LinkedHashMap();
                    switch(placement) {
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
                throw new IllegalArgumentException("Bean (" + beanName + ") is specified as a merge target, but is not" +
                        "of type List or Map");
            }
        }

        return bean;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List getMergeList() {
        return mergeList;
    }

    public void setMergeList(List mergeList) {
        this.mergeList = mergeList;
    }

    public Map getMergeMap() {
        return mergeMap;
    }

    public void setMergeMap(Map mergeMap) {
        this.mergeMap = mergeMap;
    }

    public Set getMergeSet() {
        return mergeSet;
    }

    public void setMergeSet(Set mergeSet) {
        this.mergeSet = mergeSet;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }
}

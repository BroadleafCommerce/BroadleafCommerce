package org.broadleafcommerce.common.weave;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @see org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformersManager
 *
 * @author Jeff Fischer
 */
@Service("blConditionalDirectCopyTransformersManager")
public class ConditionalDirectCopyTransformersManagerImpl implements BeanFactoryAware, ConditionalDirectCopyTransformersManager {

    @Resource(name="blConditionalDirectCopyTransformers")
    protected Map<String, ConditionalDirectCopyTransformMemberDto> entityToPropertyMap;
    protected Map<String, ConditionalDirectCopyTransformMemberDto> enabledEntities = new HashMap<String, ConditionalDirectCopyTransformMemberDto>();
    protected ConfigurableBeanFactory beanFactory;

    @PostConstruct
    public void init() {
        for (Map.Entry<String, ConditionalDirectCopyTransformMemberDto> entry : entityToPropertyMap.entrySet()) {
            if (isPropertyEnabled(entry.getValue().getConditionalProperty())) {
                enabledEntities.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public Boolean isEntityEnabled(String entityName) {
        return enabledEntities.containsKey(entityName);
    }

    @Override
    public ConditionalDirectCopyTransformMemberDto getTransformMember(String entityName) {
        return enabledEntities.get(entityName);
    }

    protected Boolean isPropertyEnabled(String propertyName) {
        Boolean shouldProceed;
        try {
            String value = beanFactory.resolveEmbeddedValue("${" + propertyName + ":false}");
            shouldProceed = Boolean.parseBoolean(value);
        } catch (Exception e) {
            shouldProceed = false;
        }
        return shouldProceed;
    }
}

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

package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.path;

import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
import org.hibernate.ejb.criteria.PathSource;
import org.hibernate.ejb.criteria.path.SingularAttributePath;

import java.io.Serializable;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;


/**
 * This specialized verison of {@link SingularAttributePath} can be used when it is desirable to find a property
 * that belongs to a polymorphic version of the identified path. This class will inspect the entire polymorphic 
 * hierarchy of the original class and return the first attribute found in that hierarchy.
 * 
 * For example, the "defaultSku" property on ProductImpl is mapped to SkuImpl. However, an implementation may have a
 * subclass of SkuImpl such as MySku, and MySku might have a property called "someProperty".
 * 
 * To be able to navigate to "someProperty" through "defaultSku", we must establish the relationship from MySku instead
 * of what is found by default (SkuImpl). This class will do its best to locate the appropriate attribute in any of
 * SkuImpl's polymoprhic hierarchy.
 * 
 * @author Andre Azzolini (apazzolini)
 * @param <X>
 */
public class PolymorphicSingularAttributePath<X> extends SingularAttributePath<X> implements Serializable {
    
    private static final long serialVersionUID = 6208588959281841553L;
    
    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();
    
    protected final CriteriaBuilderImpl builder;

    public PolymorphicSingularAttributePath(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource pathSource, 
            SingularAttribute<?, X> attribute) {
        super(criteriaBuilder, javaType, pathSource, attribute);
        this.builder = criteriaBuilder;
    }

    @Override
	@SuppressWarnings("rawtypes")
	protected Attribute locateAttributeInternal(String attributeName) {
	    EntityManagerFactoryImpl em = builder.getEntityManagerFactory();
	    Metamodel mm = em.getMetamodel();
	    
	    Class<?>[] polyClasses = dynamicDaoHelper.getAllPolymorphicEntitiesFromCeiling(getJavaType(), em.getSessionFactory(), true, true);
	    for (Class<?> clazz : polyClasses) {
    		ManagedType mt = mm.managedType(clazz);
    		try {
    		    return mt.getAttribute(attributeName);
    		} catch (IllegalArgumentException e) {
    		    // Do nothing - we'll try the next class and see if it has the attribute
    		}
	    }
	    
	    // We've failed to find the attribute ourselves. Let the super implementation try
	    return super.locateAttributeInternal(attributeName);
	}

}

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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

/**
 * @author Jeff Fischer
 */
public class FieldPathBuilder {
    
    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();
    
    protected CriteriaQuery criteria;
    protected List<Predicate> restrictions;
    
    public FieldPath getFieldPath(From root, String fullPropertyName) {
        String[] pieces = fullPropertyName.split("\\.");
        List<String> associationPath = new ArrayList<String>();
        List<String> basicProperties = new ArrayList<String>();
        int j = 0;
        for (String piece : pieces) {
            checkPiece: {
                if (j == 0) {
                    Path path = root.get(piece);
                    if (path instanceof PluralAttributePath) {
                        associationPath.add(piece);
                        break checkPiece;
                    }
                }
                basicProperties.add(piece);
            }
            j++;
        }
        FieldPath fieldPath = new FieldPath()
            .withAssociationPath(associationPath)
            .withTargetPropertyPieces(basicProperties);

        return fieldPath;
    }

    public Path getPath(From root, String fullPropertyName, CriteriaBuilder builder) {
        return getPath(root, getFieldPath(root, fullPropertyName), builder);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "serial"})
    public Path getPath(From root, FieldPath fieldPath, final CriteriaBuilder builder) {
        FieldPath myFieldPath = fieldPath;
        if (!StringUtils.isEmpty(fieldPath.getTargetProperty())) {
            myFieldPath = getFieldPath(root, fieldPath.getTargetProperty());
        }
        From myRoot = root;
        for (String pathElement : myFieldPath.getAssociationPath()) {
            myRoot = myRoot.join(pathElement);
        }
        Path path = myRoot;
        
        for (int i = 0; i < myFieldPath.getTargetPropertyPieces().size(); i++) {
            String piece = myFieldPath.getTargetPropertyPieces().get(i);
            
            //if (path.getJavaType().isAnnotationPresent(Embeddable.class)) {
                //String original = ((SingularAttributePath) path).getAttribute().getDeclaringType().getJavaType().getName() + "." + ((SingularAttributePath) path).getAttribute().getName() + "." + piece;
                //String copy = path.getJavaType().getName() + "." + piece;
                //copyCollectionPersister(original, copy, ((CriteriaBuilderImpl) builder).getEntityManagerFactory());
            //}
            
            try {
                path = path.get(piece);
            } catch (IllegalArgumentException e) {
                // We weren't able to resolve the requested piece, likely because it's in a polymoprhic version
                // of the path we're currently on. Let's see if there's any polymoprhic version of our class to
                // use instead.
        	    SessionFactoryImpl em = ((CriteriaBuilderImpl) builder).getEntityManagerFactory();
        	    Metamodel mm = em.getMetamodel();
        	    boolean found = false;
        	    
        	    Class<?>[] polyClasses = dynamicDaoHelper.getAllPolymorphicEntitiesFromCeiling(
        	            path.getJavaType(), em, true, true);
        	    
        	    for (Class<?> clazz : polyClasses) {
            		ManagedType mt = mm.managedType(clazz);
            		try {
            		    Attribute attr = mt.getAttribute(piece);
            		    if (attr != null) {
                		    Root additionalRoot = criteria.from(clazz);
                		    restrictions.add(builder.equal(path, additionalRoot));
                		    path = additionalRoot.get(piece);
                		    found = true;
                		    break;
            		    }
            		} catch (IllegalArgumentException e2) {
            		    // Do nothing - we'll try the next class and see if it has the attribute
            		}
        	    }
        	    
        	    if (!found) {
        	        throw new IllegalArgumentException("Could not resolve requested attribute against path, including" +
        	        		" known polymorphic versions of the root", e);
        	    }
            }
            
            if (path.getParentPath() != null && path.getParentPath().getJavaType().isAnnotationPresent(Embeddable.class) && path instanceof PluralAttributePath) {
                //We need a workaround for this problem until it is resolved in Hibernate (loosely related to and likely resolved by https://hibernate.atlassian.net/browse/HHH-8802)
                //We'll throw a specialized exception (and handle in an alternate flow for calls from BasicPersistenceModule)
                throw new CriteriaConversionException(String.format("Unable to create a JPA criteria Path through an @Embeddable object to a collection that resides therein (%s)", fieldPath.getTargetProperty()), fieldPath);
//                //TODO this code should work, but there still appear to be bugs in Hibernate's JPA criteria handling for lists
//                //inside Embeddables
//                Class<?> myClass = ((PluralAttributePath) path).getAttribute().getClass().getInterfaces()[0];
//                //we don't know which version of "join" to call, so we'll let reflection figure it out
//                try {
//                    From embeddedJoin = myRoot.join(((SingularAttributePath) path.getParentPath()).getAttribute());
//                    Method join = embeddedJoin.getClass().getMethod("join", myClass);
//                    path = (Path) join.invoke(embeddedJoin, ((PluralAttributePath) path).getAttribute());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
            }
        }

        return path;
    }

    /**
     * This is a workaround for HHH-6562 (https://hibernate.atlassian.net/browse/HHH-6562)
     */
    @SuppressWarnings("unchecked")
//    private void copyCollectionPersister(String originalKey, String copyKey,
//            SessionFactoryImpl sessionFactory) {
//        try {
//            Field collectionPersistersField = SessionFactoryImpl.class
//                    .getDeclaredField("collectionPersisters");
//            collectionPersistersField.setAccessible(true);
//            Map collectionPersisters = (Map) collectionPersistersField.get(sessionFactory);
//            if (collectionPersisters.containsKey(originalKey)) {
//                Object collectionPersister = collectionPersisters.get(originalKey);
//                collectionPersisters.put(copyKey, collectionPersister);
//            }
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
    
    public CriteriaQuery getCriteria() {
        return criteria;
    }

    public void setCriteria(CriteriaQuery criteria) {
        this.criteria = criteria;
    }

    public List<Predicate> getRestrictions() {
        return restrictions;
    }
    
    public void setRestrictions(List<Predicate> restrictions) {
        this.restrictions = restrictions;
    }
    
}

/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa.cache;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.copy.AbstractClassTransformer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Strip the cache annotation from classes and the class fields
 * 
 * Example configuration
 * 
 * <pre>
 * {@code
 * @Merge("blMergedClassTransformers")
 * public RemoveCacheClassTransformer RemoveProductCache() {
 *     RemoveCacheClassTransformer transformer = new RemoveCacheClassTransformer("Remove Product Cache");
 *     List<String> cacheRegions = new ArrayList<String>();
 *     cacheRegions.add("blCustomerElements");
 *     cacheRegions.add("blOrderElements");
 *     transformer.setCacheRegions(cacheRegions);
 *     return transformer;
 * }
 * }
 * </pre>
 * 
 * This feature requires that the property "remove.order.customer.entity.l2cache" be enabled (true).
 * 
 * @author Jeff Fischer
 * @author Daniel Colgrove
 */
public class RemoveCacheClassTransformer extends AbstractClassTransformer implements BroadleafClassTransformer, BeanFactoryAware {

    private static final Log logger = LogFactory.getLog(RemoveCacheClassTransformer.class);

    protected String moduleName;
    protected List<String> classNames = new ArrayList<String>();
    protected List<String> cacheRegions = new ArrayList<String>();
    protected String annotationClass = "org.hibernate.annotations.Cache";
    protected ConfigurableBeanFactory beanFactory;

    @Value("${remove.order.customer.entity.l2cache:false}")
    protected Boolean removeOrderCustomerEntityL2Cache;
    
    public RemoveCacheClassTransformer(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        // When simply copying properties over for Java class files, JPA properties do not need modification
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // Lambdas and anonymous methods in Java 8 do not have a class name defined and so no transformation should be done
        if (className == null) {
            return null;
        }

        //Be careful with Apache library usage in this class (e.g. ArrayUtils). Usage will likely cause a ClassCircularityError
        //under JRebel. Favor not including outside libraries and unnecessary classes.
        CtClass clazz = null;
        try {
            String convertedClassName = className.replace('/', '.');
            
            // If there is a class list specified, make sure the current class qualifies to have the annotation removed
            if (removeOrderCustomerEntityL2Cache && !cacheRegions.isEmpty() && classQualifies(convertedClassName)) {
                ClassPool classPool = ClassPool.getDefault();
                clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
                clazz.defrost();
                ClassFile classFile = clazz.getClassFile();
                ConstPool constantPool = classFile.getConstPool();
                List<?> classAttributes = classFile.getAttributes();

                // Only remove the annotation if it is for one of the specified regions
                if (annotationQualifies(constantPool, classAttributes)) {
                    AnnotationsAttribute classAnnotationsAttribute = stripAnnotation(constantPool, classAttributes);
                    classFile.addAttribute(classAnnotationsAttribute);

                    List<FieldInfo> fieldInfos = classFile.getFields();
                    for (FieldInfo myField : fieldInfos) {
                        List<?> attributes = myField.getAttributes();
                        AnnotationsAttribute fieldAnnotationsAttribute = stripAnnotation(constantPool, attributes);
                        myField.addAttribute(fieldAnnotationsAttribute);
                    }

                    return clazz.toBytecode();
                    
                }
            }
        } catch (ClassCircularityError error) {
            error.printStackTrace();
            throw error;
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform class", e);
        } finally {
            if (clazz != null) {
                try {
                    clazz.detach();
                } catch (Exception e) {
                    //do nothing
                }
            }
        }

        return null;
    }

    // Checks that the passes class name qualifies to be transformed
    protected Boolean classQualifies(String className) {
        Boolean classQualifies = classNames.isEmpty() || ( !classNames.isEmpty() && classNames.contains(className) );
        return classQualifies;
    }

    // Checks is the annotation matches the "annotationClass"
    protected Boolean annotationQualifies(ConstPool constantPool, List<?> attributes) {
        boolean qualifies = false;
        
        Iterator<?> itr = attributes.iterator();        
        while (itr.hasNext()) {
            Object object = itr.next();
            if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                AnnotationsAttribute attr = (AnnotationsAttribute) object;
                Annotation[] items = attr.getAnnotations();
                for (Annotation annotation : items) {
                    String typeName = annotation.getTypeName();
                    if (typeName.equals(annotationClass) && cacheRegionQualifies(annotation)) {
                        qualifies = true;
                        break;
                    }
                }
            }
        }
        return qualifies;        
    }

    // Checks that the Annotation "region" is in the list of cacheRegions to be removed
    protected Boolean cacheRegionQualifies(Annotation annotation) {
        MemberValue memberValue = annotation.getMemberValue("region");
        if (StringMemberValue.class.isAssignableFrom(memberValue.getClass())) {
            StringMemberValue smv = (StringMemberValue) memberValue;
            if (cacheRegions.contains(smv.getValue())) {
                return true;
            }
        }
        return false;
    }

    // Assumes the annotation qualifies to be removed (e.g. the annotationQualifies has already been verified)
    protected AnnotationsAttribute stripAnnotation(ConstPool constantPool, List<?> attributes) {
        Iterator<?> itr = attributes.iterator();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);

        while (itr.hasNext()) {
            Object object = itr.next();
            if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                AnnotationsAttribute attr = (AnnotationsAttribute) object;
                Annotation[] items = attr.getAnnotations();
                for (Annotation annotation : items) {
                    String typeName = annotation.getTypeName();
                    if (typeName.equals(annotationClass)) {
                        logger.debug(String.format("Stripping out %s annotation", annotationClass));
                        continue;
                    }
                    annotationsAttribute.addAnnotation(annotation);
                }
                itr.remove();
            }
        }
        return annotationsAttribute;
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

    /**
     * The list of fully-qualified classes to be impacted by this transformer
     *
     * @return
     */
    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }

    /**
     * The list of cache region names to be removed by this transformer
     *
     * @return
     */
    public List<String> getCacheRegions() {
        return cacheRegions;
    }

    public void setCacheRegions(List<String> cacheRegions) {
        this.cacheRegions = cacheRegions;
    }
    
    /**
     * The fully-qualified classname of the annotation to remove at the class and field level
     *
     * @return
     */
    public String getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(String annotationClass) {
        this.annotationClass = annotationClass;
    }

}

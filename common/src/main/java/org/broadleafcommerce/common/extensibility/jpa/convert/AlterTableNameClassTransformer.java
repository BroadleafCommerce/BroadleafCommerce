/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa.convert;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.copy.AbstractClassTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Table;

import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * This {@link BroadleafClassTransformer} changes the name of the Table for an
 * entity before Hibernate sees it. This allows us to safely change/alter the names
 * of Tables for entities on patch releases.
 * <p>
 * Example of changing the Table name of {@link Product} to "BLC_ALTER_PRODUCT"
 * <p>
 * In applicationContext.xml
 * <p>
 * ```xml
 * <bean id="blAlterProductTableClassTransformer" class="org.broadleafcommerce.common.extensibility.jpa.convert.AlterTableNameClassTransformer">
 * <constructor-arg name="tableName" value="BLC_ALTER_PRODUCT" />
 * <constructor-arg name="targetedClass" value="org.broadleafcommerce.core.catalog.domain.ProductImpl" />
 * </bean>
 * <bean id="customClassTransformers" class="org.springframework.beans.factory.config.ListFactoryBean">
 * <property name="sourceList">
 * <list>
 * <ref bean="blAlterProductTableClassTransformer" />
 * </list>
 * </property>
 * </bean>
 * <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 * <property name="collectionRef" value="customClassTransformers" />
 * <property name="targetRef" value="blMergedClassTransformers" />
 * <property name="placement" value="SPECIFIC"/>
 * <property name="position" value="0"/>
 * </bean>
 * ```
 * <p>
 * NOTE :* This will add a new table in the database and does *NOT* change the name of existing tables in a database.
 * <p>
 * Created by ReggieCole on 4/3/17.
 */
public class AlterTableNameClassTransformer extends AbstractClassTransformer implements BroadleafClassTransformer {

    private static final Log LOG = LogFactory.getLog(AlterTableNameClassTransformer.class);

    protected String tableName;

    protected String targetedClass;

    public AlterTableNameClassTransformer() {
        this(null, null);
    }

    public AlterTableNameClassTransformer(String tableName) {
        this(tableName, null);
    }

    public AlterTableNameClassTransformer(String tableName, String targetedClass) {
        this.tableName = tableName;
        this.targetedClass = targetedClass;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Constructed table name Transformer. Targeted Class:" + targetedClass + " Table Name: " + tableName);
        }
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        //do nothing
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // Lambdas and anonymous methods in Java 8 do not have a class name defined and so no transformation should be done
        if (className == null || StringUtils.isBlank(getTargetedClass()) || StringUtils.isBlank(getTableName())) {
            return null;
        }

        byte[] classBytes = null;
        String convertedClassName = className.replace('/', '.');
        if (convertedClassName.equalsIgnoreCase(getTargetedClass())) {
            try {

                String targetValue = getTableName();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Altering " + convertedClassName + " table name");
                }
                ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
                ConstPool constantPool = classFile.getConstPool();

                alterTableAnnotation(classFile, targetValue, constantPool);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bos);
                classFile.write(os);
                os.close();

                classBytes = bos.toByteArray();

            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IllegalClassFormatException("Unable to convert " + convertedClassName + " to a SingleTable inheritance strategy: " + ex.getMessage());
            }
        }
        return classBytes;
    }


    /**
     * Build class-level annotations from a template class
     *
     * @param classFile
     * @param tableName
     * @param constantPool
     * @throws NotFoundException
     */
    protected void alterTableAnnotation(ClassFile classFile, String tableName, ConstPool constantPool) throws NotFoundException {
        List<?> classFileAttributes = classFile.getAttributes();
        Iterator<?> classItr = classFileAttributes.iterator();
        AnnotationsAttribute attr = null;
        while (classItr.hasNext()) {
            Object object = classItr.next();
            if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                attr = (AnnotationsAttribute) object;
                Annotation[] items = attr.getAnnotations();
                for (Annotation annotation : items) {
                    String typeName = annotation.getTypeName();
                    if (typeName.equals(Table.class.getName())) {
                        Set<String> keys = annotation.getMemberNames();
                        for (String key : keys) {
                            if (key.equalsIgnoreCase("name")) {
                                StringMemberValue value = (StringMemberValue) annotation.getMemberValue(key);
                                String oldTableName = value.getValue();
                                value.setValue(tableName);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Altering " + classFile.getName() + " table name from: " + oldTableName + "" +
                                            " to: " + value.getValue());
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                attr.setAnnotations(items);
                break;
            }
        }
        if (attr != null) {
            classItr.remove();
            classFile.addAttribute(attr);
        }

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTargetedClass() {
        return targetedClass;
    }

    public void setTargetedClass(String targetedClass) {
        this.targetedClass = targetedClass;
    }
}

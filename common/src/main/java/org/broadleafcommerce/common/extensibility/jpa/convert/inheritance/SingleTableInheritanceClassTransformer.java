/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa.convert.inheritance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.extensibility.jpa.copy.AbstractClassTransformer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javassist.ClassPool;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 
 * @author jfischer
 *
 */
public class SingleTableInheritanceClassTransformer extends AbstractClassTransformer implements BroadleafClassTransformer {
    
    public static final String SINGLE_TABLE_ENTITIES = "broadleaf.ejb.entities.override_single_table";
    
    private static final Log LOG = LogFactory.getLog(SingleTableInheritanceClassTransformer.class);
    protected List<SingleTableInheritanceInfo> infos = new ArrayList<SingleTableInheritanceInfo>();

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        if (((String) key).equals(SINGLE_TABLE_ENTITIES)) {
            String[] classes = StringUtils.tokenizeToStringArray(props.getProperty((String) key), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            for (String clazz : classes) {
                String keyName;
                int pos = clazz.lastIndexOf(".");
                if (pos >= 0) {
                    keyName = clazz.substring(pos + 1, clazz.length());
                } else {
                    keyName = clazz;
                }
                SingleTableInheritanceInfo info = new SingleTableInheritanceInfo();
                info.setClassName(clazz);
                String discriminatorName = props.getProperty("broadleaf.ejb."+keyName+".discriminator.name");
                if (discriminatorName != null) {
                    info.setDiscriminatorName(discriminatorName);
                    String type = props.getProperty("broadleaf.ejb."+keyName+".discriminator.type");
                    if (type != null) {
                        info.setDiscriminatorType(DiscriminatorType.valueOf(type));
                    }
                    String length = props.getProperty("broadleaf.ejb."+keyName+".discriminator.length");
                    if (length != null) {
                        info.setDiscriminatorLength(Integer.parseInt(length));
                    }
                }
                infos.remove(info);
                infos.add(info);
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // Lambdas and anonymous methods in Java 8 do not have a class name defined and so no transformation should be done
        if (className == null) {
            return null;
        }
        
        if (infos.isEmpty()) {
            return null;
        }
        String convertedClassName = className.replace('/', '.');
        SingleTableInheritanceInfo key = new SingleTableInheritanceInfo();
        key.setClassName(convertedClassName);
        int pos = infos.indexOf(key);
        if (pos >= 0) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Converting " + convertedClassName + " to a SingleTable inheritance strategy."); 
                }
                SingleTableInheritanceInfo myInfo = infos.get(pos);
                ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
                ConstPool constantPool = classFile.getConstPool();
                AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
                List<?> attributes = classFile.getAttributes();
                Iterator<?> itr = attributes.iterator();
                while(itr.hasNext()) {
                    Object object = itr.next();
                    if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                        AnnotationsAttribute attr = (AnnotationsAttribute) object;
                        Annotation[] items = attr.getAnnotations();
                        for (Annotation annotation : items) {
                            String typeName = annotation.getTypeName();
                            if (!typeName.equals(Inheritance.class.getName())) {
                                annotationsAttribute.addAnnotation(annotation);
                            }
                        }
                        itr.remove();
                    }
                }
                Annotation inheritance = new Annotation(Inheritance.class.getName(), constantPool);
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("javax.persistence");
                pool.importPackage("java.lang");
                EnumMemberValue strategy = (EnumMemberValue) Annotation.createMemberValue(constantPool, pool.makeClass("InheritanceType"));
                strategy.setType(InheritanceType.class.getName());
                strategy.setValue(InheritanceType.SINGLE_TABLE.name());
                inheritance.addMemberValue("strategy", strategy);
                annotationsAttribute.addAnnotation(inheritance);
                if (myInfo.getDiscriminatorName() != null) {
                    Annotation discriminator = new Annotation(DiscriminatorColumn.class.getName(), constantPool);
                    StringMemberValue name = new StringMemberValue(constantPool);
                    name.setValue(myInfo.getDiscriminatorName());
                    discriminator.addMemberValue("name", name);
                    EnumMemberValue discriminatorType = (EnumMemberValue) Annotation.createMemberValue(constantPool, pool.makeClass("DiscriminatorType"));
                    discriminatorType.setType(DiscriminatorType.class.getName());
                    discriminatorType.setValue(myInfo.getDiscriminatorType().name());
                    discriminator.addMemberValue("discriminatorType", discriminatorType);
                    IntegerMemberValue length = new IntegerMemberValue(constantPool);
                    length.setValue(myInfo.getDiscriminatorLength());
                    discriminator.addMemberValue("length", length);
                    
                    annotationsAttribute.addAnnotation(discriminator);
                }
                classFile.addAttribute(annotationsAttribute);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bos);
                classFile.write(os);
                os.close();

                return bos.toByteArray();
            } catch(Exception ex) {
                ex.printStackTrace();
                throw new IllegalClassFormatException("Unable to convert " + convertedClassName + " to a SingleTable inheritance strategy: " + ex.getMessage());
            }
        } else {
            return null;
        }
    }
    
}

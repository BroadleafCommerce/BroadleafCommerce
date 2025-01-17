/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyIgnorePattern;
import org.hibernate.annotations.Type;
import org.hibernate.type.MaterializedClobType;
import org.springframework.stereotype.Component;

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

import javax.annotation.Resource;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Broadleaf defines the Hibernate type for Clob fields as {@link StringClobType}. This has been deprecated in favor of
 * {@link MaterializedClobType}. However, this is not a panacea, as this can map to the wrong type for Postgres. However,
 * this mapping is correct for Oracle.
 * </p>
 * The main reason to switch to MaterializedClobType is because it avoids the use of rs.getCharacterStream on the clob column.
 * Opening the character stream causes the Oracle jdbc driver to allocate a very large buffer array, which contributes to
 * wasteful short-term memory allocation.
 * </p>
 *
 * <p>
 * Hibernate 5.2 completely removed the org.hibernate.type.StringClobType that was used in all {literal @}Lob fields
 * within Broadleaf. In order to improve forwards-compatiblity, this finds any instances in entities that
 * were using StringClobType for {@literal @}Lob {@literal @}Type and swaps it out with org.hibernate.type.MaterializedClobType
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blMaterializedClobTypeClassTransformer")
public class MaterializedClobTypeClassTransformer implements BroadleafClassTransformer {

    private static final Log logger = LogFactory.getLog(MaterializedClobTypeClassTransformer.class);

    @Resource(name = "blDirectCopyIgnorePatterns")
    protected List<DirectCopyIgnorePattern> ignorePatterns = new ArrayList<DirectCopyIgnorePattern>();

    @Override
    public void compileJPAProperties(Properties properties, Object o) throws Exception {
        //do nothing
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }
        String convertedClassName = className.replace('/', '.');
        if (isIgnored(convertedClassName)) {
            return null;
        }
        try {
            boolean transformed = false;
            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
            boolean containsTypeLevelAnnotation = false;
            {
                List<?> attributes = classFile.getAttributes();
                Iterator<?> itr = attributes.iterator();
                while (itr.hasNext()) {
                    Object object = itr.next();
                    if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                        containsTypeLevelAnnotation = containsTypeLevelPersistenceAnnotation(((AnnotationsAttribute) object).getAnnotations());
                    }
                }
            }
            if (containsTypeLevelAnnotation) {
                List<FieldInfo> fieldInfos = classFile.getFields();
                ConstPool constantPool = classFile.getConstPool();
                for (FieldInfo field : fieldInfos) {
                    List<?> attributes = field.getAttributes();
                    Iterator<?> itr = attributes.iterator();
                    AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
                    while (itr.hasNext()) {
                        Object object = itr.next();
                        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                            AnnotationsAttribute attr = (AnnotationsAttribute) object;
                            Annotation[] items = attr.getAnnotations();
                            for (Annotation annotation : items) {
                                String typeName = annotation.getTypeName();
                                if (typeName.equals(Type.class.getName())) {
                                    StringMemberValue annot = (StringMemberValue) annotation.getMemberValue("type");
                                    if (annot != null && annot.getValue().equals("org.hibernate.type.StringClobType")) {
                                        if (!convertedClassName.startsWith("org.broadleafcommerce") || !convertedClassName.startsWith("com.broadleafcommerce")) {
                                            logger.warn(String.format("org.hibernate.type.StringClobType found on %s#%s which is no longer a class in Hibernate, automatically replacing with org.hibernate.type.MaterializedClobType. Please replace"
                                                + " all instances of @Type(type = \"org.hibernate.type.StringClobType\") with @Type(type = \"org.hibernate.type.MaterializedClobType\")",
                                                convertedClassName, field.getName()));
                                        } else {
                                            logger.debug(String.format("org.hibernate.type.StringClobType found on %s#%s which is no longer a class in Hibernate, automatically replacing with.",
                                                convertedClassName, field.getName()));
                                        }

                                        Annotation clobType = new Annotation(Type.class.getName(), constantPool);
                                        StringMemberValue type = new StringMemberValue(constantPool);
                                        type.setValue(MaterializedClobType.class.getName());
                                        clobType.addMemberValue("type", type);
                                        annotationsAttribute.addAnnotation(clobType);
                                        transformed = true;
                                    } else {
                                        annotationsAttribute.addAnnotation(annotation);
                                    }
                                } else {
                                    annotationsAttribute.addAnnotation(annotation);
                                }
                            }
                            if (transformed) {
                                itr.remove();
                            }
                        }
                    }
                    if (transformed) {
                        field.addAttribute(annotationsAttribute);
                    }
                }
            }

            if (transformed) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bos);
                classFile.write(os);
                os.close();

                return bos.toByteArray();
            } else {
                return null;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new IllegalClassFormatException("Unable to convert " + convertedClassName + " to sandbox: " + ex.getMessage());
        }
    }

    protected boolean isIgnored(String convertedClassName) {
        boolean isValidPattern = true;
        List<DirectCopyIgnorePattern> matchedPatterns = new ArrayList<DirectCopyIgnorePattern>();
        for (DirectCopyIgnorePattern pattern : ignorePatterns) {
            boolean isPatternMatch = false;
            for (String patternString : pattern.getPatterns()) {
                isPatternMatch = convertedClassName.matches(patternString);
                if (isPatternMatch) {
                    break;
                }
            }
            if (isPatternMatch) {
                matchedPatterns.add(pattern);
            }
            isValidPattern = !(isPatternMatch && pattern.getTemplateTokenPatterns() == null);
            if (!isValidPattern) {
                break;
            }
        }

        return !isValidPattern;
    }

    protected boolean containsTypeLevelPersistenceAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.getTypeName().equals(Entity.class.getName())
                    || annotation.getTypeName().equals(Embeddable.class.getName())
                    || annotation.getTypeName().equals(MappedSuperclass.class.getName())) {
                return true;
            }
        }
        return false;
    }
}

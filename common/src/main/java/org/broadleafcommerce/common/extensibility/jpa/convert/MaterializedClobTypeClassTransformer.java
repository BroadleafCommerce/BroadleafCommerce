/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa.convert;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyIgnorePattern;
import org.hibernate.annotations.Type;
import org.hibernate.type.MaterializedClobType;
import org.hibernate.type.StringClobType;

import javax.annotation.Resource;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
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

/**
 * Broadleaf defines the Hibernate type for Clob fields as {@link StringClobType}. This has been deprecated in favor of
 * {@link MaterializedClobType}. However, this is not a panacea, as this can map to the wrong type for Postgres. However,
 * this mapping is correct for Oracle.
 * </p>
 * The main reason to switch to MaterializedClobType is because it avoids the use of rs.getCharacterStream on the clob column.
 * Opening the character stream causes the Oracle jdbc driver to allocate a very large buffer array, which contributes to
 * wasteful short-term memory allocation.
 * </p>
 * Add this class transformer to any implementation by adding it to the blMergedClassTransformers list in app context:
 * <pre>
 * {@code
 * <bean id="myMaterializedClobTypeClassTransfomer" class="org.broadleafcommerce.common.extensibility.jpa.convert.MaterializedClobTypeClassTransformer"/>
 * <bean id="myClassTransformers" class="org.springframework.beans.factory.config.ListFactoryBean">
 *     <property name="sourceList">
 *         <list>
 *             <ref bean="myMaterializedClobTypeClassTransfomer"/>
 *         </list>
 *     </property>
 * </bean>
 * <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 *     <property name="collectionRef" value="myClassTransformers" />
 *     <property name="targetRef" value="blMergedClassTransformers" />
 * </bean>
 * }
 * </pre>
 *
 * @author Jeff Fischer
 */
public class MaterializedClobTypeClassTransformer implements BroadleafClassTransformer {

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
                for (FieldInfo myField : fieldInfos) {
                    List<?> attributes = myField.getAttributes();
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
                                    if (annot != null && annot.getValue().equals(StringClobType.class.getName())) {
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
                        myField.addAttribute(annotationsAttribute);
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

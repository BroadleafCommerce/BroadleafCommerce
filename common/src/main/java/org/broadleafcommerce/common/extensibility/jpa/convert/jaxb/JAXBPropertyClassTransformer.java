/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extensibility.jpa.convert.jaxb;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * This class allows us to remove JAXB entity and attribute annotations at runtime, and replace them
 * with javax.xml.bind.annotation.XmlTransient to suppress those properties from being serialized
 * in the case where entities are being serialized / deserialized for web services or using JAXB.
 * <p/>
 * We are putting this logic inside a JPA class transformer for 2 reasons.  First, we are manipulating
 * Broadleaf entity classes, which also have JAXB mappings.  Second, JPA already provides a very
 * convenient hook to do instrumentation.
 * <p/>
 * User: Kelly Tisdell
 */
public class JAXBPropertyClassTransformer implements BroadleafClassTransformer {

    private static final Log LOG = LogFactory.getLog(JAXBPropertyClassTransformer.class);
    private static final String OMIT_KEY_PREFIX = "broadleaf.ejb.entities.jaxb.omit_properties.";
    private static final String ADD_KEY_PREFIX = "broadleaf.ejb.entities.jaxb.add_properties.";

    protected Map<String, HashSet<String>> omitClassInfo = new HashMap<String, HashSet<String>>();
    protected Map<String, HashSet<String>> addClassInfo = new HashMap<String, HashSet<String>>();

    @Override
    @SuppressWarnings("unchecked")
    public byte[] transform(ClassLoader classLoader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (omitClassInfo.isEmpty() && addClassInfo.isEmpty()) {
            return null;
        }
        String convertedClassName = className.replace('/', '.');

        try {

            //First, let's deal with omits
            HashSet<String> omitMethodNames = omitClassInfo.get(convertedClassName);
            HashSet<String> addMethodNames = omitClassInfo.get(convertedClassName);
            if ((omitMethodNames == null || omitMethodNames.isEmpty()) && (addMethodNames == null || addMethodNames.isEmpty())) {
                return null;
            }


            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
            ConstPool constantPool = classFile.getConstPool();

            if (omitMethodNames != null) {
                //First, let's deal with omits...
                for (String methodName : omitMethodNames) {
                    MethodInfo methodInfo = resolveMethodInfo(classFile, methodName);
                    if (methodInfo == null) {
                        //We still haven't found a method.
                        //Log it an continue on.
                        LOG.error("Error finding property of method associated with name "
                                + methodName + " in class " + convertedClassName + ". Ignoring.");
                        continue;
                    }
                    AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
                    Annotation transientAnnotation = new Annotation(XmlTransient.class.getName(), constantPool);
                    annotationsAttribute.addAnnotation(transientAnnotation);

                    List<?> methodAttributes = methodInfo.getAttributes();
                    Iterator<?> itr = methodAttributes.iterator();
                    while (itr.hasNext()) {
                        Object object = itr.next();
                        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                            AnnotationsAttribute attr = (AnnotationsAttribute) object;
                            Annotation[] items = attr.getAnnotations();
                            for (Annotation annotation : items) {
                                String typeName = annotation.getTypeName();
                                if (!typeName.startsWith("javax.xml.bind.annotation")) {
                                    annotationsAttribute.addAnnotation(annotation);
                                }
                            }
                            itr.remove();
                        }
                    }
                    methodInfo.getAttributes().add(annotationsAttribute);
                }
            }

            //Now let's deal with additions
            if (addMethodNames != null) {
                for (String methodName : addMethodNames) {
                    MethodInfo methodInfo = resolveMethodInfo(classFile, methodName);
                    if (methodInfo == null) {
                        //We still haven't found a method.
                        //Log it an continue on.
                        LOG.error("Error finding property of method associated with name "
                                + methodName + " in class " + convertedClassName + ". Ignoring.");
                        continue;
                    }
                    AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);

                    List<?> methodAttributes = methodInfo.getAttributes();
                    Iterator<?> itr = methodAttributes.iterator();
                    while (itr.hasNext()) {
                        Object object = itr.next();
                        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                            AnnotationsAttribute attr = (AnnotationsAttribute) object;
                            Annotation[] items = attr.getAnnotations();
                            boolean foundXmlDescriptors = false;
                            for (Annotation annotation : items) {
                                String typeName = annotation.getTypeName();
                                if (typeName.startsWith("javax.xml.bind.annotation")) {
                                    //This is a JAXB annotation. But what kind?
                                    //If it's XMLTransient, then we don't want to add it
                                    if (!typeName.equals(XmlTransient.class.getName())){
                                        if (typeName.equals(XmlElement.class.getName()) || typeName.equals(XmlAttribute.class.getName())){
                                            //If we find an XMLElement or XMLAttribute annotation, keep track of it so we don't additionally add it.
                                            foundXmlDescriptors = true;
                                        }
                                        annotationsAttribute.addAnnotation(annotation);
                                    }
                                } else {
                                    //This has nothing to do with JAXB, so don't interfere with it
                                    annotationsAttribute.addAnnotation(annotation);
                                }
                            }
                            itr.remove();

                            if (!foundXmlDescriptors) {
                                //We didn't find an XMLAttribute or XMLElement annotation, so add one.
                                Annotation elementAnnotation = new Annotation(XmlElement.class.getName(), constantPool);
                                annotationsAttribute.addAnnotation(elementAnnotation);
                            }
                        }
                    }

                    methodInfo.getAttributes().add(annotationsAttribute);
                }
            }


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(bos);
            classFile.write(os);
            os.close();

            return bos.toByteArray();

        } catch (Exception e) {
            LOG.error("Unable to convert class " + convertedClassName + ", to modify JAXB annotations", e);
            throw new IllegalClassFormatException("Unable to convert " + convertedClassName + ", to modify JAXB annotations" + e.getMessage());
        }
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        if (!(key instanceof String)) {
            return;
        }
        String keyString = (String) key;
        if (keyString.startsWith(OMIT_KEY_PREFIX)) {
            String className = keyString.substring(OMIT_KEY_PREFIX.length() + 1);

            String[] getterNames = StringUtils.tokenizeToStringArray(props.getProperty(keyString), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            if (getterNames != null && getterNames.length > 0) {
                if (omitClassInfo.get(className) == null) {
                    omitClassInfo.put(className, new HashSet<String>());
                }

                for (String getterName : getterNames) {
                    //Check to make sure this hasn't been added to the omit collection
                    if (addClassInfo.get(className) != null && addClassInfo.get(className).contains(getterName)) {
                        throw new IllegalClassFormatException("You cannot add and exclude the same property from being serialized by JAXB: "
                                + className + "." + getterName + " - Check the configuration.");
                    }

                    omitClassInfo.get(className).add(getterName);
                }
            }
        } else if (keyString.startsWith(ADD_KEY_PREFIX)) {
            String className = keyString.substring(ADD_KEY_PREFIX.length() + 1);

            String[] getterNames = StringUtils.tokenizeToStringArray(props.getProperty(keyString), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            if (getterNames != null && getterNames.length > 0) {
                if (addClassInfo.get(className) == null) {
                    addClassInfo.put(className, new HashSet<String>());
                }

                for (String getterName : getterNames) {
                    //Check to make sure this hasn't been added to the omit collection
                    if (omitClassInfo.get(className) != null && omitClassInfo.get(className).contains(getterName)) {
                        throw new IllegalClassFormatException("You cannot add and exclude the same property from being serialized by JAXB: "
                                + className + "." + getterName + " - Check the configuration.");
                    }

                    addClassInfo.get(className).add(getterName);
                }
            }
        }
    }
    
    private MethodInfo resolveMethodInfo (ClassFile classFile, String methodName) {
        MethodInfo methodInfo = classFile.getMethod(methodName);
        if (methodInfo == null) {
            methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
            String tmpMethodName = "get" + methodName;

            methodInfo = classFile.getMethod(tmpMethodName);

            if (methodInfo == null) {
                tmpMethodName = "is" + methodName;
                methodInfo = classFile.getMethod(tmpMethodName);
            }
        }
        return methodInfo;
    }
}

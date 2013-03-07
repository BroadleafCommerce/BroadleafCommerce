/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.common.extensibility.jpa.copy;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.logging.LifeCycleEvent;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class transformer will copy fields, methods, and interface definitions from a source class to a target class,
 * based on the xformTemplates map. It will fail if it encouters any duplicate definitions.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DirectCopyClassTransformer implements BroadleafClassTransformer {
    protected SupportLogger logger;
    
    protected String moduleName;
    protected Map<String, String> xformTemplates = new HashMap<String, String>();
    
    protected static List<String> transformedMethods = new ArrayList<String>();
    
    public DirectCopyClassTransformer(String moduleName) {
        this.moduleName = moduleName;
        logger = SupportLogManager.getLogger(moduleName, this.getClass());
    }
    
    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        // When simply copying properties over for Java class files, JPA properties do not need modification
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, 
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String convertedClassName = className.replace('/', '.');
        
        if (xformTemplates.containsKey(convertedClassName)) {
            String xformKey = convertedClassName;
            String xformVal = xformTemplates.get(xformKey);
            logger.lifecycle(LifeCycleEvent.START, String.format("Transform - Copying into [%s] from [%s]", xformKey, xformVal));
            
            try {
                // Load the destination class and defrost it so it is eligible for modifications
                ClassPool classPool = ClassPool.getDefault();
                CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
                clazz.defrost();
                
                // Load the source class
                classPool.appendClassPath(new LoaderClassPath(Class.forName(xformVal).getClassLoader()));
                CtClass template = classPool.get(xformVal);
                
                // Add in extra interfaces
                CtClass[] interfacesToCopy = template.getInterfaces();
                for (CtClass i : interfacesToCopy) {
                    logger.debug(String.format("Adding interface [%s]", i.getName()));
                    clazz.addInterface(i);
                }

                /*// Add extra class level annotations
                ClassFile templateFile = template.getClassFile();
                ConstPool templateConstantPool = templateFile.getConstPool();
                ClassPool pool = ClassPool.getDefault();
                AnnotationsAttribute templateAnnotationsAttribute = new AnnotationsAttribute(templateConstantPool, AnnotationsAttribute.visibleTag);
                List<?> templateAttributes = templateFile.getAttributes();
                for (Object object : templateAttributes) {
                    if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                        AnnotationsAttribute attr = (AnnotationsAttribute) object;
                        Annotation[] items = attr.getAnnotations();
                        for (Annotation legacyAnnotation : items) {
                            templateAnnotationsAttribute.addAnnotation(legacyAnnotation);
                        }
                    }
                }
                Annotation[] annotationsToCopy = templateAnnotationsAttribute.getAnnotations();
                if (!ArrayUtils.isEmpty(annotationsToCopy)) {
                    ClassFile classFile = clazz.getClassFile();
                    ConstPool constantPool = classFile.getConstPool();
                    AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
                    List<?> attributes = classFile.getAttributes();
                    Iterator<?> itr = attributes.iterator();
                    while(itr.hasNext()) {
                        Object object = itr.next();
                        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                            AnnotationsAttribute attr = (AnnotationsAttribute) object;
                            Annotation[] items = attr.getAnnotations();
                            for (Annotation legacyAnnotation : items) {
                                annotationsAttribute.addAnnotation(legacyAnnotation);
                            }
                            itr.remove();
                        }
                    }
                    Annotation[] legacyAnnotations = annotationsAttribute.getAnnotations();
                    for (Object copyVal : annotationsToCopy) {
                        Annotation annotation = (Annotation) copyVal;
                        boolean isFound = false;
                        for (Annotation legacyAnnotation : legacyAnnotations) {
                            if (legacyAnnotation.getTypeName().equals(annotation.getTypeName())) {
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            logger.debug(String.format("Adding annotation [%s]", annotation.getTypeName()));
                            pool.importPackage(annotation.getTypeName());
                            annotationsAttribute.addAnnotation(annotation);
                        }
                    }
                    classFile.addAttribute(annotationsAttribute);
                }*/
                
                // Copy over all declared fields from the template class
                // Note that we do not copy over fields with the @NonCopiedField annotation
                CtField[] fieldsToCopy = template.getDeclaredFields();
                for (CtField field : fieldsToCopy) {
                    if (field.hasAnnotation(NonCopied.class)) {
                        logger.debug(String.format("Not adding field [%s]", field.getName()));
                    } else {
                        logger.debug(String.format("Adding field [%s]", field.getName()));
                        CtField copiedField = new CtField(field, clazz);

                        boolean defaultConstructorFound = false;

                        String implClass = getImplementationType(field.getType().getName());

                        // Look through all of the constructors in the implClass to see 
                        // if there is one that takes zero parameters
                        try {
                            CtConstructor[] implConstructors = classPool.get(implClass).getConstructors();
                            if (implConstructors != null) {
                                for (CtConstructor cons : implConstructors) {
                                    if (cons.getParameterTypes().length == 0) {
                                        defaultConstructorFound = true;
                                        break;
                                    }
                                }
                            }
                        } catch (NotFoundException e) {
                            // Do nothing -- if we don't find this implementation, it's probably because it's
                            // an array. In this case, we will not initialize the field.
                        }

                        if (defaultConstructorFound) {
                            clazz.addField(copiedField, "new " + implClass + "()");
                        } else {
                            clazz.addField(copiedField);
                        }
                    }
                }
                
                // Copy over all declared methods from the template class
                CtMethod[] methodsToCopy = template.getDeclaredMethods();
                for (CtMethod method : methodsToCopy) {
                    if (method.hasAnnotation(NonCopied.class)) {
                        logger.debug(String.format("Not adding method [%s]", method.getName()));
                    } else {
                        try {
                            CtClass[] paramTypes = method.getParameterTypes();
                            CtMethod originalMethod = clazz.getDeclaredMethod(method.getName(), paramTypes);
                            
                            if (transformedMethods.contains(methodDescription(originalMethod))) {
                                throw new RuntimeException("Method already replaced " + methodDescription(originalMethod));
                            } else {
                                logger.debug(String.format("Marking as replaced [%s]", methodDescription(originalMethod)));
                                transformedMethods.add(methodDescription(originalMethod));
                            }
                            
                            logger.debug(String.format("Removing method [%s]", method.getName()));
                            clazz.removeMethod(originalMethod);
                        } catch (NotFoundException e) {
                            // Do nothing -- we don't need to remove a method because it doesn't exist
                        }
                        
                        logger.debug(String.format("Adding method [%s]", method.getName()));
                        CtMethod copiedMethod = new CtMethod(method, clazz, null);
                        clazz.addMethod(copiedMethod);
                    }
                }
                
                logger.debug(String.format("END - Transform - Copying into [%s] from [%s]", xformKey, xformVal));
                return clazz.toBytecode();
            } catch (Exception e) {
                throw new RuntimeException("Unable to transform class", e);
            }
        }
        
        return null;
    }
    
    /**
     * This method will do its best to return an implementation type for a given classname. This will allow weaving
     * template classes to have initialized values.
     * 
     * We provide default implementations for List, Map, and Set, and will attempt to utilize a default constructor for
     * other classes.
     * 
     * If the className contains an '[', we will return null.
     */
    protected String getImplementationType(String className) {
        if (className.equals("java.util.List")) {
            return "java.util.ArrayList";
        } else if (className.equals("java.util.Map")) {
            return "java.util.HashMap";
        } else if (className.equals("java.util.Set")) {
            return "java.util.HashSet";
        } else if (className.contains("[")) {
            return null;
        }

        return className;
    }

    protected String methodDescription(CtMethod method) {
        return method.getDeclaringClass().getName() + "|" + method.getName() + "|" + method.getSignature();
    }
    
    public Map<String, String> getXformTemplates() {
        return xformTemplates;
    }

    public void setXformTemplates(Map<String, String> xformTemplates) {
        this.xformTemplates = xformTemplates;
    }
    
}
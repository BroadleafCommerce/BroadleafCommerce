/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa.copy;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityListeners;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.logging.LifeCycleEvent;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;

/**
 * This class transformer will copy fields, methods, and interface definitions from a source class to a target class,
 * based on the xformTemplates map. It will fail if it encounters any duplicate definitions.
 *
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 */
public class DirectCopyClassTransformer implements BroadleafClassTransformer {

    protected static List<String> transformedMethods = new ArrayList<String>();
    protected static List<String> annotationTransformedClasses = new ArrayList<String>();

    protected SupportLogger logger;
    protected String moduleName;
    protected Map<String, String> xformTemplates = new HashMap<String, String>();
    protected Boolean renameMethodOverlaps = false;
    protected String renameMethodPrefix = "__";
    protected Boolean skipOverlaps = false;
    protected Map<String, String> templateTokens = new HashMap<String, String>();

    @Resource(name="blDirectCopyIgnorePatterns")
    protected List<String> ignorePatterns = new ArrayList<String>();

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
        //Be careful with Apache library usage in this class (e.g. ArrayUtils). Usage will likely cause a ClassCircularityError
        //under JRebel. Favor not including outside libraries and unnecessary classes.
        try {
            boolean mySkipOverlaps = skipOverlaps;
            boolean myRenameMethodOverlaps = renameMethodOverlaps;
            String convertedClassName = className.replace('/', '.');
            ClassPool classPool = null;
            CtClass clazz = null;
            String xformKey = convertedClassName;
            String[] xformVals = null;
            Boolean[] xformSkipOverlaps = null;
            Boolean[] xformRenameMethodOverlaps = null;
            if (!xformTemplates.isEmpty()) {
                if (xformTemplates.containsKey(xformKey)) {
                    xformVals = xformTemplates.get(xformKey).split(",");
                    classPool = ClassPool.getDefault();
                    clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
                }
            } else {
                if (annotationTransformedClasses.contains(convertedClassName)) {
                    logger.warn(convertedClassName + " has already been transformed by a previous instance of DirectCopyTransfomer. " +
                            "Skipping this annotation based transformation. Generally, annotation-based transformation is handled " +
                            "by bean id blAnnotationDirectCopyClassTransformer with template tokens being added to " +
                            "blDirectCopyTransformTokenMap via EarlyStageMergeBeanPostProcessor.");
                }
                boolean isValidPattern = true;
                for (String pattern : ignorePatterns) {
                    isValidPattern = !convertedClassName.matches(pattern);
                    if (!isValidPattern) {
                        return null;
                    }
                }
                if (isValidPattern) {
                    classPool = ClassPool.getDefault();
                    clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
                    List<?> attributes = clazz.getClassFile().getAttributes();
                    Iterator<?> itr = attributes.iterator();
                    List<String> templates = new ArrayList<String>();
                    List<Boolean> skips = new ArrayList<Boolean>();
                    List<Boolean> renames = new ArrayList<Boolean>();
                    check: {
                        while(itr.hasNext()) {
                            Object object = itr.next();
                            if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                                AnnotationsAttribute attr = (AnnotationsAttribute) object;
                                Annotation[] items = attr.getAnnotations();
                                for (Annotation annotation : items) {
                                    String typeName = annotation.getTypeName();
                                    if (typeName.equals(DirectCopyTransform.class.getName())) {
                                        ArrayMemberValue arrayMember = (ArrayMemberValue) annotation.getMemberValue("value");
                                        for (MemberValue arrayMemberValue : arrayMember.getValue()) {
                                            AnnotationMemberValue member = (AnnotationMemberValue) arrayMemberValue;
                                            Annotation memberAnnot = member.getValue();
                                            ArrayMemberValue annot = (ArrayMemberValue) memberAnnot.getMemberValue("templateTokens");
                                            for (MemberValue memberValue : annot.getValue()) {
                                                String val = ((StringMemberValue) memberValue).getValue();
                                                if (val != null && templateTokens.containsKey(val)) {
                                                    templates.add(templateTokens.get(val));
                                                }
                                            }
                                            BooleanMemberValue skipAnnot = (BooleanMemberValue) memberAnnot.getMemberValue("skipOverlaps");
                                            if (skipAnnot != null) {
                                                skips.add(skipAnnot.getValue());
                                            } else {
                                                skips.add(mySkipOverlaps);
                                            }
                                            BooleanMemberValue renameAnnot = (BooleanMemberValue) memberAnnot.getMemberValue("renameMethodOverlaps");
                                            if (renameAnnot != null) {
                                                renames.add(renameAnnot.getValue());
                                            } else {
                                                renames.add(myRenameMethodOverlaps);
                                            }
                                        }
                                        xformVals = templates.toArray(new String[templates.size()]);
                                        xformSkipOverlaps = skips.toArray(new Boolean[skips.size()]);
                                        xformRenameMethodOverlaps = renames.toArray(new Boolean[renames.size()]);
                                        break check;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (xformVals != null && xformVals.length > 0) {
                logger.lifecycle(LifeCycleEvent.START, String.format("Transform - Copying into [%s] from [%s]", xformKey,
                        StringUtils.join(xformVals, ",")));
                // Load the destination class and defrost it so it is eligible for modifications
                clazz.defrost();

                int index = 0;
                for (String xformVal : xformVals) {
                    // Load the source class
                    String trimmed = xformVal.trim();
                    classPool.appendClassPath(new LoaderClassPath(Class.forName(trimmed).getClassLoader()));
                    CtClass template = classPool.get(trimmed);

                    // Add in extra interfaces
                    CtClass[] interfacesToCopy = template.getInterfaces();
                    for (CtClass i : interfacesToCopy) {
                        checkInterfaces: {
                            CtClass[] myInterfaces = clazz.getInterfaces();
                            for (CtClass myInterface : myInterfaces) {
                                if (myInterface.getName().equals(i.getName())) {
                                    if (xformSkipOverlaps[index]) {
                                        break checkInterfaces;
                                    } else {
                                        throw new RuntimeException("Duplicate interface detected " + myInterface.getName());
                                    }
                                }
                            }
                            logger.debug(String.format("Adding interface [%s]", i.getName()));
                            clazz.addInterface(i);
                        }
                    }

                    //copy over any EntityListeners
                    ClassFile classFile = clazz.getClassFile();
                    ClassFile templateFile = template.getClassFile();
                    ConstPool constantPool = classFile.getConstPool();
                    buildClassLevelAnnotations(classFile, templateFile, constantPool);

                    // Copy over all declared fields from the template class
                    // Note that we do not copy over fields with the @NonCopiedField annotation
                    CtField[] fieldsToCopy = template.getDeclaredFields();
                    for (CtField field : fieldsToCopy) {
                        if (field.hasAnnotation(NonCopied.class)) {
                            logger.debug(String.format("Not adding field [%s]", field.getName()));
                        } else {
                            try {
                                clazz.getDeclaredField(field.getName());
                                if (xformSkipOverlaps[index]) {
                                    logger.debug(String.format("Skipping overlapped field [%s]", field.getName()));
                                    continue;
                                }
                            } catch (NotFoundException e) {
                                //do nothing -- field does not exist
                            }
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

                                if (xformSkipOverlaps[index]) {
                                    logger.debug(String.format("Skipping overlapped method [%s]", methodDescription(originalMethod)));
                                    continue;
                                }

                                if (transformedMethods.contains(methodDescription(originalMethod))) {
                                    throw new RuntimeException("Method already replaced " + methodDescription(originalMethod));
                                } else {
                                    logger.debug(String.format("Marking as replaced [%s]", methodDescription(originalMethod)));
                                    transformedMethods.add(methodDescription(originalMethod));
                                }

                                logger.debug(String.format("Removing method [%s]", method.getName()));
                                if (xformRenameMethodOverlaps[index]) {
                                    originalMethod.setName(renameMethodPrefix + method.getName());
                                } else {
                                    clazz.removeMethod(originalMethod);
                                }
                            } catch (NotFoundException e) {
                                // Do nothing -- we don't need to remove a method because it doesn't exist
                            }

                            logger.debug(String.format("Adding method [%s]", method.getName()));
                            CtMethod copiedMethod = new CtMethod(method, clazz, null);
                            clazz.addMethod(copiedMethod);
                        }
                    }
                    index++;
                }

                if (xformTemplates.isEmpty()) {
                    annotationTransformedClasses.add(convertedClassName);
                }
                logger.lifecycle(LifeCycleEvent.END, String.format("Transform - Copying into [%s] from [%s]", xformKey,
                                    StringUtils.join(xformVals, ",")));
                return clazz.toBytecode();
            }
        } catch (ClassCircularityError error) {
            error.printStackTrace();
            throw error;
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform class", e);
        }

        return null;
    }

    protected void buildClassLevelAnnotations(ClassFile classFile, ClassFile templateClassFile, ConstPool constantPool) throws NotFoundException {
        List<?> templateAttributes = templateClassFile.getAttributes();
        Iterator<?> templateItr = templateAttributes.iterator();
        Annotation templateEntityListeners = null;
        while(templateItr.hasNext()) {
            Object object = templateItr.next();
            if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                AnnotationsAttribute attr = (AnnotationsAttribute) object;
                Annotation[] items = attr.getAnnotations();
                for (Annotation annotation : items) {
                    String typeName = annotation.getTypeName();
                    if (typeName.equals(EntityListeners.class.getName())) {
                        templateEntityListeners = annotation;
                    }
                }
            }
        }

        if (templateEntityListeners != null) {
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
            List<?> attributes = classFile.getAttributes();
            Iterator<?> itr = attributes.iterator();
            Annotation existingEntityListeners = null;
            while(itr.hasNext()) {
                Object object = itr.next();
                if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                    AnnotationsAttribute attr = (AnnotationsAttribute) object;
                    Annotation[] items = attr.getAnnotations();
                    for (Annotation annotation : items) {
                        String typeName = annotation.getTypeName();
                        if (typeName.equals(EntityListeners.class.getName())) {
                            logger.debug("Stripping out previous EntityListeners annotation at the class level - will merge into new EntityListeners");
                            existingEntityListeners = annotation;
                            continue;
                        }
                        annotationsAttribute.addAnnotation(annotation);
                    }
                    itr.remove();
                }
            }

            Annotation entityListeners = getEntityListeners(constantPool, existingEntityListeners, templateEntityListeners);
            annotationsAttribute.addAnnotation(entityListeners);

            classFile.addAttribute(annotationsAttribute);
        }
    }

    protected Annotation getEntityListeners(ConstPool constantPool, Annotation existingEntityListeners, Annotation templateEntityListeners) {
        Annotation listeners = new Annotation(EntityListeners.class.getName(), constantPool);
        ArrayMemberValue listenerArray = new ArrayMemberValue(constantPool);
        Set<MemberValue> listenerMemberValues = new HashSet<MemberValue>();
        {
            ArrayMemberValue templateListenerValues = (ArrayMemberValue) templateEntityListeners.getMemberValue("value");
            listenerMemberValues.addAll(Arrays.asList(templateListenerValues.getValue()));
            logger.debug("Adding template values to new EntityListeners");
        }
        if (existingEntityListeners != null) {
            ArrayMemberValue oldListenerValues = (ArrayMemberValue) existingEntityListeners.getMemberValue("value");
            listenerMemberValues.addAll(Arrays.asList(oldListenerValues.getValue()));
            logger.debug("Adding previous values to new EntityListeners");
        }
        listenerArray.setValue(listenerMemberValues.toArray(new MemberValue[listenerMemberValues.size()]));
        listeners.addMemberValue("value", listenerArray);

        return listeners;

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

    public Boolean getRenameMethodOverlaps() {
        return renameMethodOverlaps;
    }

    public void setRenameMethodOverlaps(Boolean renameMethodOverlaps) {
        this.renameMethodOverlaps = renameMethodOverlaps;
    }

    public String getRenameMethodPrefix() {
        return renameMethodPrefix;
    }

    public void setRenameMethodPrefix(String renameMethodPrefix) {
        this.renameMethodPrefix = renameMethodPrefix;
    }

    public Boolean getSkipOverlaps() {
        return skipOverlaps;
    }

    public void setSkipOverlaps(Boolean skipOverlaps) {
        this.skipOverlaps = skipOverlaps;
    }

    public Map<String, String> getTemplateTokens() {
        return templateTokens;
    }

    public void setTemplateTokens(Map<String, String> templateTokens) {
        this.templateTokens = templateTokens;
    }

    public List<String> getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(List<String> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }
}

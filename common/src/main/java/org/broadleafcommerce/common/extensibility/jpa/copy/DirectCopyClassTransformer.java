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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.logging.LifeCycleEvent;
import org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformMemberDto;
import org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformersManager;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityListeners;

/**
 * This class transformer will copy fields, methods, and interface definitions from a source class to a target class,
 * based on the xformTemplates map. It will fail if it encounters any duplicate definitions.
 *
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 */
public class DirectCopyClassTransformer extends AbstractClassTransformer implements BroadleafClassTransformer {

    protected static List<String> transformedMethods = new ArrayList<String>();
    protected static List<String> annotationTransformedClasses = new ArrayList<String>();

    private static final Log logger = LogFactory.getLog(DirectCopyClassTransformer.class);

    protected String moduleName;
    protected Map<String, String> xformTemplates = new HashMap<String, String>();
    protected Boolean renameMethodOverlaps = false;
    protected String renameMethodPrefix = "__";
    protected Boolean skipOverlaps = true;
    protected Map<String, String> templateTokens = new HashMap<String, String>();

    @Resource(name="blDirectCopyIgnorePatterns")
    protected List<DirectCopyIgnorePattern> ignorePatterns = new ArrayList<DirectCopyIgnorePattern>();

    @Resource(name="blConditionalDirectCopyTransformersManager")
    protected ConditionalDirectCopyTransformersManager conditionalDirectCopyTransformersManager;

    public DirectCopyClassTransformer(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        // When simply copying properties over for Java class files, JPA properties do not need modification
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
            boolean mySkipOverlaps = skipOverlaps;
            boolean myRenameMethodOverlaps = renameMethodOverlaps;
            String convertedClassName = className.replace('/', '.');
            ClassPool classPool = null;
            String xformKey = convertedClassName;
            Set<String> buildXFormVals = new HashSet<String>();
            Boolean[] xformSkipOverlaps = null;
            Boolean[] xformRenameMethodOverlaps = null;
            if (!xformTemplates.isEmpty()) {
                if (xformTemplates.containsKey(xformKey)) {
                    buildXFormVals.addAll(Arrays.asList(xformTemplates.get(xformKey).split(",")));
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
                        return null;
                    }
                }
                if (isValidPattern) {
                    classPool = ClassPool.getDefault();
                    clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
                    XFormParams params = reviewDirectCopyTransformAnnotations(clazz, mySkipOverlaps, myRenameMethodOverlaps, matchedPatterns);
                    XFormParams conditionalParams = reviewConditionalDirectCopyTransforms(convertedClassName, matchedPatterns);
                    if (conditionalParams != null && !conditionalParams.isEmpty()) {
                        params = combineXFormParams(params, conditionalParams);
                    }
                    if (params.getXformVals() != null && params.getXformVals().length > 0) {
                        buildXFormVals.addAll(Arrays.asList(params.getXformVals()));
                    }
                    xformSkipOverlaps = params.getXformSkipOverlaps();
                    xformRenameMethodOverlaps = params.getXformRenameMethodOverlaps();
                }
            }
            if (buildXFormVals.size() > 0) {
                String[] xformVals = buildXFormVals.toArray(new String[buildXFormVals.size()]);
                logger.debug(String.format("[%s] - Transform - Copying into [%s] from [%s]", LifeCycleEvent.END, xformKey,
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
                                    if (xformSkipOverlaps != null && xformSkipOverlaps[index]) {
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
                                CtField ctField = clazz.getDeclaredField(field.getName());
                                String originalSignature = ctField.getSignature();
                                String mySignature = field.getSignature();
                                if (!originalSignature.equals(mySignature)) {
                                    throw new IllegalArgumentException("Field with name ("+field.getName()+") and signature " +
                                            "("+field.getSignature()+") is targeted for weaving into ("+clazz.getName()+"). " +
                                            "An incompatible field of the same name and signature of ("+ctField.getSignature()+") " +
                                            "already exists. The field in the target class should be updated to a different name, " +
                                            "or made to have a matching type.");
                                }
                                if (xformSkipOverlaps != null && xformSkipOverlaps[index]) {
                                    logger.debug(String.format("Skipping overlapped field [%s]", field.getName()));
                                    continue;
                                }
                                clazz.removeField(ctField);
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

                                if (xformSkipOverlaps != null && xformSkipOverlaps[index]) {
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
                                if (xformRenameMethodOverlaps != null && xformRenameMethodOverlaps[index]) {
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
                logger.debug(String.format("[%s] - Transform - Copying into [%s] from [%s]", LifeCycleEvent.END, xformKey,
                                    StringUtils.join(xformVals, ",")));
                return clazz.toBytecode();
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


    /**
     * Combines two {@link org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyClassTransformer.XFormParams} together with
     * first passed in xformParama supercedes the second passed in parameter.
     *
     * @param defaultParams
     * @param conditionalParams
     * @return
     */
    protected XFormParams combineXFormParams(XFormParams defaultParams, XFormParams conditionalParams) {

        XFormParams response = new XFormParams();
        Map<String, Boolean> templateSkipMap = new LinkedHashMap<>();
        List<String> templates = new ArrayList<String>();
        List<Boolean> skips = new ArrayList<Boolean>();
        List<Boolean> renames = new ArrayList<Boolean>();
        // Add the default Params
        if (!defaultParams.isEmpty()) {
            for (int iter = 0; iter < defaultParams.getXformVals().length; iter++) {
                String defaultParam = defaultParams.getXformVals()[iter];
                if (!templateSkipMap.containsKey(defaultParam)) {
                    templateSkipMap.put(defaultParam, true);
                    templates.add(defaultParam);
                    skips.add(defaultParams.getXformSkipOverlaps()[iter]);
                    renames.add(defaultParams.getXformRenameMethodOverlaps()[iter]);
                }
            }
        }

        // Only add Conditional Params if they are not already included
        for (int iter = 0; iter < conditionalParams.getXformVals().length; iter++) {
            String conditionalValue = conditionalParams.getXformVals()[iter];
            if (!templateSkipMap.containsKey(conditionalValue)) {
                templates.add(conditionalValue);
                skips.add(conditionalParams.getXformSkipOverlaps()[iter]);
                renames.add(conditionalParams.getXformRenameMethodOverlaps()[iter]);
            }
        }


        // convert list to arrays
        response.setXformVals(templates.toArray(new String[templates.size()]));
        response.setXformSkipOverlaps(skips.toArray(new Boolean[skips.size()]));
        response.setXformRenameMethodOverlaps(renames.toArray(new Boolean[renames.size()]));
        return response;
    }

    /**
     * Retrieves {@link DirectCopyTransformTypes} that are placed as annotations on classes.
     * @param clazz
     * @param mySkipOverlaps
     * @param myRenameMethodOverlaps
     * @param matchedPatterns
     * @return
     */
    protected XFormParams reviewDirectCopyTransformAnnotations(CtClass clazz, boolean mySkipOverlaps, boolean myRenameMethodOverlaps, List<DirectCopyIgnorePattern> matchedPatterns) {
        List<?> attributes = clazz.getClassFile().getAttributes();
        Iterator<?> itr = attributes.iterator();
        List<String> templates = new ArrayList<String>();
        List<Boolean> skips = new ArrayList<Boolean>();
        List<Boolean> renames = new ArrayList<Boolean>();
        XFormParams response = new XFormParams();
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
                                List<String> addedTemplates = new ArrayList<String>();
                                for (MemberValue memberValue : annot.getValue()) {
                                    String val = ((StringMemberValue) memberValue).getValue();
                                    addedTemplates.addAll(reviewTemplateTokens(matchedPatterns, val));
                                }
                                templates.addAll(addedTemplates);
                                BooleanMemberValue skipAnnot = (BooleanMemberValue) memberAnnot.getMemberValue("skipOverlaps");
                                if (skipAnnot != null) {
                                    for (int j=0;j<addedTemplates.size();j++) {
                                        skips.add(skipAnnot.getValue());
                                    }
                                } else {
                                    for (int j=0;j<addedTemplates.size();j++) {
                                        skips.add(mySkipOverlaps);
                                    }
                                }
                                BooleanMemberValue renameAnnot = (BooleanMemberValue) memberAnnot.getMemberValue("renameMethodOverlaps");
                                if (renameAnnot != null) {
                                    for (int j=0;j<addedTemplates.size();j++) {
                                        renames.add(renameAnnot.getValue());
                                    }
                                } else {
                                    for (int j=0;j<addedTemplates.size();j++) {
                                        renames.add(myRenameMethodOverlaps);
                                    }
                                }
                            }
                            response.setXformVals(templates.toArray(new String[templates.size()]));
                            response.setXformSkipOverlaps(skips.toArray(new Boolean[skips.size()]));
                            response.setXformRenameMethodOverlaps(renames.toArray(new Boolean[renames.size()]));
                            break check;
                        }
                    }
                }
            }
        }
        return response;
    }

    /**
     * Retrieves {@link DirectCopyTransformTypes} that are conditionally/optionally included via properties file.
     * @see org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformersManager
     *
     * @param convertedClassName
     * @param matchedPatterns
     * @return
     */
    protected XFormParams reviewConditionalDirectCopyTransforms(String convertedClassName, List<DirectCopyIgnorePattern> matchedPatterns) {
        XFormParams response = new XFormParams();
        List<String> templates = new ArrayList<String>();
        List<Boolean> skips = new ArrayList<Boolean>();
        List<Boolean> renames = new ArrayList<Boolean>();
        if (conditionalDirectCopyTransformersManager.isEntityEnabled(convertedClassName)) {
            ConditionalDirectCopyTransformMemberDto dto = conditionalDirectCopyTransformersManager.getTransformMember(convertedClassName);
            List<String> addedTemplates = new ArrayList<String>();
            for (String templateToken : dto.getTemplateTokens()) {
                addedTemplates.addAll(reviewTemplateTokens(matchedPatterns, templateToken));
            }
            templates.addAll(addedTemplates);
            for (int j=0;j<addedTemplates.size();j++) {
                skips.add(dto.isSkipOverlaps());
                renames.add(dto.isRenameMethodOverlaps());
            }
            response.setXformVals(templates.toArray(new String[templates.size()]));
            response.setXformSkipOverlaps(skips.toArray(new Boolean[skips.size()]));
            response.setXformRenameMethodOverlaps(renames.toArray(new Boolean[renames.size()]));
        }
        return response;
    }

    protected List<String> reviewTemplateTokens(List<DirectCopyIgnorePattern> matchedPatterns, String val) {
        List<String> addedTemplates = new ArrayList<String>();
        if (val != null && templateTokens.containsKey(val)) {
            templateCheck: {
                for (DirectCopyIgnorePattern matchedPattern : matchedPatterns) {
                    for (String ignoreToken : matchedPattern.getTemplateTokenPatterns()) {
                        if (val.matches(ignoreToken)) {
                            break templateCheck;
                        }
                    }
                }
                String[] templateVals = templateTokens.get(val).split(",");
                addedTemplates.addAll(Arrays.asList(templateVals));
            }
        }
        return addedTemplates;
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

    public List<DirectCopyIgnorePattern> getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(List<DirectCopyIgnorePattern> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

    private class XFormParams {

        String[] xformVals = null;
        Boolean[] xformSkipOverlaps = null;
        Boolean[] xformRenameMethodOverlaps = null;

        public String[] getXformVals() {
            return xformVals;
        }

        public void setXformVals(String[] xformVals) {
            this.xformVals = xformVals;
        }

        public Boolean[] getXformSkipOverlaps() {
            return xformSkipOverlaps;
        }

        public void setXformSkipOverlaps(Boolean[] xformSkipOverlaps) {
            this.xformSkipOverlaps = xformSkipOverlaps;
        }

        public Boolean[] getXformRenameMethodOverlaps() {
            return xformRenameMethodOverlaps;
        }

        public void setXformRenameMethodOverlaps(Boolean[] xformRenameMethodOverlaps) {
            this.xformRenameMethodOverlaps = xformRenameMethodOverlaps;
        }

        public boolean isEmpty() {
            return xformVals == null || xformVals.length == 0;
        }
    }
}

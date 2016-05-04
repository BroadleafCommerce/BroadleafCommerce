/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.broadleafcommerce.common.logging.SupportLogManager;
import org.broadleafcommerce.common.logging.SupportLogger;
import org.broadleafcommerce.common.weave.ConditionalFieldAnnotationCopyTransformMemberDTO;
import org.broadleafcommerce.common.weave.ConditionalFieldAnnotationCopyTransformersManager;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Properties;

import javax.annotation.Resource;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.LoaderClassPath;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

/**
 * The purpose of this class is to allow the conditional addition or removal of annotations to a target entity based on template class.
 * 
 * This does not add new fields or methods, nor does it remove fields or methods.  Rather, this class copies annotations from a 
 * template class' field(s) to a target class' fields.  It removes annotations from the target that are not on the template.
 * 
 * As a result, this class takes the annotations from fields of a template class and adds them to fields in a target class. 
 * If the template contains fields that are not on the target, then unexpected behavior such as NullPointerExceptions may occur.
 * 
 * @author Kelly Tisdell
 *
 */
public class ConditionalFieldAnnotationsClassTransformer extends AbstractClassTransformer implements BroadleafClassTransformer {

    @Resource(name = "blConditionalFieldAnnotationsTransformersManager")
    protected ConditionalFieldAnnotationCopyTransformersManager manager;

    protected SupportLogger logger;
    protected String moduleName;

    public ConditionalFieldAnnotationsClassTransformer(String moduleName) {
        this.moduleName = moduleName;
        logger = SupportLogManager.getLogger(moduleName, this.getClass());
    }

    /**
     * Will return null if the Spring property value defined in {@link #propertyName} resolves to false, or if
     * an exception occurs while trying to determine the value for the property.
     *
     * @param loader
     * @param className
     * @param classBeingRedefined
     * @param protectionDomain
     * @param classfileBuffer
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // Lambdas and anonymous methods in Java 8 do not have a class name defined and so no transformation should be done
        if (className == null) {
            return null;
        }

        String convertedClassName = className.replace('/', '.');

        ConditionalFieldAnnotationCopyTransformMemberDTO dto = manager.getTransformMember(convertedClassName);
        if (dto == null || dto.getTemplateNames() == null || dto.getTemplateNames().length < 1) {
            return null;
        }

        //Be careful with Apache library usage in this class (e.g. ArrayUtils). Usage will likely cause a ClassCircularityError
        //under JRebel. Favor not including outside libraries and unnecessary classes.
        CtClass clazz = null;
        try {
            String[] xformVals = dto.getTemplateNames();

            // Load the destination class and defrost it so it is eligible for modifications
            ClassPool classPool = ClassPool.getDefault();
            clazz = classPool.makeClass(new ByteArrayInputStream(classfileBuffer), false);
            clazz.defrost();

            for (String xformVal : xformVals) {
                // Load the source class
                String trimmed = xformVal.trim();
                classPool.appendClassPath(new LoaderClassPath(Class.forName(trimmed).getClassLoader()));
                CtClass template = classPool.get(trimmed);

                CtField[] fieldsToCopy = template.getDeclaredFields();
                //Iterate over all of the fields in the template.
                //If the template field contains annotations, replace the target's annotations with the 
                //template annotations.  Otherwise, remove all annotations from the target.
                for (CtField field : fieldsToCopy) {
                    ConstPool constPool = clazz.getClassFile().getConstPool();
                    CtField fieldFromMainClass = clazz.getField(field.getName());

                    AnnotationsAttribute copied = null;

                    for (Object o : field.getFieldInfo().getAttributes()) {
                        if (o instanceof AnnotationsAttribute) {
                            AnnotationsAttribute templateAnnotations = (AnnotationsAttribute) o;
                            //have to make a copy of the annotations from the target
                            copied = (AnnotationsAttribute) templateAnnotations.copy(constPool, null);
                            break;
                        }
                    }

                    //add all the copied annotations into the target class's field.
                    for (Object attribute : fieldFromMainClass.getFieldInfo().getAttributes()) {
                        if (attribute instanceof AnnotationsAttribute) {
                            Annotation[] annotations = null;

                            if (copied != null) {
                                //If we found annotations to copy, then use all of them
                                ArrayList<Annotation> annotationsList = new ArrayList<Annotation>();
                                for (Annotation annotation : copied.getAnnotations()) {
                                    annotationsList.add(annotation);
                                }

                                annotations = new Annotation[annotationsList.size()];
                                int count = 0;
                                for (Annotation annotation : annotationsList) {
                                    annotations[count] = annotation;
                                    count++;
                                }

                                ((AnnotationsAttribute) attribute).setAnnotations(annotations);
                            } else {
                                //If no annotations were found on the template, then remove them entirely from the target.
                                ((AnnotationsAttribute) attribute).setAnnotations(new Annotation[] {});
                            }

                            break;
                        }
                    }

                }
            }

            return clazz.toBytecode();
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
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        //Nothing to do here...
    }

}

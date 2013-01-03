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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class transformer will copy fields, methods, and interface definitions from a source class to a target class,
 * based on the xformTemplates map. It will fail if it encouters any duplicate definitions.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DirectCopyClassTransformer implements BroadleafClassTransformer {
    private static final Log LOG = LogFactory.getLog(DirectCopyClassTransformer.class);
    
    protected Map<String, String> xformTemplates = new HashMap<String, String>();
    
    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        // When simply copying properties over for Java calss files, JPA properties do not need modification
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, 
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String convertedClassName = className.replace('/', '.');
        
        if (xformTemplates.containsKey(convertedClassName)) {
            String xformKey = convertedClassName;
            String xformVal = xformTemplates.get(xformKey);
            LOG.debug(String.format("Copying into [%s] from [%s]", xformKey, xformVal));
            
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
                    LOG.debug(String.format("...Attempting to copy interface [%s]", i.getName()));
                    clazz.addInterface(i);
                }
                
                // Copy over all declared fields from the template class
                CtField[] fieldsToCopy = template.getDeclaredFields();
                for (CtField field : fieldsToCopy) {
                    LOG.debug(String.format("...Attempting to copy field [%s]", field.getName()));
                    CtField copiedField = new CtField(field, clazz);
                    clazz.addField(copiedField);
                }
                
                // Copy over all declared methods from the template class
                CtMethod[] methodsToCopy = template.getDeclaredMethods();
                for (CtMethod method : methodsToCopy) {
                    LOG.debug(String.format("...Attempting to copy method [%s]", method.getName()));
                    CtMethod copiedMethod = new CtMethod(method, clazz, null);
                    clazz.addMethod(copiedMethod);
                }
                
                return clazz.toBytecode();
            } catch (Exception e) {
                throw new RuntimeException("Unable to transform class", e);
            }
        }
        
        return null;
    }

    public Map<String, String> getXformTemplates() {
        return xformTemplates;
    }

    public void setXformTemplates(Map<String, String> xformTemplates) {
        this.xformTemplates = xformTemplates;
    }
    
}


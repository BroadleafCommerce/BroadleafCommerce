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
package org.broadleafcommerce.common.extensibility.jpa.convert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyIgnorePattern;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import javax.annotation.Resource;
import javax.persistence.Entity;

/**
 * This class transformer will check to see if there is an @Entity annotation on the given class, and if there is,
 * it will add the fully qualified classname of that class to the transformedClassNames list.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class EntityMarkerClassTransformer implements BroadleafClassTransformer {
    protected static final Log LOG = LogFactory.getLog(EntityMarkerClassTransformer.class);
    
    protected List<String> transformedClassNames = new ArrayList<String>();
    
    @Resource(name = "blDirectCopyIgnorePatterns")
    protected List<DirectCopyIgnorePattern> ignorePatterns = new ArrayList<DirectCopyIgnorePattern>();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String convertedClassName = className.replace('/', '.');
        
        if (isIgnored(convertedClassName)) {
            return null;
        }

        try {
            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
            List<?> attributes = classFile.getAttributes();
            Iterator<?> itr = attributes.iterator();
            check: {
                while(itr.hasNext()) {
                    Object object = itr.next();
                    if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                        for (Annotation annotation : ((AnnotationsAttribute) object).getAnnotations()) {
                            if (annotation.getTypeName().equals(Entity.class.getName())) {
                                LOG.trace("Marking " + convertedClassName + " as transformed");
                                transformedClassNames.add(convertedClassName);
                                break check;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new IllegalClassFormatException("Unable to mark " + convertedClassName + " as transformed.");
        }
        
        // We don't need to transform anything, so we'll return null
        return null;
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

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        // When performing the check that this class transformer does, JPA properties do not need modificiation
    }
    
    /**
     * @return a list of fully qualified classnames of class that have an @Entity annotation and were picked
     * up by this class transformer (meaning that other class transformers also would have had a chance to
     * perform their necessary work on those classes)
     */
    public List<String> getTransformedClassNames() {
        return transformedClassNames;
    }

    public List<DirectCopyIgnorePattern> getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(List<DirectCopyIgnorePattern> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

}

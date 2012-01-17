package org.broadleafcommerce.common.extensibility.jpa.convert.jaxb;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

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
 *
 * We are putting this logic inside a JPA class transformer for 2 reasons.  First, we are manipulating
 * Broadleaf entity classes, which also have JAXB mappings.  Second, JPA already provides a very
 * convenient hook to do instrumentation.
 *
 * User: Kelly Tisdell
 */
public class JAXBPropertyOmittingClassTransformer implements BroadleafClassTransformer {

    private static final String KEY_PREFIX = "broadleaf.ejb.entities.jaxb.omit_properties.";

    protected Map<String,HashSet<String>> classInfo = new HashMap<String, HashSet<String>>();

    @Override
    @SuppressWarnings("unchecked")
    public byte[] transform(ClassLoader classLoader, String className, Class<?> aClass, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classInfo.isEmpty()) {
            return null;
        }
        String convertedClassName = className.replace('/', '.');

        HashSet<String> methodNames = classInfo.get(convertedClassName);
        if (methodNames != null && ! methodNames.isEmpty()) {
            try {
                ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
                ConstPool constantPool = classFile.getConstPool();

                AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constantPool, AnnotationsAttribute.visibleTag);
                Annotation transientAnnotation = new Annotation(XmlTransient.class.getName(), constantPool);
                annotationsAttribute.addAnnotation(transientAnnotation);
                for (String methodName : methodNames) {
                    List<?> methodAttributes = classFile.getMethod(methodName).getAttributes();
                    Iterator<?> itr = methodAttributes.iterator();
                    while(itr.hasNext()) {
                        Object object = itr.next();
                        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
                            AnnotationsAttribute attr = (AnnotationsAttribute) object;
                            Annotation[] items = attr.getAnnotations();
                            for (Annotation annotation : items) {
                                String typeName = annotation.getTypeName();
                                if (! typeName.startsWith("javax.xml.bind.annotation")) {
                                    annotationsAttribute.addAnnotation(annotation);
                                }
                            }
                            itr.remove();
                        }
                    }
                    classFile.getMethod(methodName).getAttributes().add(annotationsAttribute);

                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bos);
                classFile.write(os);
                os.close();

                return bos.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalClassFormatException("Unable to convert " + convertedClassName + ", to add XMLTransient annotations" + e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        if (! (key instanceof String)) {
            return;
        }
        String keyString = (String)key;
        if (! keyString.startsWith(KEY_PREFIX)) {
            return;
        }
        
        String className = keyString.substring(KEY_PREFIX.length() + 1);
        
        String[] getterNames = StringUtils.tokenizeToStringArray(props.getProperty((String) key), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        if (getterNames != null && getterNames.length > 0) {
            if (classInfo.get(className) == null) {
                classInfo.put(className, new HashSet<String>());
            }
            
            for (String getterName : getterNames) {
                classInfo.get(className).add(getterName);
            }
        }
    }
}

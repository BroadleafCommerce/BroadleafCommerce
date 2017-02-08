/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.persistence.LockModeType;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

/**
 * Detects any externally registered {@link NamedQueries} or {@link NamedNativeQueries} and adds them to the first detected
 * entity implementation class for the target persistence unit. This is a JPA/Hibernate requirement and this approach
 * allows us to continue to be compatible with that, while still allowing client implementations to adopt this approach,
 * or continue to use the several standard approaches - including entity annotation, xml or runtime query creation.
 * </p>
 * See {@link QueryConfiguration} for more information on configuring named queries externally.
 *
 * @author Jeff Fischer
 */
public class QueryConfigurationClassTransformer implements BroadleafClassTransformer {

    private static boolean isExecuted = false;
    protected List<NamedQuery> namedQueries;
    protected List<NamedNativeQuery> nativeQueries;
    protected List<String> managedClassNames;

    public QueryConfigurationClassTransformer(List<NamedQuery> namedQueries, List<NamedNativeQuery> nativeQueries, List<String> managedClassNames) {
        this.namedQueries = namedQueries;
        this.nativeQueries = nativeQueries;
        this.managedClassNames = managedClassNames;
    }

    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
        //do nothing
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain
            protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || isExecuted) {
            return null;
        }
        String convertedClassName = className.replace('/', '.');
        if (managedClassNames.contains(convertedClassName)) {
            try {
                ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
                if (isExecuted) {
                    return null;
                }
                ConstPool constantPool = classFile.getConstPool();
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("javax.persistence");
                pool.importPackage("java.lang");
                List<?> attributes = classFile.getAttributes();
                Iterator<?> itr = attributes.iterator();
                while (itr.hasNext()) {
                    Object object = itr.next();
                    processClassLevelAnnotations(constantPool, pool, object);
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(bos);
                classFile.write(os);
                os.close();

                isExecuted = true;

                return bos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalClassFormatException("Unable to convert " + convertedClassName + " to a SingleTable inheritance strategy: " + e.getMessage());
            }
        } else {
            return null;
        }
    }

    /**
     * Look for any existing {@link NamedQueries} or {@link NamedNativeQueries} and embellish those declarations, if applicable.
     * Otherwise, create new declarations of these annotations as needed.
     *
     * @param constantPool
     * @param pool
     * @param object
     * @throws NotFoundException
     */
    protected void processClassLevelAnnotations(ConstPool constantPool, ClassPool pool, Object object) throws NotFoundException {
        if (AnnotationsAttribute.class.isAssignableFrom(object.getClass())) {
            AnnotationsAttribute attr = (AnnotationsAttribute) object;
            Annotation[] items = attr.getAnnotations();
            List<Annotation> newItems = new ArrayList<Annotation>();
            ArrayMemberValue namedQueryArray = new ArrayMemberValue(constantPool);
            ArrayMemberValue nativeQueryArray = new ArrayMemberValue(constantPool);
            for (Annotation annotation : items) {
                String typeName = annotation.getTypeName();
                if (typeName.equals(NamedQueries.class.getName())) {
                    namedQueryArray = (ArrayMemberValue) annotation.getMemberValue("value");
                } else if (typeName.equals(NamedNativeQueries.class.getName())) {
                    nativeQueryArray = (ArrayMemberValue) annotation.getMemberValue("value");
                } else {
                    newItems.add(annotation);
                }
            }

            if (!namedQueries.isEmpty()) {
                prepareNamedQueries(constantPool, pool, namedQueryArray);
                Annotation namedQueriesAnnotation = new Annotation(NamedQueries.class.getName(), constantPool);

                namedQueriesAnnotation.addMemberValue("value", namedQueryArray);
                newItems.add(namedQueriesAnnotation);
            }

            if (!nativeQueries.isEmpty()) {
                prepareNativeQueries(constantPool, pool, nativeQueryArray);
                Annotation nativeQueriesAnnotation = new Annotation(NamedQueries.class.getName(), constantPool);

                nativeQueriesAnnotation.addMemberValue("value", nativeQueryArray);
                newItems.add(nativeQueriesAnnotation);
            }

            attr.setAnnotations(newItems.toArray(new Annotation[newItems.size()]));
        }
    }

    /**
     * Prepare the {@link NamedNativeQueries} declaration
     *
     * @param constantPool
     * @param pool
     * @param queryArray
     * @throws NotFoundException
     */
    protected void prepareNativeQueries(ConstPool constantPool, ClassPool pool, ArrayMemberValue queryArray) throws NotFoundException {
        List<MemberValue> values;
        if (queryArray.getValue() != null) {
            values = new ArrayList<MemberValue>(Arrays.asList(queryArray.getValue()));
        } else {
            values = new ArrayList<MemberValue>();
        }

        for (NamedNativeQuery query : nativeQueries) {
            Annotation namedQuery = new Annotation(NamedNativeQuery.class.getName(), constantPool);

            StringMemberValue name = new StringMemberValue(constantPool);
            name.setValue(query.name());
            namedQuery.addMemberValue("name", name);

            StringMemberValue queryString = new StringMemberValue(constantPool);
            queryString.setValue(query.query());
            namedQuery.addMemberValue("query", queryString);

            ClassMemberValue resultClass = new ClassMemberValue(constantPool);
            resultClass.setValue(query.resultClass().getName());
            namedQuery.addMemberValue("resultClass", resultClass);

            StringMemberValue resultSetMapping = new StringMemberValue(constantPool);
            resultSetMapping.setValue(query.resultSetMapping());
            namedQuery.addMemberValue("resultSetMapping", resultSetMapping);

            List<AnnotationMemberValue> queryHints = new ArrayList<AnnotationMemberValue>();
            for (QueryHint hint : query.hints()) {
                prepareQueryHints(constantPool, queryHints, hint);
            }
            ArrayMemberValue hintArray = new ArrayMemberValue(constantPool);
            hintArray.setValue(queryHints.toArray(new AnnotationMemberValue[queryHints.size()]));
            namedQuery.addMemberValue("hints", hintArray);

            AnnotationMemberValue queryAnnotation = new AnnotationMemberValue(namedQuery,
                    constantPool);
            values.add(queryAnnotation);
        }
        queryArray.setValue(values.toArray(new MemberValue[values.size()]));
    }

    /**
     * Prepare the {@link NamedQueries} declaration
     *
     * @param constantPool
     * @param pool
     * @param queryArray
     * @throws NotFoundException
     */
    protected void prepareNamedQueries(ConstPool constantPool, ClassPool pool, ArrayMemberValue queryArray) throws NotFoundException {
        List<MemberValue> values;
        if (queryArray.getValue() != null) {
            values = new ArrayList<MemberValue>(Arrays.asList(queryArray.getValue()));
        } else {
            values = new ArrayList<MemberValue>();
        }

        for (NamedQuery query : namedQueries) {
            Annotation namedQuery = new Annotation(NamedQuery.class.getName(), constantPool);

            StringMemberValue name = new StringMemberValue(constantPool);
            name.setValue(query.name());
            namedQuery.addMemberValue("name", name);

            StringMemberValue queryString = new StringMemberValue(constantPool);
            queryString.setValue(query.query());
            namedQuery.addMemberValue("query", queryString);

            EnumMemberValue lockMode = (EnumMemberValue) Annotation.createMemberValue(constantPool, pool.makeClass("LockModeType"));
            lockMode.setType(LockModeType.class.getName());
            lockMode.setValue(query.lockMode().toString());
            namedQuery.addMemberValue("lockMode", lockMode);

            List<AnnotationMemberValue> queryHints = new ArrayList<AnnotationMemberValue>();
            for (QueryHint hint : query.hints()) {
                prepareQueryHints(constantPool, queryHints, hint);
            }
            ArrayMemberValue hintArray = new ArrayMemberValue(constantPool);
            hintArray.setValue(queryHints.toArray(new AnnotationMemberValue[queryHints.size()]));
            namedQuery.addMemberValue("hints", hintArray);

            AnnotationMemberValue queryAnnotation = new AnnotationMemberValue(namedQuery,
                    constantPool);
            values.add(queryAnnotation);
        }
        queryArray.setValue(values.toArray(new MemberValue[values.size()]));
    }

    /**
     * Prepare any {@link QueryHint} declarations
     *
     * @param constantPool
     * @param queryHints
     * @param hint
     */
    protected void prepareQueryHints(ConstPool constantPool, List<AnnotationMemberValue> queryHints, QueryHint hint) {
        Annotation queryHint = new Annotation(QueryHint.class.getName(), constantPool);

        StringMemberValue hintName = new StringMemberValue(constantPool);
        hintName.setValue(hint.name());
        queryHint.addMemberValue("name", hintName);

        StringMemberValue hintValue = new StringMemberValue(constantPool);
        hintValue.setValue(hint.value());
        queryHint.addMemberValue("value", hintValue);

        AnnotationMemberValue hintAnnotation = new AnnotationMemberValue(queryHint,
                constantPool);
        queryHints.add(hintAnnotation);
    }
}

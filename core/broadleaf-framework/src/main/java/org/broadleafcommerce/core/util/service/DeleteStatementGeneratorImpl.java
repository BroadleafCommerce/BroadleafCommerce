/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.service;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

/**
 * The idea of this class if to iterate over props of the passed class and if found prop with annotation @OneToMany
 * recurse into it. In this way we build paths to the dependant classes. Once found "leaf" class that doesn't have
 * any more dependant entities it will start to go back and build path for that classes. In a result we will have
 * table name and path to that tabe from the root table, so we can build sql to delete dependencies in the right order.
 * It knows how to deal with @Embedded, bidirectional @OneToMany, @OneToMany through @JoinTable
 */
@Service("blcDeleteStatementGenerator")
public class DeleteStatementGeneratorImpl implements DeleteStatementGenerator {

    protected static final Log LOG = LogFactory.getLog(DeleteStatementGeneratorImpl.class);

    protected DynamicDaoHelperImpl helper = new DynamicDaoHelperImpl();

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    /**
     *
     * @param rootType type to start build sql dependency list
     * @param rootTypeIdValue value of id column of the rootType to be passed in where clause
     * @return a Map which key is a table name, and value is string with sql delete statement.
     * This map is the LinkedHashMap so order of keys is important. You should exec sql from the beginning.
     *
     */
    public Map<String, String> generateDeleteStatementsForType(Class<?> rootType, String rootTypeIdValue) {
        if(StringUtils.isEmpty(rootTypeIdValue)){
            rootTypeIdValue="XX";
        }
        LOG.info("Start generating SQL delete statements for type:"+rootType.getSimpleName());
        Stack<PathElement> stack = new Stack<>();
        Set<Class<?>> processedClasses = new HashSet<>();
        LinkedHashMap<String, Stack<PathElement>> result = new LinkedHashMap<>();
        diveDeep(rootType, null, null, stack, processedClasses, result);
        LinkedHashMap<String, String> sqls = new LinkedHashMap<>();
        for (Map.Entry<String, Stack<PathElement>> entry : result.entrySet()) {
            Stack<PathElement> value = entry.getValue();
            StringBuilder builder = new StringBuilder();
            PathElement prevTable = value.pop();
            builder.append("delete T FROM ").append(prevTable.getName()).append(" T");
            int index = 0;
            String prevTableAlias = "T";
            while (!value.empty()) {
                String nextTableAlias = "a" + index;
                PathElement next = value.pop();
                builder.append(" inner join ").append(next.getName()).append(" ").append(nextTableAlias).append(" on ")
                        .append(prevTableAlias).append(".").append(prevTable.getJoinColumn()).append("=")
                        .append(nextTableAlias).append(".").append(next.getIdField());
                prevTable = next;
                prevTableAlias = nextTableAlias;
                index++;
            }
            builder.append(" WHERE ").append(prevTableAlias).append(".").append(prevTable.getIdField()).append("=").append(rootTypeIdValue);
            String x = builder.toString();
            LOG.debug(x);
            sqls.put(entry.getKey(), x);
        }
        LOG.info("End generating SQL delete statements for type:"+rootType.getSimpleName()+". Generated "+sqls.size()+" statements");
        return sqls;
    }

    private void diveDeep(Class<?> classToProcess, String joinColumn, String mappedBy, Stack<PathElement> stack, Set<Class<?>> processedClasses, HashMap<String, Stack<PathElement>> result) {
        Class<?>[] classes = helper.getAllPolymorphicEntitiesFromCeiling(classToProcess,false,true);
        if (processedClasses.contains(classToProcess)) {
            return;
        }
        String idField = getIdField(classToProcess);
        if (idField == null) {
            idField = joinColumn;
        }
        processedClasses.add(classToProcess);

        Field[] declaredFields = classToProcess.getDeclaredFields();
        Table tableAnnotation =  classToProcess.getAnnotation(Table.class);
        if (StringUtils.isNotEmpty(mappedBy)) {
            Field field = getField(mappedBy, declaredFields);
            JoinColumn annotation = field.getAnnotation(JoinColumn.class);
            joinColumn = annotation.name();
        }
        PathElement el = new PathElement(tableAnnotation.name(), idField, joinColumn);
        stack.push(el);
        for (Class<?> aClass : classes) {
            if (!aClass.equals(classToProcess)) {
                diveDeep(aClass, idField, null, stack, processedClasses, result);
            }
        }
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Id.class) != null) {
                continue;
            }
            OneToMany annotation = declaredField.getAnnotation(OneToMany.class);
            if (annotation != null) {
                processField(stack, processedClasses, result, declaredField);
            } else if (declaredField.getAnnotation(Embedded.class) != null) {
                Class<?> type = declaredField.getType();
                for (Field decl : type.getDeclaredFields()) {
                    processField(stack, processedClasses, result, decl);
                }
            }
        }
        result.put(tableAnnotation.name(), (Stack<PathElement>) stack.clone());
        stack.pop();
    }

    private void processField(Stack<PathElement> stack, Set<Class<?>> processedClasses, HashMap<String, Stack<PathElement>> result, Field decl) {
        OneToMany oneToManyAnnot = decl.getAnnotation(OneToMany.class);
        if (oneToManyAnnot != null) {
            Class<?> aClass = oneToManyAnnot.targetEntity();
            String mappedByAnnotation = oneToManyAnnot.mappedBy();
            String joinColumnFromJoinTable = null;
            String joinTableNameFromJoinTable = null;
            if (StringUtils.isBlank(mappedByAnnotation)) {
                JoinTable joinTable = decl.getAnnotation(JoinTable.class);
                joinTableNameFromJoinTable = joinTable.name();
                JoinColumn jnColumn = joinTable.joinColumns()[0];
                JoinColumn inverseJoinColumn = joinTable.inverseJoinColumns()[0];
                PathElement pathElement = new PathElement(joinTable.name(), inverseJoinColumn.name(), jnColumn.name());
                stack.push(pathElement);
                joinColumnFromJoinTable = inverseJoinColumn.name();
            }
            diveDeep(aClass, joinColumnFromJoinTable, mappedByAnnotation, stack, processedClasses, result);
            if (StringUtils.isNotEmpty(joinTableNameFromJoinTable)) {
                result.put(joinTableNameFromJoinTable, (Stack<PathElement>) stack.clone());
                stack.pop();
            }
        }
    }

    private Field getField(String mappedBy, Field[] declaredFields) {
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().equals(mappedBy)) {
                return declaredField;
            }
        }
        return null;
    }

    private String getIdField(Class<?> clazz) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            Id annotation = declaredField.getAnnotation(Id.class);
            if (annotation != null) {
                Column column = declaredField.getAnnotation(Column.class);
                return column.name();
            }
        }
        return null;
    }

    public static class PathElement {
        private final String idField;
        private final String joinColumn;
        private final String name;

        public PathElement(String name, String idField, String joinColumn) {
            this.name = name;
            this.idField = idField;
            this.joinColumn = joinColumn;
        }

        public String getName() {
            return name;
        }

        public String getIdField() {
            return idField;
        }

        public String getJoinColumn() {
            return joinColumn;
        }
    }

}

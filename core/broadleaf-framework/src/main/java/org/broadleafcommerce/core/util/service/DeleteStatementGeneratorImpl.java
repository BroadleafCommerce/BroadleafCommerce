/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;

/**
 * The idea of this class if to iterate over props of the passed class and if found prop with annotation @OneToMany
 * recurse into it. In this way we build paths to the dependant classes. Once found "leaf" class that doesn't have
 * any more dependant entities it will start to go back and build path for that classes. In a result we will have
 * table name and path to that tabe from the root table, so we can build sql to delete dependencies in the right order.
 * It knows how to deal with @Embedded, bidirectional @OneToMany, @OneToMany through @JoinTable
 */
@Service("blDeleteStatementGenerator")
public class DeleteStatementGeneratorImpl implements DeleteStatementGenerator {

    protected static final Log LOG = LogFactory.getLog(DeleteStatementGeneratorImpl.class);

    protected DynamicDaoHelperImpl helper = new DynamicDaoHelperImpl();

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    /**
     * @param rootType        type to start build sql dependency list
     * @param rootTypeIdValue value of id column of the rootType to be passed in where clause
     * @param dependencies    - map of additional dependencies that can't be discovered during iteration from root type,
     *                        key is table name to depend on like blc_order->some other table, value is structure representing dependant table, its PK, and FK to join with/
     * @param exclustions     - Set with table name to exclude from list, like price_list, etc
     * @return a Map which key is a table name, and value is string with sql delete statement.
     * This map is the LinkedHashMap so order of keys is important. You should exec sql from the beginning.
     */
    public Map<String, String> generateDeleteStatementsForType(
            Class<?> rootType,
            String rootTypeIdValue,
            Map<String, List<PathElement>> dependencies,
            Set<String> exclustions
    ) {
        if (StringUtils.isEmpty(rootTypeIdValue)) {
            rootTypeIdValue = "XX";
        }
        LOG.info("Start generating SQL delete statements for type:" + rootType.getSimpleName());
        Stack<PathElement> stack = new Stack<>();
        Set<Class<?>> processedClasses = new HashSet<>();
        LinkedHashMap<String, OperationStackHolder> result = new LinkedHashMap<>();
        diveDeep(
                rootType,
                null,
                null,
                stack,
                processedClasses,
                result,
                dependencies,
                exclustions,
                false,
                null
        );

        Object[] objects = result.entrySet().toArray();
        Arrays.sort(objects, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                OperationStackHolder val2 = ((Map.Entry<String, OperationStackHolder>) o2).getValue();
                OperationStackHolder val1 = ((Map.Entry<String, OperationStackHolder>) o1).getValue();
                if (rootType.equals(OrderImpl.class)) {
                    String key2 = ((Map.Entry<String, OperationStackHolder>) o2).getKey();
                    String key1 = ((Map.Entry<String, OperationStackHolder>) o1).getKey();
                    if (key2.equalsIgnoreCase("BLC_RETURN_CONFIRMATION")) {
                        return 1;
                    } else if (key1.equalsIgnoreCase("BLC_RETURN_CONFIRMATION")) {
                        return -1;
                    } else if (key2.equalsIgnoreCase("BLC_RETURN_AUTHORIZATION_ITEM")
                            && !key1.equalsIgnoreCase("BLC_RETURN_CONFIRMATION")) {
                        return 1;
                    } else if (key1.equalsIgnoreCase("BLC_RETURN_AUTHORIZATION_ITEM")
                            && !key2.equalsIgnoreCase("BLC_RETURN_CONFIRMATION")) {
                        return -1;
                    }
                }
                if (((val2.isUpdate() && !val2.isRelationshipUpdate()) && !val1.isUpdate()) || (val2.isXref() && !val1.isXref())) {
                    return 1;
                } else if ((val1.isUpdate() && !val1.isRelationshipUpdate()) && !val2.isUpdate() || (val1.isXref() && !val2.isXref())) {
                    return -1;
                } else if (val1.isRelationshipUpdate() && !val2.isRelationshipUpdate() && val1.getStack().size() + 1 == val2.getStack().size()) {
                    return -1;
                } else if (val2.isRelationshipUpdate() && !val1.isRelationshipUpdate() && val2.getStack().size() + 1 == val1.getStack().size()) {
                    return 1;
                } else {
                    return val2.getStack().size() - val1.getStack().size();
                }
            }
        });
        LinkedHashMap<String, String> sqls = new LinkedHashMap<>();
        for (Object entryObject : objects) {
            Map.Entry<String, OperationStackHolder> entry = (Map.Entry<String, OperationStackHolder>) entryObject;
            OperationStackHolder operationStackHolder = entry.getValue();
            String x = getSqls(rootTypeIdValue, operationStackHolder);
            sqls.put(entry.getKey(), x);
        }
        LOG.info("End generating SQL delete statements for type:" + rootType.getSimpleName() + ". Generated " + sqls.size() + " statements");
        return sqls;
    }

    protected String getSqls(String rootTypeIdValue, OperationStackHolder operationStackHolder) {
        StringBuilder builder = new StringBuilder();
        Stack<PathElement> value = operationStackHolder.getStack();
        PathElement prevTable = value.pop();
        boolean shouldAppendWhere = true;
        boolean shouldCloseParantheses = false;
        if (operationStackHolder.isUpdate()) {
            builder.append("update ").append(prevTable.getName()).append(" SET ")
                    .append(operationStackHolder.getColumnToUpdate()).append("=NULL");
        } else {
            builder.append("delete FROM ").append(prevTable.getName());
        }
        String prevTableAlias = "";
        if (value.size() == 1) {
            shouldAppendWhere = false;
            PathElement pop = value.pop();
            if (prevTable.isFromManyToOne()) {
                if (pop.getIdField().equals(prevTable.getJoinColumn())
                        || operationStackHolder.isManyToOneRelationshipDelete()) {
                    builder.append(" WHERE ")
                            .append(pop.getIdField()).append("=").append(rootTypeIdValue);
                } else {
                    builder.append(" WHERE ").append(prevTable.getIdField()).append(" IN (SELECT ")
                            .append("t.").append(prevTable.getJoinColumn()).append(" FROM ").append(pop.getName())
                            .append(" t WHERE ").append("t.").append(pop.getIdField()).append("=").append(rootTypeIdValue).append(")");
                }
            } else {
                builder.append(" WHERE ").append(prevTable.getJoinColumn()).append("=").append(rootTypeIdValue);
            }
        } else if (value.size() > 0) {
            shouldCloseParantheses = true;
            builder.append(" WHERE ").append(prevTable.getJoinColumn()).append(" IN (SELECT ");
            PathElement next = value.pop();
            builder.append("b").append(".").append(next.getIdField()).append(" FROM ")
                    .append(next.getName()).append(" b");
            prevTable = next;
            prevTableAlias = "b";
        }
        int index = 0;

        while (!value.empty()) {
            String nextTableAlias = "a" + index;
            PathElement next = value.pop();
            if (prevTable.isFromManyToOne()) {
                builder.append(" inner join ").append(next.getName()).append(" ").append(nextTableAlias).append(" on ")
                        .append(prevTableAlias).append(".").append(prevTable.getIdField()).append("=")
                        .append(nextTableAlias).append(".").append(prevTable.getJoinColumn());

            } else {
                builder.append(" inner join ").append(next.getName()).append(" ").append(nextTableAlias).append(" on ")
                        .append(prevTableAlias).append(".").append(prevTable.getJoinColumn()).append("=")
                        .append(nextTableAlias).append(".").append(next.getIdField());
            }
            prevTable = next;
            prevTableAlias = nextTableAlias;
            index++;
        }
        if (shouldAppendWhere) {
            builder.append(" WHERE ");
            if (StringUtils.isNotEmpty(prevTableAlias)) {
                builder.append(prevTableAlias).append(".");
            }
            builder.append(prevTable.getIdField()).append("=").append(rootTypeIdValue);
        }
        if (shouldCloseParantheses) {
            builder.append(")");
        }
        String x = builder.toString();
        LOG.debug(x);
        return x;
    }

    protected void diveDeep(
            Class<?> classToProcess,
            String joinColumn,
            String mappedBy,
            Stack<PathElement> stack,
            Set<Class<?>> processedClasses,
            HashMap<String, OperationStackHolder> result,
            Map<String, List<PathElement>> dependencies,
            Set<String> exclusions,
            boolean fromManyToOne,
            Class prevProcessedClass
    ) {
        Class<?>[] classes = helper.getAllPolymorphicEntitiesFromCeiling(
                classToProcess, false, true
        );
        if (processedClasses.contains(classToProcess)) {
            return;
        }
        String idField = getIdField(classToProcess);
        if (idField == null) {
            idField = joinColumn;
        }
        processedClasses.add(classToProcess);

        Field[] declaredFields = classToProcess.getDeclaredFields();
        Table tableAnnotation = classToProcess.getAnnotation(Table.class);
        if (StringUtils.isNotEmpty(mappedBy)) {
            Field field = getField(mappedBy, declaredFields);
            JoinColumn annotation = field.getAnnotation(JoinColumn.class);
            joinColumn = annotation.name();
        }
        if (exclusions.contains(tableAnnotation.name())) {
            return;
        }
        PathElement el = new PathElement(tableAnnotation.name(), idField, joinColumn, fromManyToOne);
        stack.push(el);
        for (Class<?> aClass : classes) {
            if (!aClass.equals(classToProcess)) {
                diveDeep(
                        aClass,
                        idField,
                        null,
                        stack,
                        processedClasses,
                        result,
                        dependencies,
                        exclusions,
                        false,
                        classToProcess
                );
            }
        }
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Id.class) != null) {
                continue;
            }
            OneToMany annotation = declaredField.getAnnotation(OneToMany.class);
            if (annotation != null) {
                processField(
                        stack,
                        processedClasses,
                        result,
                        declaredField,
                        dependencies,
                        exclusions,
                        false,
                        classToProcess
                );
            } else if (declaredField.getAnnotation(Embedded.class) != null) {
                Class<?> type = declaredField.getType();
                for (Field decl : type.getDeclaredFields()) {
                    processField(
                            stack,
                            processedClasses,
                            result,
                            decl,
                            dependencies,
                            exclusions,
                            true,
                            classToProcess
                    );
                }
            } else if (declaredField.getAnnotation(CollectionTable.class) != null) {
                CollectionTable collectionTable = declaredField.getAnnotation(CollectionTable.class);
                JoinColumn collectionJoinColumn = collectionTable.joinColumns()[0];
                String collectionTableName = collectionTable.name();
                PathElement p = new PathElement(
                        collectionTableName, collectionJoinColumn.name(), collectionJoinColumn.name()
                );
                stack.push(p);
                result.put(collectionTableName, new OperationStackHolder((Stack<PathElement>) stack.clone()));
                stack.pop();
            } else if (declaredField.getAnnotation(ManyToOne.class) != null) {
                ManyToOne manyToOne = declaredField.getAnnotation(ManyToOne.class);
                if (manyToOne.targetEntity().equals(classToProcess)) {
                    JoinColumn selfJoinColumn = declaredField.getAnnotation(JoinColumn.class);
                    result.put(
                            tableAnnotation.name() + "_UPDATE",
                            new OperationStackHolder(
                                    (Stack<PathElement>) stack.clone(), true, selfJoinColumn.name()
                            )
                    );
                } else if (manyToOne.targetEntity().equals(prevProcessedClass) && fromManyToOne) {
                    //ManyToMany without 3rd table
                    Stack<PathElement> clone = (Stack<PathElement>) stack.clone();
                    PathElement pop = clone.pop();
                    result.put(
                            tableAnnotation.name() + "_UPDATE",
                            new OperationStackHolder(clone, true, pop.getJoinColumn(), true)
                    );
                }/*else{
                    JoinColumn jnColumn = declaredField.getAnnotation(JoinColumn.class);
                    diveDeep(manyToOne.targetEntity(), jnColumn.name(), null, stack, processedClasses, result, dependencies, exclusions, true, classToProcess);
                }*/
            } else if (declaredField.getAnnotation(ManyToMany.class) != null) {
                JoinTable joinTable = declaredField.getAnnotation(JoinTable.class);
                JoinColumn[] joinColumns = joinTable.joinColumns();
                stack.push(
                        new PathElement(joinTable.name(), joinColumns[0].referencedColumnName(), joinColumns[0].name())
                );
                result.put(joinTable.name(), new OperationStackHolder((Stack<PathElement>) stack.clone(), true));
                stack.pop();
            }/*else if(declaredField.getAnnotation(OneToOne.class)!=null){
                JoinColumn jnColumn = declaredField.getAnnotation(JoinColumn.class);
                diveDeep(declaredField.getAnnotation(OneToOne.class).targetEntity(), jnColumn.name(), null, stack, processedClasses, result, dependencies, exclusions, true, classToProcess);
            }*/
        }
        processManualDefinedDependencies(stack, result, dependencies, tableAnnotation.name());
        OperationStackHolder operationStackHolder = result.get(tableAnnotation.name() + "_UPDATE");
        //so this is biderectional and we will update one side to set null, to to delete we need to use another side of relationship
        //mostly for order->blc_quote->order relationship case.
        if (operationStackHolder != null && operationStackHolder.isRelationshipUpdate()) {
            result.put(
                    tableAnnotation.name(),
                    new OperationStackHolder(
                            (Stack<PathElement>) stack.clone(),
                            false,
                            "",
                            false,
                            true
                    )
            );
        } else {
            result.put(tableAnnotation.name(), new OperationStackHolder((Stack<PathElement>) stack.clone()));
        }
        stack.pop();
    }

    protected void processManualDefinedDependencies(
            Stack<PathElement> stack,
            HashMap<String, OperationStackHolder> result,
            Map<String, List<PathElement>> dependencies,
            String tableName
    ) {
        List<PathElement> pathElement = dependencies.get(tableName);
        if (pathElement != null) {
            for (PathElement p : pathElement) {
                stack.push(p);
                if (dependencies.get(p.getName()) != null) {
                    processManualDefinedDependencies(stack, result, dependencies, p.getName());
                }
                String name = p.getName();
                if (result.get(p.getName()) != null) {
                    name += "_DUPLICATE";
                }
                result.put(name, new OperationStackHolder((Stack<PathElement>) stack.clone()));
                stack.pop();
            }
        }
    }

    protected void processField(
            Stack<PathElement> stack,
            Set<Class<?>> processedClasses,
            HashMap<String, OperationStackHolder> result,
            Field decl,
            Map<String, List<PathElement>> dependencies,
            Set<String> exclustions,
            boolean fromEmbedded,
            Class prevClassToProcess
    ) {
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
                if (exclustions.contains(joinTable.name())) {
                    return;
                }
                PathElement pathElement = new PathElement(joinTable.name(), inverseJoinColumn.name(), jnColumn.name());
                stack.push(pathElement);
                joinColumnFromJoinTable = inverseJoinColumn.name();
                result.put(joinTableNameFromJoinTable, new OperationStackHolder((Stack<PathElement>) stack.clone(), true));
            }
            diveDeep(
                    aClass,
                    joinColumnFromJoinTable,
                    mappedByAnnotation,
                    stack,
                    processedClasses,
                    result,
                    dependencies,
                    exclustions,
                    false,
                    prevClassToProcess
            );
            if (StringUtils.isNotEmpty(joinTableNameFromJoinTable)) {
                if (!result.containsKey(joinTableNameFromJoinTable)) {
                    result.put(joinTableNameFromJoinTable, new OperationStackHolder((Stack<PathElement>) stack.clone()));
                }
                stack.pop();
            }
        } else if (decl.getAnnotation(ManyToOne.class) != null) {
            ManyToOne manyToOne = decl.getAnnotation(ManyToOne.class);
            Class aClass = manyToOne.targetEntity();
            if (!processedClasses.contains(aClass)) {
                String joinColumnName = decl.getAnnotation(JoinColumn.class).name();
                diveDeep(
                        aClass,
                        joinColumnName,
                        null,
                        stack,
                        processedClasses,
                        result,
                        dependencies,
                        exclustions,
                        fromEmbedded,
                        prevClassToProcess
                );
            }
        } else if (decl.getAnnotation(OneToOne.class) != null) {
            OneToOne manyToOne = decl.getAnnotation(OneToOne.class);
            Class aClass = manyToOne.targetEntity();
            if (!processedClasses.contains(aClass)) {
                String joinColumnName = decl.getAnnotation(JoinColumn.class).name();
                diveDeep(
                        aClass,
                        joinColumnName,
                        null,
                        stack,
                        processedClasses,
                        result,
                        dependencies,
                        exclustions,
                        fromEmbedded,
                        prevClassToProcess
                );
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
        private final boolean fromManyToOne;

        /**
         * @param name          table name
         * @param idField       id field name
         * @param joinColumn    fk column name in this table to join with "parent table", like blc_order->order_item, fk is ORDER_ID
         * @param fromManyToOne is this path came from @ManyToOne - ManyToMany case without 3rd table
         *                      (or exceptional case @OneToOne biderectional with one site @ManyToOne)
         */
        public PathElement(String name, String idField, String joinColumn, boolean fromManyToOne) {
            this.name = name;
            this.idField = idField;
            this.joinColumn = joinColumn;
            this.fromManyToOne = fromManyToOne;
        }

        public PathElement(String name, String idField, String joinColumn) {
            this(name, idField, joinColumn, false);
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

        public boolean isFromManyToOne() {
            return fromManyToOne;
        }
    }

    public static class OperationStackHolder {
        private Stack<PathElement> stack;
        private boolean isUpdate;
        private String columnToUpdate;
        private boolean xref;
        private boolean relationshipUpdate;
        private boolean manyToOneRelationshipDelete;

        public OperationStackHolder(
                Stack<PathElement> stack,
                boolean isUpdate,
                String columnToUpdate,
                boolean relationshipUpdate
        ) {
            this.stack = stack;
            this.isUpdate = isUpdate;
            this.columnToUpdate = columnToUpdate;
            this.relationshipUpdate = relationshipUpdate;
        }

        public OperationStackHolder(
                Stack<PathElement> stack,
                boolean isUpdate,
                String columnToUpdate,
                boolean relationshipUpdate,
                boolean manyToOneRelationshipDelete
        ) {
            this(stack, isUpdate, columnToUpdate, relationshipUpdate);
            this.manyToOneRelationshipDelete = manyToOneRelationshipDelete;
        }

        public OperationStackHolder(Stack<PathElement> stack, boolean isUpdate, String columnToUpdate) {
            this.stack = stack;
            this.isUpdate = isUpdate;
            this.columnToUpdate = columnToUpdate;
        }

        public OperationStackHolder(Stack<PathElement> stack) {
            this(stack, false, null);
        }

        public OperationStackHolder(Stack<PathElement> stack, boolean isXref) {
            this(stack, false, null);
            this.xref = isXref;
        }

        public Stack<PathElement> getStack() {
            return stack;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public String getColumnToUpdate() {
            return columnToUpdate;
        }

        public boolean isXref() {
            return xref;
        }

        public boolean isRelationshipUpdate() {
            return relationshipUpdate;
        }

        public boolean isManyToOneRelationshipDelete() {
            return manyToOneRelationshipDelete;
        }
    }

}

/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.persistence;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TableGenerator;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Detect inconsistencies between the values in the SEQUENCE_GENERATOR and the primary
 * keys of the managed tables.
 *
 * @author Jeff Fischer
 */
@Repository("blSequenceGeneratorCorruptionDetection")
public class SequenceGeneratorCorruptionDetection implements ApplicationListener<ContextRefreshedEvent> {

    private static final Log LOG = LogFactory.getLog(SequenceGeneratorCorruptionDetection.class);

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Value("${detect.sequence.generator.inconsistencies}")
    protected boolean detectSequenceGeneratorInconsistencies = true;

    @Value("${auto.correct.sequence.generator.inconsistencies}")
    protected boolean automaticallyCorrectInconsistencies = false;

    @Value("${default.schema.sequence.generator}")
    protected String defaultSchemaSequenceGenerator = "";

    @Override
    @Transactional("blTransactionManager")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (detectSequenceGeneratorInconsistencies) {
            SessionFactory sessionFactory = ((HibernateEntityManager) em).getSession().getSessionFactory();
            for (Object item : sessionFactory.getAllClassMetadata().values()) {
                ClassMetadata metadata = (ClassMetadata) item;
                String idProperty = metadata.getIdentifierPropertyName();
                Class<?> mappedClass = metadata.getMappedClass();
                Field idField;
                try {
                    idField = mappedClass.getDeclaredField(idProperty);
                } catch (NoSuchFieldException e) {
                    continue;
                }
                idField.setAccessible(true);
                GenericGenerator genericAnnot = idField.getAnnotation(GenericGenerator.class);
                TableGenerator tableAnnot = idField.getAnnotation(TableGenerator.class);
                String segmentValue = null;
                String tableName = null;
                String segmentColumnName = null;
                String valueColumnName = null;
                if (genericAnnot != null && genericAnnot.strategy().equals("org.broadleafcommerce.common.persistence.IdOverrideTableGenerator")) {
                    //This is a BLC style ID generator
                    for (Parameter param : genericAnnot.parameters()) {
                        if (param.name().equals("segment_value")) {
                            segmentValue = param.value();
                        }
                        if (param.name().equals("table_name")) {
                            tableName = param.value();
                        }
                        if (param.name().equals("segment_column_name")) {
                            segmentColumnName = param.value();
                        }
                        if (param.name().equals("value_column_name")) {
                            valueColumnName = param.value();
                        }
                    }
                } else if (tableAnnot != null) {
                    //This is a traditional Hibernate generator
                    segmentValue = tableAnnot.pkColumnValue();
                    tableName = tableAnnot.table();
                    segmentColumnName = tableAnnot.pkColumnName();
                    valueColumnName = tableAnnot.valueColumnName();
                }
                if (!StringUtils.isEmpty(segmentValue) && !StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(segmentColumnName) && !StringUtils.isEmpty(valueColumnName)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("select ");
                    sb2.append(valueColumnName);
                    sb2.append(" from ");
                    if (!tableName.contains(".") && !StringUtils.isEmpty(defaultSchemaSequenceGenerator)) {
                        sb2.append(defaultSchemaSequenceGenerator);
                        sb2.append(".");
                    }
                    sb2.append(tableName);
                    sb2.append(" where ");
                    sb2.append(segmentColumnName);
                    sb2.append(" = '");
                    sb2.append(segmentValue);
                    sb2.append("'");

                    List results2 = em.createNativeQuery(sb2.toString()).getResultList();
                    if (results2 != null && !results2.isEmpty() && results2.get(0) != null) {
                        Long maxSequenceId = ((Number) results2.get(0)).longValue();

                        LOG.info("Detecting id sequence state between " + mappedClass.getName() + " and " + segmentValue + " in " + tableName);

                        StringBuilder sb = new StringBuilder();
                        sb.append("select max(");
                        sb.append(idField.getName());
                        sb.append(") from ");
                        sb.append(mappedClass.getName());
                        sb.append(" entity");
                        List results = em.createQuery(sb.toString()).getResultList();
                        if (results != null && !results.isEmpty() && results.get(0) != null) {
                            Long maxEntityId = (Long) results.get(0);
                            if (maxEntityId > maxSequenceId) {
                                if (automaticallyCorrectInconsistencies) {
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("update ");
                                    if (!tableName.contains(".") && !StringUtils.isEmpty(defaultSchemaSequenceGenerator)) {
                                        sb2.append(defaultSchemaSequenceGenerator);
                                        sb2.append(".");
                                    }
                                    sb3.append(tableName);
                                    sb3.append(" set ");
                                    sb3.append(valueColumnName);
                                    sb3.append(" = ");
                                    sb3.append(String.valueOf(maxEntityId + 10));
                                    sb3.append(" where ");
                                    sb3.append(segmentColumnName);
                                    sb3.append(" = '");
                                    sb3.append(segmentValue);
                                    sb3.append("'");

                                    int response = em.createNativeQuery(sb3.toString()).executeUpdate();
                                    if (response <= 0) {
                                        throw new RuntimeException("Unable to update " + tableName + " with the sequence generator id for " + segmentValue);
                                    }
                                } else {
                                    String reason = "A data inconsistency has been detected between the " + tableName + " table and one or more entity tables for which it manages current max primary key values.\n" +
                                        "The inconsistency was detected between the managed class (" + mappedClass.getName() + ") and the identifier (" + segmentValue + ") in " + tableName + ". Broadleaf\n" +
                                        "has stopped startup of the application in order to allow you to resolve the issue and avoid possible data corruption. If you wish to disable this detection, you may\n" +
                                        "set the 'detect.sequence.generator.inconsistencies' property to false in your application's common.properties or common-shared.properties. If you would like for this component\n" +
                                        "to autocorrect these problems by setting the sequence generator value to a value greater than the max entity id, then set the 'auto.correct.sequence.generator.inconsistencies'\n" +
                                        "property to true in your application's common.properties or common-shared.properties. If you would like to provide a default schema to be used to qualify table names used in the\n" +
                                        "queries for this detection, set the 'default.schema.sequence.generator' property in your application's common.properties or common-shared.properties. Also, if you are upgrading\n" +
                                        "from 1.6 or below, please refer to http://docs.broadleafcommerce.org/current/1.6-to-2.0-Migration.html for important information regarding migrating your SEQUENCE_GENERATOR table.";
                                    LOG.error("Broadleaf Commerce failed to start", new RuntimeException(reason));
                                    System.exit(1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

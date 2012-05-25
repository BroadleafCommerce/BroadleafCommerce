/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.common.persistence;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/4/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdOverrideTableGenerator extends TableGenerator {

    public static final String ENTITY_NAME_PARAM = "entity_name";

    private String entityName;

    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        final Serializable id = session.getEntityPersister(entityName, obj).getIdentifier( obj, session );
		if ( id != null ) {
            return id;
        }
        return super.generate(session, obj);
    }

    @Override
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        if (params.get("table_name") == null) {
            params.put("table_name", "SEQUENCE_GENERATOR");
        }

        if (params.get("segment_column_name") == null) {
            params.put("segment_column_name", "ID_NAME");
        }

        if (params.get("value_column_name") == null) {
            params.put("value_column_name", "ID_VAL");
        }

        if (params.get("increment_size") == null) {
            params.put("increment_size", 50);
        }
        super.configure(type, params, dialect);
        entityName = (String) params.get(ENTITY_NAME_PARAM);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}

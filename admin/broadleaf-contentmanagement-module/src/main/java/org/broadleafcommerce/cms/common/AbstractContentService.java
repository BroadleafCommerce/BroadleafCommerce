/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

/**
 * PageService and StructuredContentService are similar and can share
 * much of the logic.   This class provides a place for that code.
 * 
 * @author bpolster
 */
public class AbstractContentService  {
    private static final Log LOG = LogFactory.getLog(AbstractContentService.class);

    public <T, U> List<T> findItems(SandBox sandbox, Criteria c, Class<T> baseClass, Class<U> concreteClass, String originalIdProperty) {
        c.add(Restrictions.eq("archivedFlag", false));

        if (sandbox == null) {
            // Query is hitting the production sandbox for a single site
            c.add(Restrictions.isNull("sandbox"));
            return (List<T>) c.list();
        } if (SandBoxType.PRODUCTION.equals(sandbox.getSandBoxType())) {
            // Query is hitting the production sandbox for a multi-site
            c.add(Restrictions.eq("sandbox", sandbox));
            return (List<T>) c.list();
        } else {
            addSandboxCriteria(sandbox, c, concreteClass, originalIdProperty);
            return (List<T>) c.list();
        }
    }

    public <T> Long countItems(SandBox sandbox, Criteria c, Class<T> concreteClass, String originalIdProperty) {
        c.add(Restrictions.eq("archivedFlag", false));
        c.setProjection(Projections.rowCount());

        if (sandbox == null) {
            // Query is hitting the production sandbox for a single site
            c.add(Restrictions.isNull("sandbox"));
            return (Long) c.uniqueResult();
        } if (SandBoxType.PRODUCTION.equals(sandbox.getSandBoxType())) {
            // Query is hitting the production sandbox for a multi-site
            c.add(Restrictions.eq("sandbox", sandbox));
            return (Long) c.uniqueResult();
        } else {
            addSandboxCriteria(sandbox, c, concreteClass, originalIdProperty);
            return (Long) c.uniqueResult();
        }
    }

    private <T> void addSandboxCriteria(SandBox sandbox, Criteria c, Class<T> type, String originalIdProperty) {
        Criterion originalSandboxExpression = Restrictions.eq("originalSandBox", sandbox);
        Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
        Criterion userSandboxExpression = Restrictions.or(currentSandboxExpression, originalSandboxExpression);
        Criterion productionSandboxExpression = Restrictions.isNull("sandbox");

        if (productionSandboxExpression != null) {
            c.add(Restrictions.or(userSandboxExpression, productionSandboxExpression));
        } else {
            c.add(userSandboxExpression);
        }

        // Build a sub-query to exclude items from production that are also in my sandbox.
        // (e.g. my sandbox always wins even if the items in my sandbox don't match the
        // current criteria.)
        //
        // This subquery prevents the following:
        // 1.  Duplicate items (one for sbox, one for prod)
        // 2.  Filter issues where the production item qualifies for the passed in criteria
        //     but has been modified so that the item in the sandbox no longer does.
        // 3.  Inverse of #2.
        DetachedCriteria existsInSboxCriteria = DetachedCriteria.forClass(type, "sboxItem");
        existsInSboxCriteria.add(userSandboxExpression);
        existsInSboxCriteria.add(Restrictions.eq("archivedFlag", false));
        String outerAlias = c.getAlias();
        existsInSboxCriteria.add(Property.forName(outerAlias + ".id").eqProperty("sboxItem."+originalIdProperty));
        existsInSboxCriteria.setProjection(Projections.id());
        c.add(Subqueries.notExists(existsInSboxCriteria));
    }
}

/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate;

import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * @author Jeff Fischer
 */
@Component("blBetweenDatePredicateProvider")
public class BetweenDatePredicateProvider implements PredicateProvider<Comparable, Comparable> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, From root,
                                    String ceilingEntity, String fullPropertyName, Path<Comparable> explicitPath,
                                    List<Comparable> directValues) {
        Path<Comparable> path;
        if (explicitPath != null) {
            path = explicitPath;
        } else {
            path = fieldPathBuilder.getPath(root, fullPropertyName, builder);
        }
        if (directValues.size() == 2) {
            if (directValues.get(0) == null) {
                return builder.lessThan(path, directValues.get(1));
            } else if (directValues.get(1) == null) {
                return builder.greaterThanOrEqualTo(path, directValues.get(0));
            }
            return builder.between(path, directValues.get(0), directValues.get(1));
        } else {
            // The user passed in a single date which is only down to the second granularity. The database stores things
            // down to the millisecond, so we can't just do equals we have to filter dates between the date provided and
            // 1000 milliseconds later than the date provided to get all records for that particular second
            Date secondFromNow = new Date(((Date)directValues.get(0)).getTime() + 1000);
            return builder.between(path, directValues.get(0), secondFromNow);
        }
    }
}

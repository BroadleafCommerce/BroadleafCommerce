/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

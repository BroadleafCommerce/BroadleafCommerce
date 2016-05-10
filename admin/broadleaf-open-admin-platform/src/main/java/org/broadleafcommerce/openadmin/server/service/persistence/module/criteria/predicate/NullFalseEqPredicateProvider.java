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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.springframework.stereotype.Component;

/**
 * This predicate provider is very similar to the {@link EqPredicateProvider}, except that it will treat
 * nulls equal to false. This implementation will provide an equality clause for the character 'N' and
 * {@link Boolean#FALSE}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blNullFalseEqPredicateProvider")
public class NullFalseEqPredicateProvider implements PredicateProvider<Serializable, Serializable> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, From root, String ceilingEntity,
                                    String fullPropertyName, Path<Serializable> explicitPath, List<Serializable> directValues) {
        Path<Serializable> path;
        if (explicitPath != null) {
            path = explicitPath;
        } else {
            path = fieldPathBuilder.getPath(root, fullPropertyName, builder);
        }
        
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Serializable directValue : directValues) {
            boolean attachNullClause = false;
            if (directValue instanceof Boolean) {
                if (((Boolean) directValue).equals(Boolean.FALSE)) {
                    attachNullClause = true;
                }
            } else if (directValue instanceof Character) {
                if (((Character) directValue).equals('N')) {
                    attachNullClause = true;
                }
            }
            
            if (attachNullClause) {
                predicates.add(
                    builder.or( 
                        builder.equal(path, directValue),
                        builder.isNull(path)
                    )
                );
            } else {
                predicates.add(builder.equal(path, directValue));
            }
        }
        
        return builder.or(predicates.toArray(new Predicate[predicates.size()]));
    }
}

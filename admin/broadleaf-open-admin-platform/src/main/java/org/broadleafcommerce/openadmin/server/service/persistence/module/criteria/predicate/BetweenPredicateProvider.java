package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate;

import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @author Jeff Fischer
 */
@Component("blBetweenPredicateProvider")
public class BetweenPredicateProvider implements PredicateProvider<Comparable, Comparable> {

    @Override
    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, From root, String ceilingEntity,
                                    String fullPropertyName, Path<Comparable> explicitPath, List<Comparable> directValues) {
        Path<Comparable> path;
        if (explicitPath != null) {
            path = explicitPath;
        } else {
            path = fieldPathBuilder.getPath(root, fullPropertyName);
        }
        if (directValues.size() > 1) {
            return builder.between(path, directValues.get(0), directValues.get(1));
        } else {
            return builder.equal(path, directValues.get(0));
        }
    }
}

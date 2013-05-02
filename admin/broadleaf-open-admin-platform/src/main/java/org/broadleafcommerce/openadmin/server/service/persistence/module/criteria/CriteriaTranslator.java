package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public interface CriteriaTranslator {

    TypedQuery<Serializable> translateQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings, Integer firstResult, Integer maxResults);

    TypedQuery<Serializable> translateCountQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings);

}

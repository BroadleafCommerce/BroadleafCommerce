package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.openadmin.dto.ListGridFetchRequest;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public interface ListGridFetchFactory {
    ListGridFetchRequest getListGridFetchRequest(PersistencePackageRequest request);

    ListGridFetchRequest createListGridFetchRequest(String ceilingEntity);

    void setAdditionalFieldsToListGridFetchRequest(ListGridFetchRequest listGridFetchRequest, ListGridFetchEntity matchedEntity);

    ListGridFetchEntity attemptToFindRegexTarget(String ceilingEntity, ListGridFetchEntity listGridFetchEntity);

    List<String> getUniversalFetchFields();

    void setUniversalFetchFields(List<String> universalFetchFields);

    List<ListGridFetchEntity> getEntities();

    void setEntities(List<ListGridFetchEntity> entities);
}

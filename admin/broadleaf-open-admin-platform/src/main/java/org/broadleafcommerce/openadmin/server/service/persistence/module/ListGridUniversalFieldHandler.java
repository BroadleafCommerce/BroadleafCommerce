package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.openadmin.dto.ListGridFetchRequest;

import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public interface ListGridUniversalFieldHandler {

    Boolean canHandleEntity(ListGridFetchRequest listGridFetchRequest, ListGridFetchEntity matchedEntity);

    List<String> getUniversalFetchFields();

    void setUniversalFetchFields(List<String> universalFetchFields);
}

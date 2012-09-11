package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public interface PropertyBuilder {

    public Map<String, FieldMetadata> execute(Boolean overridePopulateManyToOne);

}

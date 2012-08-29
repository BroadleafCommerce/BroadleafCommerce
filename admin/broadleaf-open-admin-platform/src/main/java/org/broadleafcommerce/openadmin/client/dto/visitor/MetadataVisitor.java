package org.broadleafcommerce.openadmin.client.dto.visitor;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;

/**
 * @author Jeff Fischer
 */
public interface MetadataVisitor {

    public void visit(BasicFieldMetadata metadata);

    public void visit(CollectionMetadata metadata);

}

package org.broadleafcommerce.openadmin.client.dto.visitor;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;

/**
 * @author Jeff Fischer
 */
public class MetadataVisitorAdapter implements MetadataVisitor {

    @Override
    public void visit(BasicFieldMetadata metadata) {
        //do nothing
    }

    @Override
    public void visit(CollectionMetadata metadata) {
        //do nothing
    }
}

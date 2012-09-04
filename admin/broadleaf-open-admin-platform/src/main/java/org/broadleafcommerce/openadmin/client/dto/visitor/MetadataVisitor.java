package org.broadleafcommerce.openadmin.client.dto.visitor;

import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;

/**
 * @author Jeff Fischer
 */
public interface MetadataVisitor {

    public void visit(BasicFieldMetadata metadata);

    public void visit(BasicCollectionMetadata metadata);

    public void visit(AdornedTargetCollectionMetadata metadata);

    public void visit(MapMetadata metadata);
}

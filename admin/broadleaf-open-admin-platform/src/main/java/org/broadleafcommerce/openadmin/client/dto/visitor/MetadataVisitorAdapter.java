package org.broadleafcommerce.openadmin.client.dto.visitor;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public class MetadataVisitorAdapter implements MetadataVisitor, Serializable, IsSerializable {

    @Override
    public void visit(AdornedTargetCollectionMetadata metadata) {
        throw new IllegalArgumentException("Not supported in this context");
    }

    @Override
    public void visit(BasicFieldMetadata metadata) {
        throw new IllegalArgumentException("Not supported in this context");
    }

    @Override
    public void visit(BasicCollectionMetadata metadata) {
        throw new IllegalArgumentException("Not supported in this context");
    }

    @Override
    public void visit(MapMetadata metadata) {
        throw new IllegalArgumentException("Not supported in this context");
    }
}

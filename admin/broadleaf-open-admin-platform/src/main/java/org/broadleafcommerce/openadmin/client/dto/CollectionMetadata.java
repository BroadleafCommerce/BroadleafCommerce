package org.broadleafcommerce.openadmin.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.broadleafcommerce.common.presentation.AddType;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public class CollectionMetadata implements IsSerializable, Serializable {

    private OperationTypes operationTypes;
    private AddType addType;

    public AddType getAddType() {
        return addType;
    }

    public void setAddType(AddType addType) {
        this.addType = addType;
    }

    public OperationTypes getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(OperationTypes operationTypes) {
        this.operationTypes = operationTypes;
    }

    public CollectionMetadata cloneCollectionMetadata() {
        CollectionMetadata metadata = new CollectionMetadata();
        metadata.setAddType(addType);
        metadata.setOperationTypes(operationTypes);

        return metadata;
    }
}

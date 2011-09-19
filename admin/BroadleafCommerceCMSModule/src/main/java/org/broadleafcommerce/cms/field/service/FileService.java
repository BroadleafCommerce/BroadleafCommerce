package org.broadleafcommerce.cms.field.service;

import org.broadleafcommerce.cms.field.domain.FieldData;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/19/11
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FileService {
    FieldData readFieldDataById(Long id);

    FieldData updateFieldData(FieldData fieldData);

    void delete(FieldData fieldData);

    FieldData addFieldData(FieldData fieldData);
}

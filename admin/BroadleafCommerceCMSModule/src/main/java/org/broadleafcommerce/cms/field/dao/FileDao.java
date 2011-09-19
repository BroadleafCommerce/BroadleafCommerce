package org.broadleafcommerce.cms.field.dao;

import org.broadleafcommerce.cms.field.domain.FieldData;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/19/11
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FileDao {
    FieldData readFieldDataById(Long id);

    FieldData updateFieldData(FieldData fieldData);

    void delete(FieldData fieldData);

    FieldData addFieldData(FieldData fieldData);
}

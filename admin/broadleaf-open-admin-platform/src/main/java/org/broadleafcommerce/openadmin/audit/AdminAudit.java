package org.broadleafcommerce.openadmin.audit;

import java.util.Date;

/**
 * @author Jeff Fischer
 */
public interface AdminAudit {

    Date getDateCreated();

    Date getDateUpdated();

    void setDateCreated(Date dateCreated);

    void setDateUpdated(Date dateUpdated);

    Long getCreatedBy();

    void setCreatedBy(Long createdBy);

    Long getUpdatedBy();

    void setUpdatedBy(Long updatedBy);

}

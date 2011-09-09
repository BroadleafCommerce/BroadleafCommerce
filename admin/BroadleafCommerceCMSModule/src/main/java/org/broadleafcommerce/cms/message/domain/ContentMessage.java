package org.broadleafcommerce.cms.message.domain;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface ContentMessage {
    public String getDescription();

    public void setDescription(String description);

    public Boolean getKeyEditableFlag();

    public void setKeyEditableFlag(Boolean keyEditableFlag);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public void addContentValue(ContentMessageValue contentMessageValue);

    public Map<String,ContentMessageValue> getContentMessageValues();
}

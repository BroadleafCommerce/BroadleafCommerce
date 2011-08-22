package org.broadleafcommerce.cms.message.domain;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface ContentMessage {
    public String getDescription();

    public void setDescription(String description);

    public Character getKeyEditableFlag();

    public void setKeyEditableFlag(Character keyEditableFlag);

    public Character getDeleted();

    public void setDeleted(Character deleted);

    public void addContentValue(ContentMessageValue contentMessageValue);

    public Map<String,ContentMessageValue> getContentMessageValues();
}

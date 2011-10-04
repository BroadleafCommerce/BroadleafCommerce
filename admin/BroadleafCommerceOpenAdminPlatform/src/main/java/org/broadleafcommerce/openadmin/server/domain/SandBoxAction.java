package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface SandBoxAction {
    public Long getId();

    public void setId(Long id);

    public SandBoxActionType getActionType();

    public void setActionType(SandBoxActionType type);

    public String getComment();

    public void setComment(String comment);

    public List<SandBoxItem> getSandBoxItems();

    public void setSandBoxItems(List<SandBoxItem> itemList);

    public void addSandBoxItem(SandBoxItem item);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
}

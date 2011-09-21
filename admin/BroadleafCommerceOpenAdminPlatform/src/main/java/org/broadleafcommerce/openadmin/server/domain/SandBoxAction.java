package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

import java.util.Date;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface SandBoxAction {
    public Long getId();

    public void setId(Long id);

    public SandBoxActionType getActionType();

    public void setActionType(SandBoxActionType type);

    public Date getActionDate();

    public void setActionDate(Date date);

    public AdminUser getUser();

    public void setUser(AdminUser user);

    public String getComment();

    public void setComment(String comment);

    public List<SandBoxItem> getSandBoxItems();

    public void setSandBoxItems(List<SandBoxItem> itemList);

    public void addSandBoxItem(SandBoxItem item);

}

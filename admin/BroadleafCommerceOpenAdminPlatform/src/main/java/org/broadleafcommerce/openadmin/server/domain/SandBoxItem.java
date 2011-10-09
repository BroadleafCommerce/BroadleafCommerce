package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

import java.io.Serializable;
import java.util.List;

public interface SandBoxItem extends Serializable {

	public Long getId();

	public void setId(Long id);

    public SandBox getSandBox();

	public void setSandBox(SandBox sandBox);

    public SandBox getOriginalSandBox();

	public void setOriginalSandBox(SandBox sandBox);

    public SandBoxItemType getSandBoxItemType();

    public void setSandBoxItemType(SandBoxItemType itemType);

    public SandBoxOperationType getSandBoxOperationType();

    public void setSandBoxOperationType(SandBoxOperationType type);

    public String getDescription();

    public void setDescription(String description);

    public Long getTemporaryItemId();

    public void setTemporaryItemId(Long id);

    public Long getOriginalItemId();

    public void setOriginalItemId(Long id);

    public List<SandBoxAction> getSandBoxActions();

    public void setSandBoxActions(List<SandBoxAction> actionList);

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public void addSandBoxAction(SandBoxAction action);

    public AdminAuditable getAuditable();

    public void setAuditable(AdminAuditable auditable);
}
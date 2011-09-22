package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.AdminPresentationClass;
import org.broadleafcommerce.presentation.AdminPresentationOverride;
import org.broadleafcommerce.presentation.AdminPresentationOverrides;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
@AdminPresentationOverrides(
        {@AdminPresentationOverride(name="createdBy.login", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="createdBy.password", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="createdBy.email", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="createdBy.currentSandBox", value=@AdminPresentation(excluded = true))}
)
@AdminPresentationClass(populateToOneFields = true)
public class SandBoxItemImpl implements SandBoxItem {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SandBoxItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxItemImpl", allocationSize = 50)
    @Column(name = "SANDBOX_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "SANDBOX_ID")
    @AdminPresentation(excluded = true)
	protected SandBox sandBox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIGINAL_SANDBOX_ID")
    @AdminPresentation(excluded = true)
	protected SandBox originalSandBox;

    @Column(name = "SANDBOX_ITEM_TYPE")
    @AdminPresentation(friendlyName="Item Type", order=2, group="Details", fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.openadmin.server.domain.SandBoxItemType")
    protected String sandBoxItemType;

    @Column(name = "SANDBOX_OPERATION_TYPE")
    @AdminPresentation(friendlyName="Operation Type", order=3, group="Details", fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.openadmin.server.domain.SandBoxOperationType")
    protected String sandboxOperationType;

    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Description", order=1, group="Details")
    protected String description;

    @Column(name = "LAST_UPDATE_DATE")
    @AdminPresentation(friendlyName="Last Updated", order=4, group="Details")
    protected Date lastUpdateDate;

    @ManyToOne(targetEntity = AdminUserImpl.class)
    @JoinColumn(name = "CREATED_BY_USER_ID")
    protected AdminUser createdBy;

    @Column(name = "TEMPORARY_ITEM_ID")
    @AdminPresentation(excluded = true)
    protected Long temporaryItemId;

    @Column(name = "ORIGINAL_ITEM_ID")
    @AdminPresentation(excluded = true)
    protected Long originalItemId;

    @Column(name = "ARCHIVED_FLAG")
    @AdminPresentation(excluded = true)
    protected Boolean archivedFlag = Boolean.FALSE;

    @ManyToMany(targetEntity = SandBoxActionImpl.class, cascade = CascadeType.ALL)
    @JoinTable(
        name = "SANDBOX_ITEM_ACTION",
        joinColumns = {@JoinColumn(name ="SANDBOX_ITEM_ID", referencedColumnName = "SANDBOX_ITEM_ID")},
        inverseJoinColumns = {@JoinColumn(name = "SANDBOX_ACTION_ID", referencedColumnName = "SANDBOX_ACTION_ID")}
    )
    protected List<SandBoxAction> sandBoxActions;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

    @Override
	public SandBox getSandBox() {
		return sandBox;
	}

    @Override
	public void setSandBox(SandBox sandBox) {
		this.sandBox = sandBox;
	}

    @Override
	public SandBox getOriginalSandBox() {
		return originalSandBox;
	}

    @Override
	public void setOriginalSandBox(SandBox originalSandBox) {
		this.originalSandBox = originalSandBox;
	}

    @Override
    public SandBoxItemType getSandBoxItemType() {
        return SandBoxItemType.getInstance(sandBoxItemType);
    }

   @Override
    public void setSandBoxItemType(SandBoxItemType sandBoxItemType) {
        this.sandBoxItemType = sandBoxItemType.getType();
    }

    @Override
    public SandBoxOperationType getSandBoxOperationType() {
        return SandBoxOperationType.getInstance(sandboxOperationType);
    }


    @Override
    public void setSandBoxOperationType(SandBoxOperationType sandboxOperationType) {
        this.sandboxOperationType = sandboxOperationType.getType();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public AdminUser getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(AdminUser createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getOriginalItemId() {
        return originalItemId;
    }

    @Override
    public void setOriginalItemId(Long itemIdentifer) {
        this.originalItemId = itemIdentifer;
    }

    @Override
    public Long getTemporaryItemId() {
        return temporaryItemId;
    }

    @Override
    public void setTemporaryItemId(Long temporaryItemId) {
        this.temporaryItemId = temporaryItemId;
    }

    @Override
    public List<SandBoxAction> getSandBoxActions() {
        return sandBoxActions;
    }

    @Override
    public void setSandBoxActions(List<SandBoxAction> actionList) {
        this.sandBoxActions = actionList;
    }

    @Override
    public Boolean getArchivedFlag() {
        return archivedFlag;
    }

    @Override
    public void setArchivedFlag(Boolean archivedFlag) {
        this.archivedFlag = archivedFlag;
    }

    @Override
    public void addSandBoxAction(SandBoxAction action) {
        if (sandBoxActions == null) {
            sandBoxActions = new ArrayList<SandBoxAction>();
        }
        sandBoxActions.add(action);
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SandBoxItemImpl other = (SandBoxItemImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

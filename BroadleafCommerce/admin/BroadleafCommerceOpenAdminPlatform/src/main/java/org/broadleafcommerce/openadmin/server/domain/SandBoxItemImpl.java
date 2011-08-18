package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class SandBoxItemImpl implements SandBoxItem {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SandBoxItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxItemImpl", allocationSize = 50)
    @Column(name = "SANDBOX_ITEM_ID")
    protected Long id;
    
    @ManyToOne(targetEntity = EntityImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "ENTITY_ID")
    protected Entity entity;

    @Column(name = "SANDBOX_TEMP_ITEM_ID")
    @Index(name="SNDBX_ITM_TMP_ID", columnNames={"SANDBOX_TEMP_ITEM_ID"})
    protected Long temporaryId;
    
    @ManyToOne(targetEntity = PersistencePerspectiveImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "PERSIST_PERSPECTVE_ID")
    protected PersistencePerspective persistencePerspective;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "SANDBOX_ID")
	protected SandBox sandBox;

    @Column(name = "CEILING_ENTITY")
    protected String ceilingEntityFullyQualifiedClassname;

    @Column(name = "CUST_CRITERIA")
	protected String customCriteria;

    @Column(name = "CHANGE_TYPE")
    @Index(name="SNDBX_ITM_CHG_TYPE", columnNames={"CHANGE_TYPE"})
    protected ChangeType changeType;
    
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#getEntity()
	 */
	@Override
	public Entity getEntity() {
		return entity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#setEntity(org.broadleafcommerce.openadmin.domain.Entity)
	 */
	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#getPersistencePerspective()
	 */
	@Override
	public PersistencePerspective getPersistencePerspective() {
		return persistencePerspective;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#setPersistencePerspective(org.broadleafcommerce.openadmin.domain.PersistencePerspective)
	 */
	@Override
	public void setPersistencePerspective(
			PersistencePerspective persistencePerspective) {
		this.persistencePerspective = persistencePerspective;
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
    public String getCeilingEntityFullyQualifiedClassname() {
        return ceilingEntityFullyQualifiedClassname;
    }

    @Override
    public void setCeilingEntityFullyQualifiedClassname(String ceilingEntityFullyQualifiedClassname) {
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
    }

    @Override
    public String getCustomCriteria() {
        return customCriteria;
    }

    @Override
    public void setCustomCriteria(String customCriteria) {
        this.customCriteria = customCriteria;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public Long getTemporaryId() {
        return temporaryId;
    }

    @Override
    public void setTemporaryId(Long temporaryId) {
        this.temporaryId = temporaryId;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((persistencePerspective == null) ? 0
						: persistencePerspective.hashCode());
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
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (persistencePerspective == null) {
			if (other.persistencePerspective != null)
				return false;
		} else if (!persistencePerspective.equals(other.persistencePerspective))
			return false;
		return true;
	}
    
}

package org.broadleafcommerce.openadmin.server.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    
    @ManyToOne(targetEntity = EntityImpl.class)
    @JoinColumn(name = "ENTITY_ID")
    protected Entity entity;
    
    @ManyToOne(targetEntity = PersistencePerspectiveImpl.class)
    @JoinColumn(name = "PERSIST_PERSPECTIVE_ID")
    protected PersistencePerspective persistencePerspective;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "SANDBOX_ID")
	protected SandBox sandBox;
    
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

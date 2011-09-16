package org.broadleafcommerce.openadmin.server.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class SandBoxItemImpl implements SandBoxItem {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SandBoxItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxItemImpl", allocationSize = 50)
    @Column(name = "SANDBOX_ITEM_ID")
    protected Long id;

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

    @Override
	public SandBox getSandBox() {
		return sandBox;
	}

    @Override
	public void setSandBox(SandBox sandBox) {
		this.sandBox = sandBox;
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

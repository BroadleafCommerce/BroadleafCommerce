package org.broadleafcommerce.openadmin.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_PRSPCTV_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class PersistencePerspectiveItemImpl implements PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "PersistencePerspectiveItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PersistencePerspectiveItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PersistencePerspectiveItemImpl", allocationSize = 50)
    @Column(name = "PERSIST_PERSPECT_ITEM_ID")
    protected Long id;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistencePerspectiveItemImpl other = (PersistencePerspectiveItemImpl) obj;
		
		if (id != null && other.id != null) {
            return id.equals(other.id);
        }
		
		return true;
	}

    public void accept(PersistencePerspectiveItemVisitor visitor) {
        //do nothing
    }
}

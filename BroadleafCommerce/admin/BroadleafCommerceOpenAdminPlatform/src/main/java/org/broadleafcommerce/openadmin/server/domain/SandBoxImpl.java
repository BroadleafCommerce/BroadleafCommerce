package org.broadleafcommerce.openadmin.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class SandBoxImpl implements SandBox {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SandBoxId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxImpl", allocationSize = 50)
    @Column(name = "SANDBOX_ID")
    protected Long id;
    
    @Column(name = "SANDBOX_NAME")
    @Index(name="SNDBX_NAME_INDEX", columnNames={"SANDBOX_NAME"})
    protected String name;
    
    @Column(name="AUTHOR")
    protected Long author;
    
    @OneToMany(mappedBy = "sandBox", targetEntity = SandBoxItemImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    @BatchSize(size = 50)
    protected List<SandBoxItem> sandBoxItems = new ArrayList<SandBoxItem>();

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#getSandBoxItems()
	 */
	@Override
	public List<SandBoxItem> getSandBoxItems() {
		return sandBoxItems;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.domain.SandBox#setSandBoxItems(java.util.List)
	 */
	@Override
	public void setSandBoxItems(List<SandBoxItem> sandBoxItems) {
		this.sandBoxItems = sandBoxItems;
	}

	public Long getAuthor() {
		return author;
	}

	public void setAuthor(Long author) {
		this.author = author;
	}

    @Override
    public Set<AdminRole> getAllowedRoles() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAllowedRoles(Set<AdminRole> allowedRoles) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SandBoxImpl other = (SandBoxImpl) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
    
}

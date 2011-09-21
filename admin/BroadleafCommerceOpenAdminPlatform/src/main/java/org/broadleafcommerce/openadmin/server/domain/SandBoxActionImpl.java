package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SANDBOX_ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class SandBoxActionImpl implements SandBoxAction {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SandBoxActionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SandBoxActionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxItemImpl", allocationSize = 50)
    @Column(name = "SANDBOX_ACTION_ID")
    protected Long id;

    @Column(name = "ACTION_TYPE")
    protected String sandBoxActionType;

    @Column(name = "ACTION_DATE")
    protected Date actionDate;

    @ManyToOne(targetEntity = AdminUserImpl.class)
    @JoinColumn(name = "USER_ID")
    protected AdminUser user;

    @Column(name = "COMMENT")
    protected String comment;

    @ManyToMany(targetEntity = SandBoxItemImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "SANDBOX_ITEM_ACTION",
               joinColumns = {@JoinColumn(name = "SANDBOX_ACTION_ID", referencedColumnName = "SANDBOX_ACTION_ID")},
               inverseJoinColumns = {@JoinColumn(name ="SANDBOX_ITEM_ID", referencedColumnName = "SANDBOX_ITEM_ID")})
    protected List<SandBoxItem> sandBoxItems;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public SandBoxActionType getActionType() {
        return SandBoxActionType.getInstance(sandBoxActionType);
    }

    @Override
    public void setActionType(SandBoxActionType type) {
        sandBoxActionType = type.getType();
    }

    @Override
    public Date getActionDate() {
        return actionDate;
    }

    @Override
    public void setActionDate(Date date) {
        this.actionDate = date;
    }



    @Override
    public AdminUser getUser() {
        return user;
    }

    @Override
    public void setUser(AdminUser user) {
        this.user = user;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public List<SandBoxItem> getSandBoxItems() {
        return sandBoxItems;
    }

    @Override
    public void setSandBoxItems(List<SandBoxItem> sandBoxItems) {
        this.sandBoxItems = sandBoxItems;
    }

    @Override
    public void addSandBoxItem(SandBoxItem item) {
        if (sandBoxItems == null) {
            sandBoxItems = new ArrayList<SandBoxItem>();
        }
        sandBoxItems.add(item);
    }
}

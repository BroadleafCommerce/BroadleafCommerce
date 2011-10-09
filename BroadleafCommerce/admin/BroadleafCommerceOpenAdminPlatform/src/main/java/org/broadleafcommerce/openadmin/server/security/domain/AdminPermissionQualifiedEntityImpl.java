package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/24/11
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADMIN_PERMISSION_ENTITY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class AdminPermissionQualifiedEntityImpl implements AdminPermissionQualifiedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "AdminPermissionEntityId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AdminPermissionEntityId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdminPermissionEntityImpl", allocationSize = 50)
    @Column(name = "ADMIN_PERMISSION_ENTITY_ID")
    protected Long id;

    @Column(name = "CEILING_ENTITY", nullable=false)
    @AdminPresentation(friendlyName="Ceiling Entity Name", order=1, group="Permission", prominent=true)
    protected String ceilingEntityFullyQualifiedName;

    @ManyToOne(targetEntity = AdminPermissionImpl.class)
    @JoinColumn(name = "ADMIN_PERMISSION_ID")
	protected AdminPermission adminPermission;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getCeilingEntityFullyQualifiedName() {
        return ceilingEntityFullyQualifiedName;
    }

    @Override
    public void setCeilingEntityFullyQualifiedName(String ceilingEntityFullyQualifiedName) {
        this.ceilingEntityFullyQualifiedName = ceilingEntityFullyQualifiedName;
    }

    @Override
    public AdminPermission getAdminPermission() {
        return adminPermission;
    }

    @Override
    public void setAdminPermission(AdminPermission adminPermission) {
        this.adminPermission = adminPermission;
    }
}

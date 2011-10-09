package org.broadleafcommerce.openadmin.server.security.domain;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/24/11
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminPermissionQualifiedEntity {
    Long getId();

    void setId(Long id);

    String getCeilingEntityFullyQualifiedName();

    void setCeilingEntityFullyQualifiedName(String ceilingEntityFullyQualifiedName);

    public AdminPermission getAdminPermission();

    public void setAdminPermission(AdminPermission adminPermission);
}

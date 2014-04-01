package org.broadleafcommerce.openadmin.server.service.persistence.validation;

/**
 * Marker interface for any validator that contains field names as part of its configuration. This interface is checked
 * during property building and will be used to modify the field name configuration item. Take the following example,
 * AdminUserImpl declares a password match on the field passwordConfirm. However, if the AdminUserImpl instance
 * is associated to another entity, then the password match field should actually become [another entity].[admin user field].passwordConfirm.
 *
 * @author Jeff Fischer
 */
public interface FieldNamePropertyValidator {
}

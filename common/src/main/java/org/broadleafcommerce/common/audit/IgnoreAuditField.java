package org.broadleafcommerce.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field to be ignored during standard auditing. This is useful for auditing password field changes where you
 * don't want to actually store the password change values as part of the audit.
 * </p>
 * This annotation only applies to the enterprise module audit feature. Its effects are dormant otherwise.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface IgnoreAuditField {
}

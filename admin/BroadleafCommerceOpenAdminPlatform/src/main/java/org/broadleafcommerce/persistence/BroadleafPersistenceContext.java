package org.broadleafcommerce.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.PersistenceProperty;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface BroadleafPersistenceContext {

	/**
     * (Optional) The name by which the entity manager is to be accessed in the
     * environment referencing context; not needed when dependency
     * injection is used.
     */
    String name() default "";

    /**
     * (Optional) The name of the persistence unit as defined in the
     * <code>persistence.xml</code> file. If the <code>unitName</code> element is
     * specified, the persistence unit for the entity manager that is
     * accessible in JNDI must have the same name.
     */
    String unitName() default "";
    
    /**
     * (Optional) The name of the sand box persistence unit as defined in the
     * <code>persistence.xml</code> file. If the <code>sandBoxUnitName</code> element is
     * specified, the persistence unit for the entity manager that is
     * accessible in JNDI must have the same name. Sand Boxes are a unique concept
     * to Broadleaf Commerce and represent a repository for change sets generated
     * through the Broadleaf administrative application. Sand box persistence units
     * are defined in <code>persistence.xml</code> like any other persistence unit.
     */
    String sandBoxUnitName() default "";

    /**
     * (Optional) Properties for the container or persistence
     * provider.  Vendor specific properties may be included in this
     * set of properties.  Properties that are not recognized by
     * a vendor are ignored.
     */
    PersistenceProperty[] properties() default {};
}

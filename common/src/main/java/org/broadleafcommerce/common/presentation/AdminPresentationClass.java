/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.override.AdminGroupPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminTabPresentationOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AdminPresentationClass {
    
    /**
     * <p>Specify whether or not the open admin module persistence mechanism
     * should traverse ManyToOne or OneToOne field boundaries in the entity
     * when retrieving and populating field values.</p>
     *
     * @return whether or not to populate ManyToOne or OneToOne fields
     */
    PopulateToOneFieldsEnum populateToOneFields() default PopulateToOneFieldsEnum.NOT_SPECIFIED;

    /**
     * <p>The friendly name to present to a user for this field in a GUI. If supporting i18N,
     * the friendly name may be a key to retrieve a localized friendly name using
     * the GWT support for i18N. This name will be presented to users when they add a
     * new entity in the GUI and select the polymorphic type for that new added entity.</p>
     *
     * @return the friendly name
     */
    String friendlyName() default "";

    /**
     * <p>Specify the fully qualified class name of the ceiling entity for this inheritance hierarchy. This
     * value affects the list of polymorphic types presented to the administrative user in the admin
     * UI. By specifying a class lower in the inheritance hierarchy, you can cause only a subset of
     * the entire JPA inheritance hierarchy to be presented to the user as options when creating new
     * entities. This value will override any previous settings for this inheritance hierarchy.</p>
     *
     * @return the fully qualified classname of the new top-level member of this inheritance hierarchy
     * to be displayed to the admin user
     */
    String ceilingDisplayEntity() default "";

    /**
     * <p>Specify whether or not this class should be excluded from admin detection as a polymorphic type.
     * This is useful if you have several entities that implement an interface, but you only want the
     * admin to ignore one of the entities as a valid type for the interface.</p>
     *
     * @return Whether or not the admin should ignore this entity as a valid polymorphic type
     */
    boolean excludeFromPolymorphism() default false;

    /**
     * These AdminTabPresentation items define each tab that will be displayed in the entity's EntityForm.
     *
     * @return the tabs for the entity's EntityForm
     */
    AdminTabPresentation[] tabs() default {};

    /**
     * These AdminTabPresentationOverride items override a superclass' tab information by targeting the
     * superclass' tab and the property to be overridden
     *
     * @return the tab overrides for the entity's EntityForm
     */
    AdminTabPresentationOverride[] tabOverrides() default {};

    /**
     * These AdminGroupPresentationOverride items override a superclass' group information by targeting the
     * superclass' tab, group, and the property to be overridden
     *
     * @return the group overrides for the entity's EntityForm
     */
    AdminGroupPresentationOverride[] groupOverrides() default {};

}

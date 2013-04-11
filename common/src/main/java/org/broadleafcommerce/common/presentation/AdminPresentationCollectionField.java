package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.CustomFieldSearchableTypes;

/**
 * This annotation is used to describe a member of a Collection structure that should be
 * displayed as a regular field in the admin tool. The members of the collection must
 * implement the <tt>ValueAssignable</tt> interface.
 *
 * @author Jeff Fischer
 */
public @interface AdminPresentationCollectionField {

    /**
     * <p>Represents the field name for this field.
     *
     * @return the name and friendly name for this field
     */
    String fieldName();

    /**
     * <p>Represents the metadata for this field. The <tt>AdminPresentation</tt> properties will be used
     * by the system to determine how this field should be treated in the admin tool (e.g. date fields get
     * a date picker in the UI)</p>
     *
     * @return the descriptive metadata for this field
     */
    AdminPresentation fieldPresentation();

    /**
     * <p>Optional - if the Collection structure is using generics, then the system can usually infer the concrete
     * type for the Collection value. However, if not using generics for the Collection, or if the value cannot be clearly
     * inferred, you can explicitly set the Collection structure value type here.</p>
     *
     * @return the concrete type for the Collection structure value
     */
    Class<?> targetClass() default Void.class;

    /**
     * <p>Optional - if the collection field value contains searchable information and should be included in Broadleaf
     * search engine indexing and searching. If set, the collection member class must implement the <tt>Searchable</tt> interface.
     * Note, support for indexing and searching this field must be explicitly added to the Broadleaf search service
     * as well.</p>
     *
     * @return Whether or not this field is searchable with the Broadleaf search engine
     */
    CustomFieldSearchableTypes searchable() default CustomFieldSearchableTypes.NOT_SPECIFIED;

    /**
     * <p>Optional - if the collection member is not primitive and contains a bi-directional reference back to the containing entity,
     * you can declare here the field name of the collection member class for this reference. Note, if the collection
     * uses the JPA mappedBy property, the system will try to infer the manyToField value so you don't have to set
     * it here.</p>
     *
     * @return the parent entity referring field name
     */
    String manyToField() default "";
}

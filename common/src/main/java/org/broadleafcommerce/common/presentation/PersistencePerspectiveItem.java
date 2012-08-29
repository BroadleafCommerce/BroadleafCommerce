package org.broadleafcommerce.common.presentation;

/**
 * @author Jeff Fischer
 */
public @interface PersistencePerspectiveItem {

    /**
     * For the target entity of this collection, specify the field
     * name that refers back to the parent entity.
     *
     * For collection definitions that use the "mappedBy" property
     * of the @OneToMany and @ManyToMany annotations, this value
     * can be safely ignored as the system will be able to infer
     * the proper value from this.
     *
     * @return the parent entity referring field name
     */
    String entity_manyToField() default "";

    /**
     * For the target entity of this collection, specify the field
     * name of the field that will provide the display value in
     * the admin gui.
     *
     * Only required if the display value field is called
     * something other than "name"
     *
     * @return the display value field name for the collection entity
     */
    String entity_displayValueProperty() default "name";
}

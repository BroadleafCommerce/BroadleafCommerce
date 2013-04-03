package org.broadleafcommerce.common.presentation;

/**
 * @author Jeff Fischer
 */
public @interface AdminPresentationMapField {

    AdminPresentationMapKey fieldName() default @AdminPresentationMapKey(friendlyKeyName = "", keyName = "");

    AdminPresentation fieldPresentation() default @AdminPresentation();

    Class<?> targetClass() default Void.class;

    //TODO support map fields with complex types and data driven enumerations. The complex type support should include either create or lookup
    //TODO add another property to contain the fully qualified type of the toOneLookup, if it can't be inferred from the map generic type
    /*AdminPresentationToOneLookup toOneLookup() default @AdminPresentationToOneLookup();

    AdminPresentationDataDrivenEnumeration dataDrivenEnumeration() default @AdminPresentationDataDrivenEnumeration();

    *//**
     * <p>Optional - only applies when the map value for this map field is a complex value (i.e. entity) and
     * you would like the field to appear as a toOneLookup in the admin.</p>
     *
     * @return whether or not to treat this map field value as a toOneLookup field in the admin
     *//*
    boolean useToOneLookupConfig() default false;

    *//**
     * <p>Optional - only applies when the map value for this field is a simple value (i.e. string) and
     * you would like the selection of values in the admin limited to an enumeration of values.</p>
     *
     * @return whether or not to treat this map field value as a strict enumeration of values in the admin
     *//*
    boolean useDataDrivenEnumerationConfig() default false;*/

}

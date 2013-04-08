package com.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

/**
 * Represents a custom field that may be introduced by an admin user in the admin tool. Custom fields
 * are maintained in an attributes map on the target entity.
 *
 * @author Jeff Fischer
 */
public interface CustomField extends Serializable {

    /**
     * The fully qualified class name of the entity whose attribute map should contain this custom field.
     *
     * @return the fully qualified class name of the entity
     */
    String getCustomFieldTarget();

    /**
     * The fully qualified class name of the entity whose attribute map should contain this custom field.
     *
     * @param customFieldTarget the fully qualified class name of the entity
     */
    void setCustomFieldTarget(String customFieldTarget);

    /**
     * The type for this field (e.g. Boolean, Date, ...). This drives the picker and validation in the admin.
     *
     * @return The type for this field
     */
    String getCustomFieldType();

    /**
     * The type for this field (e.g. Boolean, Date, ...). This drives the picker and validation in the admin.
     *
     * @param customFieldType The type for this field
     */
    void setCustomFieldType(String customFieldType);

    /**
     * The order in which this field will appear in the main listgrid for the target entity in the admin.
     * May be null, in which case the field will not appear in the main grid.
     *
     * @return the order in which this field appears in the list grid.
     */
    Integer getGridOrder();

    /**
     * The order in which this field will appear in the main listgrid for the target entity in the admin.
     * May be null, in which case the field will not appear in the main grid.
     *
     * @param gridOrder the order in which this field appears in the list grid.
     */
    void setGridOrder(Integer gridOrder);

    /**
     * The group in the target entity in which this field will reside. Grouping effects visual organization of fields
     * in the admin tool form. May be null, in which case the field will be in the default, general group.
     *
     * @return The group in the target entity in which this field will reside.
     */
    String getGroupName();

    /**
     * The group in the target entity in which this field will reside. Grouping effects visual organization of fields
     * in the admin tool form. May be null, in which case the field will be in the default, general group.
     *
     * @param groupName The group in the target entity in which this field will reside.
     */
    void setGroupName(String groupName);

    /**
     * The primary key value for the custom field.
     *
     * @return the primary key value
     */
    Long getId();

    /**
     * The primary key value for the custom field.
     *
     * @param id the primary key value
     */
    void setId(Long id);

    /**
     * The field label that will be for the target entity attribute map key. Will also be used to derive
     * the label shown to the admin user for the field (using i18n).
     *
     * @return the label for this field
     */
    String getLabel();

    /**
     * The field label that will be for the target entity attribute map key. Will also be used to derive
     * the label shown to the admin user for the field (using i18n).
     *
     * @param label
     */
    void setLabel(String label);

    /**
     * Whether or not this field is available to rule builders based on the target entity.
     *
     * @return whether or not this field appears in the rule builder
     */
    Boolean getShowFieldInRuleBuilder();

    /**
     * Whether or not this field is available to rule builders based on the target entity.
     *
     * @param showFieldInRuleBuilder whether or not this field appears in the rule builder
     */
    void setShowFieldInRuleBuilder(Boolean showFieldInRuleBuilder);

    /**
     * Whether or not this field is visible on the target entity form.
     *
     * @return whether or not this field appears on the target entity form.
     */
    Boolean getShowFieldOnForm();

    /**
     * Whether or not this field is visible on the target entity form.
     *
     * @param showFieldOnForm whether or not this field appears on the target entity form.
     */
    void setShowFieldOnForm(Boolean showFieldOnForm);
}

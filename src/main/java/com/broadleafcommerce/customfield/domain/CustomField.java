/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */

package com.broadleafcommerce.customfield.domain;

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
     * Optional - The order for this field in the form
     *
     * @return the order of the field in the form
     */
    Integer getFieldOrder();

    /**
     * Optional - the order for this field in the form
     *
     * @param fieldOrder the order of the field in the form
     */
    void setFieldOrder(Integer fieldOrder);

    /**
     * Optional - The group in the target entity in which this field will reside. Grouping effects visual organization of fields
     * in the admin tool form. May be null, in which case the field will be in the default, general group.
     *
     * @return The group in the target entity in which this field will reside.
     */
    String getGroupName();

    /**
     * Optional - The group in the target entity in which this field will reside. Grouping effects visual organization of fields
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
     * The field attribute name that will be for the target entity attribute map key.
     *
     * @return the attribute name for this field
     */
    String getAttributeName();

    /**
     * The field attributeName that will be for the target entity attribute map key.
     *
     * @param attributeName the attributeName for this field
     */
    void setAttributeName(String attributeName);

    /**
     * The name that will be displayed to the user in the target form and rule builder. Will also be used to derive
     * the label shown to the admin user for the field (using i18n).
     *
     * @return The display name for the custom field
     */
    String getFriendlyName();

    /**
     * The name that will be displayed to the user in the target form and rule builder. Will also be used to derive
     * the label shown to the admin user for the field (using i18n).
     *
     * @param friendlyName The display name for the custom field
     */
    void setFriendlyName(String friendlyName);

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
     * @return whether or not this field appears on the target entity form
     */
    Boolean getShowFieldOnForm();

    /**
     * Whether or not this field is visible on the target entity form.
     *
     * @param showFieldOnForm whether or not this field appears on the target entity form
     */
    void setShowFieldOnForm(Boolean showFieldOnForm);

    /**
     * Optional - Whether or not the field is included in search engine indexing and is including in keyword searches
     * performed on the site. May not apply to every custom attribute field (e.g. attributes on Customer are
     * not searchable).
     *
     * @return Whether or not the field is available for search engine usage
     */
    Boolean getSearchable();

    /**
     * Optional - Whether or not the field is included in search engine indexing and is including in keyword searches
     * performed on the site. May not apply to every custom attribute field (e.g. attributes on Customer are
     * not searchable).
     *
     * @param searchable Whether or not the field is available for search engine usage
     */
    void setSearchable(Boolean searchable);
}

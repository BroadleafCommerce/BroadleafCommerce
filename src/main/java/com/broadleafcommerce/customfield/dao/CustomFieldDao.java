/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package com.broadleafcommerce.customfield.dao;

import com.broadleafcommerce.customfield.domain.CustomField;

import java.util.List;

/**
 * Data Access Object for retrieving information regarding custom fields.
 *
 * @see CustomField
 * @author Jeff Fischer
 */
public interface CustomFieldDao {

    /**
     * Get the custom field via its primary key value
     *
     * @param id the primary key value
     * @return the custom field instance
     */
    CustomField retrieveById(Long id);

    /**
     * Get a list of custom fields via the target entity name. The target entity is the fully
     * qualified class name of the entity type that contains the attribute map for list of returned
     * custom fields.
     *
     * @param targetEntityName the entity type that contains the attribute map for the custom fields.
     * @return the custom field instances that are related to the target entity type.
     */
    List<CustomField> retrieveByTargetEntityName(String targetEntityName);
}

/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.handler;

import java.io.Serializable;
import java.util.List;

import org.broadleafcommerce.openadmin.dto.Entity;

/**
 * @author Jeff Fischer
 */
public interface DynamicEntityRetriever {

    Entity fetchDynamicEntity(Serializable root, List<String> dirtyFields, boolean includeId) throws Exception;

}

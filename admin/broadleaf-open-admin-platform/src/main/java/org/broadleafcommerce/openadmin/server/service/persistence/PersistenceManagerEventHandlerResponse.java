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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class PersistenceManagerEventHandlerResponse {

    protected Entity entity;
    protected DynamicResultSet dynamicResultSet;
    protected PersistenceManagerEventHandlerResponseStatus status;
    protected Map<String, Object> additionalData = new HashMap<String, Object>();

    public enum PersistenceManagerEventHandlerResponseStatus {
        HANDLED,NOT_HANDLED,HANDLED_BREAK
    }

    public PersistenceManagerEventHandlerResponse withEntity(Entity entity) {
        setEntity(entity);
        return this;
    }

    public PersistenceManagerEventHandlerResponse withStatus(PersistenceManagerEventHandlerResponseStatus status) {
        setStatus(status);
        return this;
    }

    public PersistenceManagerEventHandlerResponse withDynamicResultSet(DynamicResultSet dynamicResultSet) {
        setDynamicResultSet(dynamicResultSet);
        return this;
    }

    public PersistenceManagerEventHandlerResponse withAdditionalData(Map<String, Object> additionalData) {
        setAdditionalData(additionalData);
        return this;
    }

    public DynamicResultSet getDynamicResultSet() {
        return dynamicResultSet;
    }

    public void setDynamicResultSet(DynamicResultSet dynamicResultSet) {
        this.dynamicResultSet = dynamicResultSet;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public PersistenceManagerEventHandlerResponseStatus getStatus() {
        return status;
    }

    public void setStatus(PersistenceManagerEventHandlerResponseStatus status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
}

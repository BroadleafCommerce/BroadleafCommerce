/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.purge;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Specification that defines a hierarchy of tables for Purge processing.  The PurgeSpec defines the table(s), 
 * primary keys, and the foreign keys used to link the tables.
 * 
 * The specification is designed using a perspective from a root table and traversing into related tables.  It is
 * important that the table references are defined in traversal order so the process knows which tables to purge
 * first in order that foreign key violations can be avoided.  As the hierarchy is defined, the root  
 * table can change if there are groupings of related tables relative to the new root.  Once that cluster of
 * tables are defined the root can return to the previous root table.
 * 
 * Key methods used to define the hierarchy:
 * addTableRefFromRoot - adds a table name, pkColumn name, and fkColumn name that is fk referenced "from" the root table
 * addTableRefToRoot   - adds a table name, pkColumn name, and fkColumn name that is fk referenced "to" the root table
 * newRoot - changes the root to the next defined TableRef
 * endRoot - returns the root to the previous table
 * 
 * This class is designed to use method chaining.  For example:
 * 
 * <code>
 * customerPurgeSpec = new PurgeSpec("blc_customer", "customer_id")
 *     .addTableRefToRoot("blc_customer_attribute", "customer_attribute_id", "customer_id")
 *     .newRoot()
 *         .addTableRefToRoot("blc_customer_address", "customer_address_id", "customer_id")
 *         .addTableRefFromRoot("blc_address", "address_id", "address_id")
 *     .endRoot()
 *     .addTableRefToRoot("blc_customer_role", "customer_role_id", "customer_id");
 * </code>
 * 
 * @author dcolgrove
 *
 */
public class PurgeSpecification {

    private Stack<PurgeSpecification> specStack = new Stack<PurgeSpecification>();
    private boolean pushToStack = false;
    private List<PurgeSpecification> fkToRoot = new ArrayList<PurgeSpecification>();
    private List<PurgeSpecification> fkFromRoot = new ArrayList<PurgeSpecification>();
    protected String tableName;
    protected String idColumnName;
    protected String fkColumnName;

    public PurgeSpecification(String tableName, String idColumnName) {
        this(tableName, idColumnName, true);
    }

    public PurgeSpecification(String tableName, String idColumnName, boolean isRoot) {
        this.tableName = tableName;
        this.idColumnName = idColumnName;
        specStack.push(this);
    }
    
    private PurgeSpecification(String tableName, String idColumnName, Stack<PurgeSpecification> specStack) {
        this(tableName, idColumnName, false);
        this.specStack = specStack;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getIdColumnName() {
        return idColumnName;
    }
    
    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    public PurgeSpecification addTableRefFromRoot(String tableName, String idColumn, String fkColumn) {
        PurgeSpecification spec = new PurgeSpecification(tableName, idColumn, this.specStack);
        spec.setFkColumnName(fkColumn);
        this.fkFromRoot.add(spec);
        if (pushToStack) {
            specStack.push(spec);
            pushToStack = false;
        }
        return specStack.lastElement();
    }
    
    public List<PurgeSpecification> getTableRefsFromRoot() {
        return fkFromRoot;
    }
    
    public PurgeSpecification addTableRefToRoot(String tableName, String idColumn, String fkColumn) {
        PurgeSpecification spec = new PurgeSpecification(tableName, idColumn, this.specStack);
        spec.setFkColumnName(fkColumn);
        this.fkToRoot.add(spec);
        if (pushToStack) {
            specStack.push(spec);
            pushToStack = false;
        }        
        return specStack.lastElement();
    }
    
    public List<PurgeSpecification> getTableRefsToRoot() {
        return fkToRoot;
    }

    
    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }
    
    public PurgeSpecification newRoot() {
        this.pushToStack = true;
        return specStack.lastElement();
    }
    
    public PurgeSpecification endRoot() {
        specStack.pop();
        return specStack.lastElement();
    }
}

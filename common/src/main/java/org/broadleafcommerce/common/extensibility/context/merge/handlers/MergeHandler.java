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
package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import org.w3c.dom.Node;

import java.util.List;

/**
 * All handlers must implement the MergeHandler interface. It defines
 * the key properties and actions a MergeHandler must perform. MergeHandlers
 * perform the actual merge of data from the patch document to the source
 * document based on the business rules of the implementation.
 * 
 * @author jfischer
 *
 */
public interface MergeHandler {
    
    /**
     * Perform the merge using the supplied list of nodes from the source and
     * patch documents, respectively. Also, a list of nodes that have already
     * been merged is provided and may be used by the implementation when
     * necessary.
     * 
     * @param nodeList1 list of nodes to be merged from the source document
     * @param nodeList2 list of nodes to be merged form the patch document
     * @param exhaustedNodes already merged nodes
     * @return list of merged nodes
     */
    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes);
    
    /**
     * Retrieve the priority for the handler. Priorities are used by the MergeManager
     * to establish the order of operations for performing merges.
     * 
     * @return the priority value
     */
    public int getPriority();
    
    /**
     * Set the priority for this handler
     * @param priority
     */
    public void setPriority(int priority);
    
    /**
     * Retrieve the XPath query associated with this handler. XPath is used by the handler
     * to define to section of the source and patch documents that will be merged.
     * 
     * @return the xpath query
     */
    public String getXPath();
    
    /**
     * Set the xpath query
     * 
     * @param xpath
     */
    public void setXPath(String xpath);
    
    /**
     * Retrieve any child merge handlers associated with this handler. Child merge handlers
     * may be added alter merge behavior for a subsection of the merge area defined
     * by this merge handler.
     * 
     * @return child merge handlers
     */
    public MergeHandler[] getChildren();
    
    /**
     * Set the child merge handlers
     * 
     * @param children
     */
    public void setChildren(MergeHandler[] children);
    
    /**
     * Retrieve the name associated with this merge handlers. Merge handler names are
     * period-delimited numeric strings that define the hierarchical relationship of mergehandlers
     * and their children. For example, "2" could be used to define the second handler in the configuration
     * list and "2.1" would be the name describing the first child handler of "2".
     * 
     * @return the period-delimited numeric string that names this handler
     */
    public String getName();
    
    /**
     * Set the period-delimited numeric string that names this handler
     * 
     * @param name
     */
    public void setName(String name);

}

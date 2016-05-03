/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
 * This handler is responsible for replacing nodes in the source document
 * with the same nodes from the patch document. This handler will replace
 * all nodes with the same name entirely, regardless of differences in
 * attributes.
 * 
 * @author jfischer
 *
 */
public class NodeReplace extends NodeReplaceInsert {

    @Override
    protected boolean checkNode(List<Node> usedNodes, Node[] primaryNodes, Node node) {
        if (replaceNode(primaryNodes, node, usedNodes)) {
            return true;
        }
        //check if this same node already exists
        if (exactNodeExists(primaryNodes, node, usedNodes)) {
            return true;
        }
        return false;
    }

    protected boolean replaceNode(Node[] primaryNodes, Node testNode, List<Node> usedNodes) {
        boolean foundItem = false;
        for (int j=0;j<primaryNodes.length;j++){
            if (primaryNodes[j].getNodeName().equals(testNode.getNodeName())) {
                Node newNode = primaryNodes[j].getOwnerDocument().importNode(testNode.cloneNode(true), true);
                primaryNodes[j].getParentNode().replaceChild(newNode, primaryNodes[j]);
                usedNodes.add(testNode);
                foundItem = true;
            }
        }

        return foundItem;
    }
}

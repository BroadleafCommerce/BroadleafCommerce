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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This handler is responsible for replacing nodes in the source document
  * with the same nodes from the patch document. Note, additional nodes
  * from the patch document that are not present in the source document
  * are simply appended to the source document. This handler differs from its
 * parent in that it will not replace an existing node with a node from
 * the patch document if that patch node contains no child nodes.
 *
 * @author Jeff Fischer
 */
public class NonEmptyNodeReplaceInsert extends NodeReplaceInsert {

    @Override
    protected boolean replaceNode(Node[] primaryNodes, Node testNode, final String attribute, List<Node> usedNodes) {
        if (testNode.getAttributes().getNamedItem(attribute) == null) {
            return false;
        }
        //filter out primary nodes that don't have the attribute
        ArrayList<Node> filterList = new ArrayList<Node>();
        for (int j=0;j<primaryNodes.length;j++){
            if (primaryNodes[j].getAttributes().getNamedItem(attribute) != null) {
                filterList.add(primaryNodes[j]);
            }
        }
        Node[] filtered = {};
        filtered = filterList.toArray(filtered);

        Comparator<Node> idCompare = new Comparator<Node>() {
            public int compare(Node arg0, Node arg1) {
                Node id1 = arg0.getAttributes().getNamedItem(attribute);
                Node id2 = arg1.getAttributes().getNamedItem(attribute);
                String idVal1 = id1.getNodeValue();
                String idVal2 = id2.getNodeValue();
                return idVal1.compareTo(idVal2);
            }
        };
        Arrays.sort(filtered, idCompare);
        int pos = Arrays.binarySearch(filtered, testNode, idCompare);
        if (pos >= 0) {
            evaluate:{
                if (!testNode.hasChildNodes()) {
                    break evaluate;
                }
                Node newNode = filtered[pos].getOwnerDocument().importNode(testNode.cloneNode(true), true);
                filtered[pos].getParentNode().replaceChild(newNode, filtered[pos]);
            }
            usedNodes.add(testNode);
            return true;
        }
        return false;
    }
}

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This handler implementation provides behavior where a list of elements from the
 * patch document are appended to the same parent element in the source document.
 * 
 * @author jfischer
 *
 */
public class InsertItems extends BaseHandler {

    private static final Log LOG = LogFactory.getLog(InsertItems.class);

    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes) {
        if (CollectionUtils.isEmpty(nodeList1) || CollectionUtils.isEmpty(nodeList2)) {
            return null;
        }
        List<Node> usedNodes = new ArrayList<Node>();
        Node node1Parent = nodeList1.get(0).getParentNode();
        for (Node aNodeList2 : nodeList2) {
            Node tempNode = node1Parent.getOwnerDocument().importNode(aNodeList2.cloneNode(true), true);
            if (LOG.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("matching node for insertion: ");
                sb.append(tempNode.getNodeName());
                int attrLength = tempNode.getAttributes().getLength();
                for (int x = 0; x < attrLength; x++) {
                    sb.append(" : (");
                    sb.append(tempNode.getAttributes().item(x).getNodeName());
                    sb.append("/");
                    sb.append(tempNode.getAttributes().item(x).getNodeValue());
                    sb.append(")");
                }
                LOG.debug(sb.toString());
            }
            if (LOG.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("inserting into parent: ");
                sb.append(node1Parent.getNodeName());
                int attrLength = node1Parent.getAttributes().getLength();
                for (int x = 0; x < attrLength; x++) {
                    sb.append(" : (");
                    sb.append(node1Parent.getAttributes().item(x).getNodeName());
                    sb.append("/");
                    sb.append(node1Parent.getAttributes().item(x).getNodeValue());
                    sb.append(")");
                }
                LOG.debug(sb.toString());
            }
            node1Parent.appendChild(tempNode);
            usedNodes.add(tempNode);
        }

        Node[] response = {nodeList2.get(0).getParentNode()};
        return response;
    }

}

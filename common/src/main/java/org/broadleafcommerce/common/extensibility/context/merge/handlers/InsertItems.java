/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

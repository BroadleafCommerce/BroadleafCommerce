/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    public Node[] merge(NodeList nodeList1, NodeList nodeList2, List<Node> exhaustedNodes) {
        if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
            return null;
        }
        List<Node> usedNodes = new ArrayList<Node>();
        Node node1Parent = nodeList1.item(0).getParentNode();
        for (int j=0;j<nodeList2.getLength();j++) {
        	Node tempNode = node1Parent.getOwnerDocument().importNode(nodeList2.item(j).cloneNode(true), true);
            if(LOG.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("matching node for insertion: ");
                sb.append(tempNode.getNodeName());
                int attrLength = tempNode.getAttributes().getLength();
                for (int x=0;x<attrLength;x++){
                    sb.append(" : (");
                    sb.append(tempNode.getAttributes().item(x).getNodeName());
                    sb.append("/");
                    sb.append(tempNode.getAttributes().item(x).getNodeValue());
                    sb.append(")");
                }
                LOG.debug(sb.toString());
            }
            if(LOG.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("inserting into parent: ");
                sb.append(node1Parent.getNodeName());
                int attrLength = node1Parent.getAttributes().getLength();
                for (int x=0;x<attrLength;x++){
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

        Node[] response = {nodeList2.item(0).getParentNode()};
        return response;
    }

}

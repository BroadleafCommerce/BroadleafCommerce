/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This handler is responsible for replacing nodes in the source document
 * with the same nodes from the patch document. Note, additional nodes
 * from the patch document that are not present in the source document
 * are simply appended to the source document.
 * 
 * @author jfischer
 *
 */
public class NodeReplaceInsert extends BaseHandler {
    
    private static final Log LOG = LogFactory.getLog(NodeReplaceInsert.class);

    private static final Comparator<Node> NODE_COMPARATOR = new Comparator<Node>() {
        public int compare(Node arg0, Node arg1) {
            int response = -1;
            if (arg0.isSameNode(arg1)) {
                response = 0;
            }
            //determine if the element is an ancestor
            if (response != 0) {
                boolean eof = false;
                Node parentNode = arg0;
                while (!eof) {
                    parentNode = parentNode.getParentNode();
                    if (parentNode == null) {
                        eof = true;
                    } else if (arg1.isSameNode(parentNode)) {
                        response = 0;
                        eof = true;
                    }
                }
            }
            return response;
        }
    };

    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes) {
        if (CollectionUtils.isEmpty(nodeList1) || CollectionUtils.isEmpty(nodeList2)) {
            return null;
        }
        Node[] primaryNodes = new Node[nodeList1.size()];
        for (int j=0;j<primaryNodes.length;j++){
            primaryNodes[j] = nodeList1.get(j);
        }

        ArrayList<Node> list = new ArrayList<Node>();
        for (int j=0;j<nodeList2.size();j++){
            list.add(nodeList2.get(j));
        }

        List<Node> usedNodes = matchNodes(exhaustedNodes, primaryNodes, list);

        Node[] response = {};
        response = usedNodes.toArray(response);
        return response;
    }

    private boolean exhaustedNodesContains(List<Node> exhaustedNodes, Node node) {
        boolean contains = false;
        for (Node exhaustedNode : exhaustedNodes) {
            if (NODE_COMPARATOR.compare(exhaustedNode, node) == 0) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    private List<Node> matchNodes(List<Node> exhaustedNodes, Node[] primaryNodes, ArrayList<Node> list) {
        List<Node> usedNodes = new ArrayList<Node>(20);
        Iterator<Node> itr = list.iterator();
        Node parentNode = primaryNodes[0].getParentNode();
        Document ownerDocument = parentNode.getOwnerDocument();
        while(itr.hasNext()) {
            Node node = itr.next();
            if (Element.class.isAssignableFrom(node.getClass()) && !exhaustedNodesContains(exhaustedNodes, node)) {
                
                if(LOG.isDebugEnabled()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("matching node for replacement: ");
                    sb.append(node.getNodeName());
                    int attrLength = node.getAttributes().getLength();
                    for (int j=0;j<attrLength;j++){
                        sb.append(" : (");
                        sb.append(node.getAttributes().item(j).getNodeName());
                        sb.append("/");
                        sb.append(node.getAttributes().item(j).getNodeValue());
                        sb.append(")");
                    }
                    LOG.debug(sb.toString());
                }
                if (!checkNode(usedNodes, primaryNodes, node)) {
                    //simply append the node if all the above fails
                    Node newNode = ownerDocument.importNode(node.cloneNode(true), true);
                    parentNode.appendChild(newNode);
                    usedNodes.add(node);
                }
            }
        }
        return usedNodes;
    }
    
    protected boolean checkNode(List<Node> usedNodes, Node[] primaryNodes, Node node) {
        //find matching nodes based on id
        if (replaceNode(primaryNodes, node, "id", usedNodes)) {
            return true;
        }
        //find matching nodes based on name
        if (replaceNode(primaryNodes, node, "name", usedNodes)) {
            return true;
        }
        //check if this same node already exists
        if (exactNodeExists(primaryNodes, node, usedNodes)) {
            return true;
        }
        return false;
    }

    protected boolean exactNodeExists(Node[] primaryNodes, Node testNode, List<Node> usedNodes) {
        for (int j=0;j<primaryNodes.length;j++){
            if (primaryNodes[j].isEqualNode(testNode)) {
                usedNodes.add(primaryNodes[j]);
                return true;
            }
        }
        return false;
    }

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
            Node newNode = filtered[pos].getOwnerDocument().importNode(testNode.cloneNode(true), true);
            filtered[pos].getParentNode().replaceChild(newNode, filtered[pos]);
            usedNodes.add(testNode);
            return true;
        }
        return false;
    }

}

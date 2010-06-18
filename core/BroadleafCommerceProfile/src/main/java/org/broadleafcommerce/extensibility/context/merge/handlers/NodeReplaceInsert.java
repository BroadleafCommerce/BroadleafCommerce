/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.extensibility.context.merge.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    public Node[] merge(NodeList nodeList1, NodeList nodeList2, List<Node> exhaustedNodes) {
        if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
            return null;
        }
        Node[] primaryNodes = new Node[nodeList1.getLength()];
        for (int j=0;j<primaryNodes.length;j++){
            primaryNodes[j] = nodeList1.item(j);
        }

        ArrayList<Node> list = new ArrayList<Node>();
        for (int j=0;j<nodeList2.getLength();j++){
            list.add(nodeList2.item(j));
        }

        List<Node> usedNodes = matchNodes(exhaustedNodes, primaryNodes, list);

        Node[] response = {};
        response = usedNodes.toArray(response);
        return response;
    }

    private List<Node> matchNodes(List<Node> exhaustedNodes, Node[] primaryNodes, ArrayList<Node> list) {
        Comparator<Node> hashCompare = new Comparator<Node>() {
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
        Node[] tempNodes = {};
        tempNodes = exhaustedNodes.toArray(tempNodes);
        Arrays.sort(tempNodes, hashCompare);

        List<Node> usedNodes = new ArrayList<Node>();

        Iterator<Node> itr = list.iterator();
        Node parentNode = primaryNodes[0].getParentNode();
        Document ownerDocument = parentNode.getOwnerDocument();
        while(itr.hasNext()) {
            Node node = itr.next();
            if (Element.class.isAssignableFrom(node.getClass()) && Arrays.binarySearch(tempNodes, node, hashCompare) < 0) {
            	
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

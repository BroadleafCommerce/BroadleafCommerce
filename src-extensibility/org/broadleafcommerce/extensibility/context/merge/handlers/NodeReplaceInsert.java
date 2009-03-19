package org.broadleafcommerce.extensibility.context.merge.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#merge(org.w3c.dom.NodeList, org.w3c.dom.NodeList, org.w3c.dom.Node[])
	 */
	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
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

	private List<Node> matchNodes(Node[] exhaustedNodes, Node[] primaryNodes, ArrayList<Node> list) {
		Comparator<Node> hashCompare = new Comparator<Node>() {
			@Override
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
		Arrays.sort(exhaustedNodes, hashCompare);
		
		List<Node> usedNodes = new ArrayList<Node>();
		
		Iterator<Node> itr = list.iterator();
		while(itr.hasNext()) {
			Node node = itr.next();
			if (Element.class.isAssignableFrom(node.getClass()) && Arrays.binarySearch(exhaustedNodes, node, hashCompare) < 0) {
				//find matching nodes based on id
				if (replaceNode(primaryNodes, node, "id")) {
					usedNodes.add(node);
					continue;
				}
				//find matching nodes based on name
				if (replaceNode(primaryNodes, node, "name")) {
					usedNodes.add(node);
					continue;
				}
				//check if this same node already exists
				if (exactNodeExists(primaryNodes, node)) {
					usedNodes.add(node);
					continue;
				}
				//simply append the node if all the above fails
				primaryNodes[0].getParentNode().appendChild(primaryNodes[0].getOwnerDocument().importNode(node.cloneNode(true), true));
				usedNodes.add(node);
			}
		}
		return usedNodes;
	}
	
	private boolean exactNodeExists(Node[] primaryNodes, Node testNode) {
		for (int j=0;j<primaryNodes.length;j++){
			if (primaryNodes[j].isEqualNode(testNode)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean replaceNode(Node[] primaryNodes, Node testNode, final String attribute) {
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
			@Override
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
			filtered[pos].getParentNode().replaceChild(filtered[pos].getOwnerDocument().importNode(testNode.cloneNode(true), true), filtered[pos]);
			return true;
		}
		return false;
	}

}

package org.broadleafcommerce.extensibility.context.merge.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Replace extends BaseHandler {

	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
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
			/*String primaryName = primaryNodes[j].getNodeName();
			String testName = testNode.getNodeName();
			if (primaryName.equals(testName)) {
				NamedNodeMap primaryMap = primaryNodes[j].getAttributes();
				NamedNodeMap testMap = testNode.getAttributes();
				int primaryLength = primaryMap.getLength();
				int testLength = testMap.getLength();
				if (primaryLength == testLength) {
					for (int x=0;x<primaryLength;x++){
						Attr primaryAttr = (Attr) primaryMap.item(x);
						String key = primaryAttr.getName();
						String value = primaryAttr.getValue();
						Attr testAttr = (Attr) testMap.getNamedItem(key);
						if (testAttr == null) {
							return false;
						}
						if (!value.equals(testAttr.getValue())) {
							return false;
						}
					}
					return true;
				}
			}*/
		}
		return false;
	}
	
	private boolean replaceNode(Node[] primaryNodes, Node testNode, final String attribute) {
		Comparator<Node> idCompare = new Comparator<Node>() {
			@Override
			public int compare(Node arg0, Node arg1) {
				Node id1 = arg0.getAttributes().getNamedItem(attribute);
				Node id2 = arg1.getAttributes().getNamedItem(attribute);
				if (id1 == null || id2 == null) return -1;
				return id1.getNodeValue().compareTo(id2.getNodeValue());
			} 
		};
		Arrays.sort(primaryNodes, idCompare);
		int pos = Arrays.binarySearch(primaryNodes, testNode, idCompare);
		if (pos >= 0) {
			primaryNodes[pos].getParentNode().replaceChild(primaryNodes[pos].getOwnerDocument().importNode(testNode.cloneNode(true), true), primaryNodes[pos]);
			return true;
		}
		return false;
	}

}

package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Insert extends BaseHandler {

	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
		Node node1 = nodeList1.item(0);
		Node node2 = nodeList2.item(0);
		NodeList list2 = node2.getChildNodes();
		for (int j=0;j<list2.getLength();j++) {
			node1.appendChild(node1.getOwnerDocument().importNode(list2.item(j).cloneNode(true), true));
		}
		
		Node[] response = new Node[nodeList2.getLength()];
		for (int j=0;j<response.length;j++){
			response[j] = nodeList2.item(j);
		}
		return response;
	}

}

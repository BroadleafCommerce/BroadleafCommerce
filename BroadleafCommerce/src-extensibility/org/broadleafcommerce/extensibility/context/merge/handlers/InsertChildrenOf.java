package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This handler implementation provides behavior where the child nodes from
 * an element in the patch document are added to the same node in the source
 * document.
 * 
 * @author jfischer
 *
 */
public class InsertChildrenOf extends BaseHandler {

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#merge(org.w3c.dom.NodeList, org.w3c.dom.NodeList, org.w3c.dom.Node[])
	 */
	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
		if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
			return null;
		}
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

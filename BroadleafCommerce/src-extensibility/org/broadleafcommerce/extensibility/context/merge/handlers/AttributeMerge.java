package org.broadleafcommerce.extensibility.context.merge.handlers;

import java.util.Arrays;
import java.util.Comparator;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttributeMerge extends BaseHandler {
	
	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
		Node node1 = nodeList1.item(0);
		Node node2 = nodeList2.item(0);
		NamedNodeMap attributes2 = node2.getAttributes();
		
		Comparator<Object> nameCompare = new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return ((Node) arg0).getNodeName().compareTo(((Node) arg1).getNodeName());
			}
    	};
    	
		Arrays.sort(exhaustedNodes, nameCompare);
		int length = attributes2.getLength();
		for (int j=0;j<length;j++){
			Node temp = attributes2.item(j);
			int pos = Arrays.binarySearch(exhaustedNodes, temp, nameCompare);
			if (pos < 0) {
				((Element) node1).setAttributeNode((Attr) node1.getOwnerDocument().importNode(temp.cloneNode(true), true));
			}
		}
		
		return null;
	}
	
}

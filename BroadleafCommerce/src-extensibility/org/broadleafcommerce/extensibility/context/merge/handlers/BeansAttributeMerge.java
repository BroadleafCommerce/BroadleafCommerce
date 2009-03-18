package org.broadleafcommerce.extensibility.context.merge.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.broadleafcommerce.extensibility.context.merge.MergeManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BeansAttributeMerge extends BaseHandler {
	
	@Override
	public void merge(Node node1, Node node2) {
		NamedNodeMap attributes1 = node1.getAttributes();
		NamedNodeMap attributes2 = node2.getAttributes();
		
		Document doc = (Document) MergeManager.getCurrentDocument();
		
		Element beans = doc.createElement("beans");
		Node schemaLocation = null;
		ArrayList<Node> allAttributes = new ArrayList<Node>();
		int length = attributes1.getLength();
		for (int j=0;j<length;j++) {
			Node temp = attributes1.item(j);
			if (temp.getNodeName().contains("schemaLocation")) {
				schemaLocation = temp.cloneNode(true);
			} else {
				allAttributes.add(temp.cloneNode(true));
			}
		}
		
		length = attributes2.getLength();
		for (int j=0;j<length;j++){
			Node temp = attributes2.item(j);
			if (temp.getNodeName().contains("schemaLocation") && schemaLocation != null) {
				schemaLocation = mergeSchemaLocations(schemaLocation, temp);
			} else {
				allAttributes.add(temp.cloneNode(true));
			}
		}
		allAttributes.add(schemaLocation);
		Iterator<Node> itr = allAttributes.iterator();
		while(itr.hasNext()) {
			beans.setAttributeNode((Attr) doc.importNode(itr.next(), true)); 
		}
		
		doc.appendChild(beans);
	}

	private Node mergeSchemaLocations(Node location1, Node location2) {
		String[] items1 = location1.getNodeValue().split("[\\s\\n\\r]+");
		String[] items2 = location2.getNodeValue().split("[\\s\\n\\r]+");
		ArrayList<String> finalItems = new ArrayList<String>();
		for (int j=0;j<items1.length;j++){
			finalItems.add(items1[j]);
		}
		for (int j=0;j<items2.length;j++){
			finalItems.add(items2[j]);
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> itr = finalItems.iterator();
		while (itr.hasNext()) {
			sb.append(itr.next());
			if (itr.hasNext()) {
				sb.append(" ");
			}
		}
		location1.setNodeValue(sb.toString());
		
		return location1;
	}

	@Override
	public String getXPath() {
		return "/beans";
	}
	
}

package org.broadleafcommerce.extensibility.context.merge;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MergePoint {
	
	private MergeHandler handler;
	private Document doc1;
	private Document doc2;
	private XPath xPath;
	
	public MergePoint(MergeHandler handler, Document doc1, Document doc2) {
		this.handler = handler;
		this.doc1 = doc1;
		this.doc2 = doc2;
		XPathFactory factory=XPathFactory.newInstance();
		xPath=factory.newXPath();
	}
	
	public Node[] merge(Node[] exhaustedNodes) throws XPathExpressionException {
		return merge(handler, exhaustedNodes);
	}
	
	private Node[] merge(MergeHandler handler, Node[] exhaustedNodes) throws XPathExpressionException {
		if (handler.getChildren() != null) {
			MergeHandler[] children = handler.getChildren();
			for (int j=0;j<children.length;j++){
				Node[] temp = merge(children[j], exhaustedNodes);
				if (temp != null) {
					Node[] newExhausted = new Node[exhaustedNodes.length + temp.length];
					System.arraycopy(exhaustedNodes, 0, newExhausted, 0, exhaustedNodes.length);
					System.arraycopy(temp, 0, newExhausted, exhaustedNodes.length, temp.length);
					exhaustedNodes = newExhausted;
				}
			}
		}
		NodeList nodeList1 = (NodeList) xPath.evaluate(handler.getXPath(), doc1, XPathConstants.NODESET);
		NodeList nodeList2 = (NodeList) xPath.evaluate(handler.getXPath(), doc2, XPathConstants.NODESET);
		if (nodeList1 != null && nodeList2 != null) {
			return handler.merge(nodeList1, nodeList2, exhaustedNodes);
		}
		return null;
	}
}

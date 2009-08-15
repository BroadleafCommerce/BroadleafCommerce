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
package org.broadleafcommerce.extensibility.context.merge;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides the xml merging apparatus at a defined XPath merge point in 
 * 2 xml documents. The MergeHandler that embodies the XPath point can have
 * embedded XPath merge points, resulting in a cumulative effect with varying
 * merge behavior for a sector of the documents. For example, it may be desirable
 * to replace all the child nodes of a given node with all the child nodes from the same
 * parent node in the patch document, with the exception of a single node. That single
 * node may instead contribute its contents in a additive fashion (rather than replace).
 * 
 * @author jfischer
 *
 */
public class MergePoint {
	
	private static final Log LOG = LogFactory.getLog(MergePoint.class);
	
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
	
	/**
	 * Execute the merge operation and also provide a list of nodes that have already been
	 * merged. It is up to the handler implementation to respect or ignore this list.
	 * 
	 * @param exhaustedNodes
	 * @return list of merged nodes
	 * @throws XPathExpressionException
	 */
	public Node[] merge(List<Node> exhaustedNodes) throws XPathExpressionException {
		return merge(handler, exhaustedNodes);
	}
	
	private Node[] merge(MergeHandler handler, List<Node> exhaustedNodes) throws XPathExpressionException {
		if (LOG.isDebugEnabled()) {
    		LOG.debug("Processing handler: " + handler.getXPath());
    	}
		if (handler.getChildren() != null) {
			MergeHandler[] children = handler.getChildren();
			for (int j=0;j<children.length;j++){
				Node[] temp = merge(children[j], exhaustedNodes);
				if (temp != null) {
					for (Node node : temp) {
						exhaustedNodes.add(node);
					}
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

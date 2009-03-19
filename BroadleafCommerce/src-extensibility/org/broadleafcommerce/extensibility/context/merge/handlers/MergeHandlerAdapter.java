package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MergeHandlerAdapter implements MergeHandler {

	@Override
	public MergeHandler[] getChildren() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public String getXPath() {
		return null;
	}

	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2,
			Node[] exhaustedNodes) {
		return null;
	}

	@Override
	public void setChildren(MergeHandler[] children) {
		
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public void setPriority(int priority) {

	}

	@Override
	public void setXPath(String xpath) {

	}

}

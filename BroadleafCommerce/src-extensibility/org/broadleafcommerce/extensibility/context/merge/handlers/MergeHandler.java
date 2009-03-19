package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface MergeHandler {
	
	public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes);
	
	public int getPriority();
	
	public void setPriority(int priority);
	
	public String getXPath();
	
	public void setXPath(String xpath);
	
	public MergeHandler[] getChildren();
	
	public void setChildren(MergeHandler[] children);
	
	public String getName();
	
	public void setName(String name);

}

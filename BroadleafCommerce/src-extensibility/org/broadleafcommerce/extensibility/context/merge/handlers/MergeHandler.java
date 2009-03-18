package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;

public interface MergeHandler {
	
	public void merge(Node node1, Node node2);
	
	public int getPriority();
	
	public void setPriority(int priority);
	
	public String getXPath();
	
	public void setXPath(String xpath);
	
	public MergeHandler[] getChildren();
	
	public void setChildren(MergeHandler[] children);
	
	public String getName();
	
	public void setName(String name);

}

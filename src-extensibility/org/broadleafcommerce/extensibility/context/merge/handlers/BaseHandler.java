package org.broadleafcommerce.extensibility.context.merge.handlers;


public abstract class BaseHandler implements MergeHandler, Comparable<Object> {

	protected int priority;
	protected String xpath;
	protected MergeHandler[] children;
	protected String name;

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String getXPath() {
		return xpath;
	}
	
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public void setXPath(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public int compareTo(Object arg0) {
		return new Integer(getPriority()).compareTo(new Integer(((MergeHandler) arg0).getPriority()));
	}

	@Override
	public MergeHandler[] getChildren() {
		return children;
	}

	@Override
	public void setChildren(MergeHandler[] children) {
		this.children = children;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}

package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This adapter class allows the developer to create a merge handler
 * instance and only override a subset of the functionality, instead of
 * having to provide an independent, full implementation of the MergeHandler
 * interface.
 * 
 * @author jfischer
 *
 */
public class MergeHandlerAdapter implements MergeHandler {

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#getChildren()
	 */
	@Override
	public MergeHandler[] getChildren() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#getPriority()
	 */
	@Override
	public int getPriority() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#getXPath()
	 */
	@Override
	public String getXPath() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#merge(org.w3c.dom.NodeList, org.w3c.dom.NodeList, org.w3c.dom.Node[])
	 */
	@Override
	public Node[] merge(NodeList nodeList1, NodeList nodeList2,
			Node[] exhaustedNodes) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#setChildren(org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler[])
	 */
	@Override
	public void setChildren(MergeHandler[] children) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#setPriority(int)
	 */
	@Override
	public void setPriority(int priority) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#setXPath(java.lang.String)
	 */
	@Override
	public void setXPath(String xpath) {
		//do nothing
	}

}

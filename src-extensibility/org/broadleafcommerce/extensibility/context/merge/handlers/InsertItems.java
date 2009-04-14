package org.broadleafcommerce.extensibility.context.merge.handlers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This handler implementation provides behavior where a list of elements from the
 * patch document are appended to the same parent element in the source document.
 * 
 * @author jfischer
 *
 */
public class InsertItems extends BaseHandler {

    /* (non-Javadoc)
     * @see org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler#merge(org.w3c.dom.NodeList, org.w3c.dom.NodeList, org.w3c.dom.Node[])
     */
    @Override
    public Node[] merge(NodeList nodeList1, NodeList nodeList2, Node[] exhaustedNodes) {
        if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
            return null;
        }
        Node node1Parent = nodeList1.item(0).getParentNode();
        for (int j=0;j<nodeList2.getLength();j++) {
            node1Parent.appendChild(node1Parent.getOwnerDocument().importNode(nodeList2.item(j).cloneNode(true), true));
        }

        Node[] response = {nodeList2.item(0).getParentNode()};
        return response;
    }

}

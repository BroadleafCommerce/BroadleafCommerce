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

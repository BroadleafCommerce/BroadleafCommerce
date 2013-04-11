/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * This handler implementation provides behavior where the child nodes from an
 * element in the patch document are added to the same node in the source
 * document.
 * @author jfischer
 */
public class InsertChildrenOf extends BaseHandler {

    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes) {
        if (CollectionUtils.isEmpty(nodeList1) || CollectionUtils.isEmpty(nodeList2)) {
            return null;
        }
        Node node1 = nodeList1.get(0);
        Node node2 = nodeList2.get(0);
        NodeList list2 = node2.getChildNodes();
        for (int j = 0; j < list2.getLength(); j++) {
            node1.appendChild(node1.getOwnerDocument().importNode(list2.item(j).cloneNode(true), true));
        }

        Node[] response = new Node[nodeList2.size()];
        for (int j = 0; j < response.length; j++) {
            response[j] = nodeList2.get(j);
        }
        return response;
    }

}

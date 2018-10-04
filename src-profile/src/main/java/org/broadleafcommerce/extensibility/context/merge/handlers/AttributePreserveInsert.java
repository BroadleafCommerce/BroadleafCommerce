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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Merge the attributes of a source and patch node, only adding attributes from
 * the patch side. When the same attribute is encountered in the source and
 * patch children list, the source attribute is left untouched.
 * @author jfischer
 */
public class AttributePreserveInsert extends BaseHandler {

    public Node[] merge(NodeList nodeList1, NodeList nodeList2, List<Node> exhaustedNodes) {
        if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
            return null;
        }
        Node node1 = nodeList1.item(0);
        Node node2 = nodeList2.item(0);
        NamedNodeMap attributes2 = node2.getAttributes();

        Comparator<Object> nameCompare = new Comparator<Object>() {
            public int compare(Object arg0, Object arg1) {
                return ((Node) arg0).getNodeName().compareTo(((Node) arg1).getNodeName());
            }
        };
        Node[] tempNodes = {};
        tempNodes = exhaustedNodes.toArray(tempNodes);
        Arrays.sort(tempNodes, nameCompare);
        int length = attributes2.getLength();
        for (int j = 0; j < length; j++) {
            Node temp = attributes2.item(j);
            int pos = Arrays.binarySearch(tempNodes, temp, nameCompare);
            if (pos < 0) {
                ((Element) node1).setAttributeNode((Attr) node1.getOwnerDocument().importNode(temp.cloneNode(true), true));
            }
        }

        return null;
    }

}

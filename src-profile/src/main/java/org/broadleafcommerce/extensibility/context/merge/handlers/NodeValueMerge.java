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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MergeHandler implementation that provides merging for the white space
 * delimited text values of a source and patch node. This merge takes into
 * account the same values from both nodes, such that the resulting string
 * is a union of the two without any repeat values.
 * 
 * @author jfischer
 *
 */
public class NodeValueMerge extends BaseHandler {

    public Node[] merge(NodeList nodeList1, NodeList nodeList2, List<Node> exhaustedNodes) {
        if (nodeList1 == null || nodeList2 == null || nodeList1.getLength() == 0 || nodeList2.getLength() == 0) {
            return null;
        }
        Node node1 = nodeList1.item(0);
        Node node2 = nodeList2.item(0);
        String[] items1 = node1.getNodeValue().split("[\\s\\n\\r]+");
        String[] items2 = node2.getNodeValue().split("[\\s\\n\\r]+");
        ArrayList<String> finalItems = new ArrayList<String>();
        for (int j=0;j<items1.length;j++){
            finalItems.add(items1[j]);
        }
        for (int j=0;j<items2.length;j++){
            finalItems.add(items2[j]);
        }
        StringBuffer sb = new StringBuffer();
        Iterator<String> itr = finalItems.iterator();
        while (itr.hasNext()) {
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(" ");
            }
        }
        node1.setNodeValue(sb.toString());

        Node[] response = new Node[nodeList2.getLength()];
        for (int j=0;j<response.length;j++){
            response[j] = nodeList2.item(j);
        }
        return response;
    }

}

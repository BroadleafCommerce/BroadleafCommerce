/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.context.merge.handlers;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    protected String delimiter = " ";
    protected String regex = "[\\s\\n\\r]+";

    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes) {
        if (CollectionUtils.isEmpty(nodeList1) || CollectionUtils.isEmpty(nodeList2)) {
            return null;
        }
        Node node1 = nodeList1.get(0);
        Node node2 = nodeList2.get(0);
        String[] items1 = node1.getNodeValue().split(getRegEx());
        String[] items2 = node2.getNodeValue().split(getRegEx());
        Set<String> finalItems = new LinkedHashSet<String>();
        for (String anItems1 : items1) {
            finalItems.add(anItems1.trim());
        }
        for (String anItems2 : items2) {
            finalItems.add(anItems2.trim());
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = finalItems.iterator();
        while (itr.hasNext()) {
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(getDelimiter());
            }
        }
        node1.setNodeValue(sb.toString());
        node2.setNodeValue(sb.toString());

        Node[] response = new Node[nodeList2.size()];
        for (int j=0;j<response.length;j++){
            response[j] = nodeList2.get(j);
        }
        return response;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getRegEx() {
        return regex;
    }
}

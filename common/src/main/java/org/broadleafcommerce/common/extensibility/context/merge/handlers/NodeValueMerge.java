/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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

    @Override
    public Node[] merge(List<Node> nodeList1, List<Node> nodeList2, List<Node> exhaustedNodes) {
        if (CollectionUtils.isEmpty(nodeList1) || CollectionUtils.isEmpty(nodeList2)) {
            return null;
        }
        Node node1 = nodeList1.get(0);
        Node node2 = nodeList2.get(0);
        Set<String> finalItems = getMergedNodeValues(node1, node2);
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

    protected Set<String> getMergedNodeValues(Node node1, Node node2) {
        String[] items1 = node1.getNodeValue().split(getRegEx());
        String[] items2 = node2.getNodeValue().split(getRegEx());
        Set<String> finalItems = new LinkedHashSet<String>();
        for (String anItems1 : items1) {
            finalItems.add(anItems1.trim());
        }
        for (String anItems2 : items2) {
            finalItems.add(anItems2.trim());
        }
        return finalItems;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getRegEx() {
        return regex;
    }
}

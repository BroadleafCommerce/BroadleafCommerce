/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

package org.broadleafcommerce.common.util;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * An API for w3c.Nodes manipulation
 * @author gdiaz
 *
 */
public class NodeUtil {

    static Logger LOG = Logger.getLogger(NodeUtil.class.getName());

    /**
     * a simple implementation of the Comparator interface, (applied to the Node class) that uses the value of a given 
     * node attribute as comparison criterion. 
     * Nodes not having the required attribute (or not having attributes at all) bypass this comparator, i.e., they are considered arbitrarily different
     * as far as this comparator is concerned.
     * 
     * 
     * @author gdiaz
     *
     */
    public static class NodeComparatorBySingleAttribute implements Comparator<Node> {

        /**
         * the name of the unique attribute whose value will be compared
         */
        private String attributeName;

        public NodeComparatorBySingleAttribute(String attributeName) {
            this.attributeName = attributeName;
        }

        @Override
        public int compare(Node o1, Node o2) {
            NamedNodeMap attributes1 = o1.getAttributes();
            NamedNodeMap attributes2 = o2.getAttributes();
            if (attributes1 == null || attributes2 == null) {
                return -1;
            }

            Node id1 = attributes1.getNamedItem(attributeName);
            Node id2 = attributes2.getNamedItem(attributeName);
            if (id1 == null || id2 == null) {
                return -1;
            }
            String idVal1 = id1.getNodeValue();
            String idVal2 = id2.getNodeValue();
            return idVal1.compareTo(idVal2);
        }
    }

    /**
     * tries to find a test Node within an array of nodes
     * The array is assumed sorted according to a custom comparator by single attribute, but if can be optionally sorted inside the method
     * @param arrNodes   the haystack
     * @param testNode   the needle
     * @param attribute  the attribute used for comparison
     * @param sortArray  true if the array needs to be sorted, false if it comes already sorted
     * @return
     */
    public static int findNode(Node[] arrNodes, Node testNode, String attributeName, boolean sortArray) {

        NodeComparatorBySingleAttribute comparator = new NodeComparatorBySingleAttribute(attributeName);
        if (sortArray) {
            Arrays.sort(arrNodes, comparator);
        }
        int position = Arrays.binarySearch(arrNodes, testNode, comparator);
        return position;
    }

    /**
     * creates a sorted list of nodes, with the merged nodes of 2 NodeLists
     * The comparison criteria is a single-attribute comparator, whose attribute name is also given as a parameter
     * The original NodeLists are not modified. They can be null. They are not assumed to be sorted.
     * @param Node   the target node (assumed childless, and within the same document) to which the merged children will be appended 
     * @param list1
     * @param list2
     * @param attribute
     */
    public static void mergeNodeLists(Node targetNode, org.w3c.dom.NodeList list1, org.w3c.dom.NodeList list2, String attributeName) {
        NodeComparatorBySingleAttribute comparator = new NodeComparatorBySingleAttribute(attributeName);
        TreeSet<Node> resultSet = new TreeSet<Node>(comparator);
        if (list1 != null) {
            for (int i = 0; i < list1.getLength(); i++) {
                resultSet.add(list1.item(i));
            }
        }
        if (list2 != null) {
            for (int i = 0; i < list2.getLength(); i++) {
                resultSet.add(list2.item(i));
            }
        }
        for (Node node : resultSet) {
            targetNode.appendChild(node);
        }
    }

}

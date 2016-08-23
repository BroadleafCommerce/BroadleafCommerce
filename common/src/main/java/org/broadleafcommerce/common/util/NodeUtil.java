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

package org.broadleafcommerce.common.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * An API for w3c.Nodes manipulation
 * @author gdiaz
 *
 */
public class NodeUtil {

    private static final String TEXT_ELEMENT_NAME = "#text";
    private static final String MO_GROUP = "mo:group";
    private static final String GROUP_NAME = "groupName";
    private static final String MO_TAB = "mo:tab";
    private static final String TAB_NAME = "tabName";

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
            if (attributes1 == null) {
                return 1;
            } else if (attributes2 == null) {
                return -1;
            } else if (MO_TAB.equals(o1.getNodeName()) && !MO_TAB.equals(o2.getNodeName())) {
                return -1;
            } else if (!MO_TAB.equals(o1.getNodeName()) && MO_TAB.equals(o2.getNodeName())) {
                return 1;
            } else if (MO_GROUP.equals(o1.getNodeName()) && !MO_GROUP.equals(o2.getNodeName())) {
                return -1;
            } else if (!MO_GROUP.equals(o1.getNodeName()) && MO_GROUP.equals(o2.getNodeName())) {
                return 1;
            }

            Node id1, id2;
            if (MO_TAB.equals(o1.getNodeName()) && MO_TAB.equals(o2.getNodeName())) {
                id1 = attributes1.getNamedItem(TAB_NAME);
                id2 = attributes2.getNamedItem(TAB_NAME);
            } else if (MO_GROUP.equals(o1.getNodeName()) && MO_GROUP.equals(o2.getNodeName())) {
                id1 = attributes1.getNamedItem(GROUP_NAME);
                id2 = attributes2.getNamedItem(GROUP_NAME);
            } else {
                id1 = attributes1.getNamedItem(attributeName);
                id2 = attributes2.getNamedItem(attributeName);
            }

            if (id1 == null || id2 == null) {
                return -1;
            }
            String idVal1 = id1.getNodeValue();
            String idVal2 = id2.getNodeValue();
            return idVal1.compareTo(idVal2);
        }
    }

    /**
     * given an array of nodes, returns a subarray containing only those nodes having a non-null specified attribute
     * @param primaryNodes     the original array of nodes. All nodes are assumed to at least have attributes
     * @param attributeName    the attribute name
     * @return
     */
    public static Node[] filterByAttribute(Node[] primaryNodes, String attributeName) {
        //filter out primary nodes that don't have the attribute
        ArrayList<Node> filterList = new ArrayList<Node>();
        for (int j = 0; j < primaryNodes.length; j++) {
            if (primaryNodes[j].getAttributes().getNamedItem(attributeName) != null) {
                filterList.add(primaryNodes[j]);
            }
        }
        Node[] filtered = filterList.toArray(new Node[] {});
        return filtered;
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
     * @param list1 the original list to merge
     * @param list2 the second list to merge which will overwrite values from <b>list1</b>
     * @param attribute
     */
    public static void mergeNodeLists(Node targetNode, org.w3c.dom.NodeList list1, org.w3c.dom.NodeList list2, String attributeName) {
        NodeComparatorBySingleAttribute comparator = new NodeComparatorBySingleAttribute(attributeName);
        TreeSet<Node> resultSet = new TreeSet<Node>(comparator);
        if (list1 != null) {
            for (int i = 0; i < list1.getLength(); i++) {
                if (!TEXT_ELEMENT_NAME.equals(list1.item(i).getNodeName())) {
                    resultSet.add(list1.item(i));
                }
            }
        }
        if (list2 != null) {
            for (int i = 0; i < list2.getLength(); i++) {
                if (!TEXT_ELEMENT_NAME.equals(list2.item(i).getNodeName())) {
                    resultSet.add(list2.item(i));
                }
            }
        }
        for (Node node : resultSet) {
            targetNode.appendChild(node);
        }
    }

}

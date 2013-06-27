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

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This handler is responsible for replacing nodes in the source document
  * with the same nodes from the patch document. Note, additional nodes
  * from the patch document that are not present in the source document
  * are simply appended to the source document. This handler differs from its
 * parent in that it will not replace an existing node with a node from
 * the patch document if that patch node contains no child nodes.
 *
 * @author Jeff Fischer
 */
public class NonEmptyNodeReplaceInsert extends NodeReplaceInsert {

    @Override
    protected boolean replaceNode(Node[] primaryNodes, Node testNode, final String attribute, List<Node> usedNodes) {
        if (testNode.getAttributes().getNamedItem(attribute) == null) {
            return false;
        }
        //filter out primary nodes that don't have the attribute
        ArrayList<Node> filterList = new ArrayList<Node>();
        for (int j=0;j<primaryNodes.length;j++){
            if (primaryNodes[j].getAttributes().getNamedItem(attribute) != null) {
                filterList.add(primaryNodes[j]);
            }
        }
        Node[] filtered = {};
        filtered = filterList.toArray(filtered);

        Comparator<Node> idCompare = new Comparator<Node>() {
            public int compare(Node arg0, Node arg1) {
                Node id1 = arg0.getAttributes().getNamedItem(attribute);
                Node id2 = arg1.getAttributes().getNamedItem(attribute);
                String idVal1 = id1.getNodeValue();
                String idVal2 = id2.getNodeValue();
                return idVal1.compareTo(idVal2);
            }
        };
        Arrays.sort(filtered, idCompare);
        int pos = Arrays.binarySearch(filtered, testNode, idCompare);
        if (pos >= 0) {
            evaluate:{
                if (!testNode.hasChildNodes()) {
                    break evaluate;
                }
                Node newNode = filtered[pos].getOwnerDocument().importNode(testNode.cloneNode(true), true);
                filtered[pos].getParentNode().replaceChild(newNode, filtered[pos]);
            }
            usedNodes.add(testNode);
            return true;
        }
        return false;
    }
}

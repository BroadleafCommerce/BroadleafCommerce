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

import java.util.List;

/**
 * This handler is responsible for replacing nodes in the source document
 * with the same nodes from the patch document. This handler will replace
 * all nodes with the same name entirely, regardless of differences in
 * attributes.
 * 
 * @author jfischer
 *
 */
public class NodeReplace extends NodeReplaceInsert {

    @Override
    protected boolean checkNode(List<Node> usedNodes, Node[] primaryNodes, Node node) {
        if (replaceNode(primaryNodes, node, usedNodes)) {
            return true;
        }
        //check if this same node already exists
        if (exactNodeExists(primaryNodes, node, usedNodes)) {
            return true;
        }
        return false;
    }

    protected boolean replaceNode(Node[] primaryNodes, Node testNode, List<Node> usedNodes) {
        boolean foundItem = false;
        for (int j=0;j<primaryNodes.length;j++){
            if (primaryNodes[j].getNodeName().equals(testNode.getNodeName())) {
                Node newNode = primaryNodes[j].getOwnerDocument().importNode(testNode.cloneNode(true), true);
                primaryNodes[j].getParentNode().replaceChild(newNode, primaryNodes[j]);
                usedNodes.add(testNode);
                foundItem = true;
            }
        }

        return foundItem;
    }
}

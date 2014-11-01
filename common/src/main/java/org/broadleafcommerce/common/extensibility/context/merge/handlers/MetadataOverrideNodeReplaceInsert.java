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

import org.w3c.dom.Node;

import java.util.List;

/**
 * This handler is responsible for replacing nodes in the source document
 * with the same nodes from the patch document. Note, additional nodes
 * from the patch document that are not present in the source document
 * are simply appended to the source document. This is specialized for the
 * mo:overrideItem element.
 * 
 * @author jfischer
 *
 */
public class MetadataOverrideNodeReplaceInsert extends NodeReplaceInsert {

    protected boolean checkNode(List<Node> usedNodes, Node[] primaryNodes, Node node) {
        //find matching nodes based on id
        if (replaceNode(primaryNodes, node, "configurationKey", usedNodes)) {
            return true;
        }
        //find matching nodes based on name
        if (replaceNode(primaryNodes, node, "ceilingEntity", usedNodes)) {
            return true;
        }
        //check if this same node already exists
        if (exactNodeExists(primaryNodes, node, usedNodes)) {
            return true;
        }
        return false;
    }

}

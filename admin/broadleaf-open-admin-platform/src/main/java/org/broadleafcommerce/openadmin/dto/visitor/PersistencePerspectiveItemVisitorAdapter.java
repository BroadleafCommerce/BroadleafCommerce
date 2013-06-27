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

package org.broadleafcommerce.openadmin.dto.visitor;

import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.SimpleValueMapStructure;


public class PersistencePerspectiveItemVisitorAdapter implements PersistencePerspectiveItemVisitor {

    @Override
    public void visit(AdornedTargetList adornedTargetList) {
        //do nothing
    }

    @Override
    public void visit(MapStructure mapStructure) {
        //do nothing
    }

    @Override
    public void visit(SimpleValueMapStructure simpleValueMapStructure) {
        //do nothing
    }

    @Override
    public void visit(ForeignKey foreignKey) {
        //do nothing
    }
}

/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.Hilite;
import com.smartgwt.client.widgets.DataBoundComponent;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class PresentationLayerAssociatedDataSource extends DynamicEntityDataSource {

    public static Hilite[] hilites = new Hilite[] {
        new Hilite() {{
            setCssText("font-style: italic;");
            setId("listGridInActivePropertyHilite");
        }},
        new Hilite() {{
            setCssText("text-decoration: line-through;");
            setId("listGridDeletedPropertyHilite");
        }},
        new Hilite() {{
            setCssText("font-weight: bold;");
            setId("listGridDirtyPropertyHilite");
        }},
        new Hilite() {{
            setCssText("color: #968e9a;");
            setId("listGridLockedPropertyHilite");
        }}
    };
    
    protected DataBoundComponent associatedGrid;
    
    /**
     * @param name
     * @param persistencePerspective
     * @param service
     * @param modules
     */
    public PresentationLayerAssociatedDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    public DataBoundComponent getAssociatedGrid() {
        return associatedGrid;
    }

    public void setAssociatedGrid(DataBoundComponent associatedGrid) {
        this.associatedGrid = associatedGrid;
    }

    public void loadAssociatedGridBasedOnRelationship(String relationshipValue, DSCallback dsCallback) {
        Criteria criteria = createRelationshipCriteria(relationshipValue);
        if (dsCallback != null) {
            getAssociatedGrid().fetchData(criteria, dsCallback);
        } else {
            getAssociatedGrid().fetchData(criteria);
        }
    }
}

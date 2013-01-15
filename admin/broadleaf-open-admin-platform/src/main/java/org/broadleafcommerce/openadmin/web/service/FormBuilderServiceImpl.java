/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCArrayUtils;
import org.broadleafcommerce.common.util.TypedPredicate;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.form.EntityForm;
import org.broadleafcommerce.openadmin.web.form.Field;
import org.broadleafcommerce.openadmin.web.form.HeaderColumn;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blFormBuilderService")
public class FormBuilderServiceImpl implements FormBuilderService {

    @Override
    public ListGrid getListGrid(ClassMetadata metadata, Entity[] entities) {
        List<HeaderColumn> hcs = new ArrayList<HeaderColumn>();

        for (Property p : metadata.getProperties()) {
            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata fieldMetadata = (BasicFieldMetadata) p.getMetadata();
                if (fieldMetadata.isProminent() != null && fieldMetadata.isProminent()) {
                    HeaderColumn hc = (HeaderColumn) new HeaderColumn()
                            .setName(p.getName())
                            .setFriendlyName(p.getMetadata().getFriendlyName());
                    hcs.add(hc);
                }
            }
        }

        ListGrid lg = new ListGrid();

        lg.setMetadata(metadata);
        lg.setEntities(Arrays.asList(entities));
        lg.setHeaderColumns(hcs);

        return lg;
    }

    @Override
    public EntityForm getEntityForm(ClassMetadata cmd, Entity e) {
        EntityForm ef = new EntityForm(e);

        ef.setMetadata(cmd);

        for (final Property p : cmd.getProperties()) {
            // We have all polymorphic types here. Filter out ones that do not apply
            boolean entityHasProperty = BLCArrayUtils.contains(e.getProperties(), new TypedPredicate<Property>() {
                @Override
                public boolean evaluate(Property entityProperty) {
                    return p.getName().equals(entityProperty.getName());
                }
            });

            if (!entityHasProperty) {
                continue;
            }

            if (p.getMetadata() instanceof BasicFieldMetadata) {
                BasicFieldMetadata metadata = (BasicFieldMetadata) p.getMetadata();

                String fieldType = metadata.getFieldType() == null ? null : metadata.getFieldType().toString();
                
                if (fieldType.equalsIgnoreCase("FOREIGN_KEY")) {
                    continue;
                }
                
                if (!fieldType.equalsIgnoreCase("BOOLEAN") && !fieldType.equalsIgnoreCase("ADDITIONAL_FOREIGN_KEY")) {
                    fieldType = "TEXT";
                }

                Field f = new Field()
                        .setName(p.getName())
                        .setFriendlyName(p.getMetadata().getFriendlyName())
                        .setFieldType(fieldType);

                if (StringUtils.isBlank(f.getFriendlyName())) {
                    f.setFriendlyName(f.getName());
                }

                String groupName = ((BasicFieldMetadata) p.getMetadata()).getGroup();
                groupName = groupName == null ? "Default" : groupName;

                List<Field> fs = ef.getGroupedFields().get(groupName);
                if (fs == null) {
                    fs = new ArrayList<Field>();
                    ef.getGroupedFields().put(groupName, fs);
                }

                fs.add(f);
            } else if (p.getMetadata() instanceof MapMetadata) {
                //TODO: Add implementation
                ef.getNonBasicFields().add(p);
            } else if (p.getMetadata() instanceof AdornedTargetCollectionMetadata) {
                //TODO: Add implementation
                ef.getNonBasicFields().add(p);
            } else if (p.getMetadata() instanceof BasicCollectionMetadata) {
                //TODO: Add implementation
                ef.getNonBasicFields().add(p);
            } else {
                //TODO: Add implementation
            }
        }

        return ef;
    }

}

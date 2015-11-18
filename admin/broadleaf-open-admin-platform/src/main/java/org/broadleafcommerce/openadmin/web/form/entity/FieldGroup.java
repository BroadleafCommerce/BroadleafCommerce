/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FieldGroup {

    protected String title;
    protected Integer order;
    protected Set<Field> alternateOrderedFields = new HashSet<Field>();
    protected Set<Field> fields = new HashSet<Field>();
    protected Boolean isVisible;

    public Boolean getIsVisible() {
        if (isVisible != null) {
            return isVisible;
        }
        for (Field f : getFields()) {
            if (f.getIsVisible()) {
                return true;
            }
        }
        return false;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    
    public FieldGroup withTitle(String title) {
        setTitle(title);
        return this;
    }
    
    public FieldGroup withOrder(Integer order) {
        setOrder(order);
        return this;
    }

    public boolean addField(Field field) {
        if (field.getAlternateOrdering()) {
            return alternateOrderedFields.add(field);
        } else {
            return fields.add(field);
        }
    }

    public boolean removeField(Field field) {
        if (field.getAlternateOrdering()) {
            return alternateOrderedFields.remove(field);
        } else {
            return fields.remove(field);
        }
    }

    public Set<Field> getFields() {
        List<Field> myFields = new ArrayList<Field>();
        myFields.addAll(fields);
        Collections.sort(myFields, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getFriendlyName(), o2.getFriendlyName())
                    .append(o1.getName(), o2.getName())
                    .toComparison();
            }
        });
        if (!alternateOrderedFields.isEmpty()) {
            List<Field> mapFieldsList = new ArrayList<Field>(alternateOrderedFields);
            Collections.sort(mapFieldsList, new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    return new CompareToBuilder()
                        .append(o1.getOrder(), o2.getOrder())
                        .append(o1.getFriendlyName(), o2.getFriendlyName())
                        .append(o1.getName(), o2.getName())
                        .toComparison();
                }
            });
            /*
            alternate ordered fields whose order is less or equal to zero appear first and are
            prepended to the response list in order
             */
            List<Field> smallOrderFields = new ArrayList<Field>();
            for (Field mapField : mapFieldsList) {
                if (mapField.getOrder() <= 0) {
                    smallOrderFields.add(mapField);
                }
            }
            myFields.addAll(0, smallOrderFields);
            /*
            Alternate ordered fields (specifically custom fields) have a different ordering rule than regular fields. For example,
            if a user enters 3 for the field order value for a custom field, that custom field should be the third
            on the form. Regular BLC AdminPresentation fields tends to have orders like 1000, 2000, etc..., so this
            distinction is necessary.
             */
            for (Field mapField : mapFieldsList) {
                if (mapField.getOrder() <= 0) {
                    continue;
                }
                if (mapField.getOrder() < myFields.size() + 1) {
                    myFields.add(mapField.getOrder() - 1, mapField);
                    continue;
                }
                myFields.add(mapField);
            }
        }

        //don't allow any modification of the fields
        return Collections.unmodifiableSet(new LinkedHashSet<Field>(myFields));
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public boolean isMasterFieldGroup() {
        if (getTitle() != null && getTitle().toLowerCase().contains("master")) {
            return true;
        }
        return false;
    }

    public boolean containsFieldData() {
        for (Field field : fields) {
            if (field.getValue() != null) {
                return true;
            }
        }
        return false;
    }

}

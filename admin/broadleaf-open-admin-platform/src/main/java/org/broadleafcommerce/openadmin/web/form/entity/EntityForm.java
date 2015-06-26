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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class EntityForm {

    protected static final Log LOG = LogFactory.getLog(EntityForm.class);

    public static final String HIDDEN_GROUP = "hiddenGroup";
    public static final String MAP_KEY_GROUP = "keyGroup";
    public static final String DEFAULT_GROUP_NAME = "Default";
    public static final Integer DEFAULT_GROUP_ORDER = 99999;
    public static final String DEFAULT_TAB_NAME = "General";
    public static final Integer DEFAULT_TAB_ORDER = 100;

    protected String id;
    protected String parentId;
    protected String idProperty = "id";
    protected String ceilingEntityClassname;
    protected String entityType;
    protected String mainEntityName;
    protected String sectionKey;
    protected Boolean readOnly = false;

    /**
     * special member used for only for a Translation entity form. Set at the controller, in order to populate a hidden field in the form
     * indicating that there were errors and the form should not be allowed to submit.
     */
    protected Boolean preventSubmit = false;

    protected String translationCeilingEntity;
    protected String translationId;

    protected Set<Tab> tabs = new TreeSet<Tab>(new Comparator<Tab>() {

        @Override
        public int compare(Tab o1, Tab o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getTitle(), o2.getTitle())
                    .toComparison();
        }
    });
    protected List<SectionCrumb> sectionCrumbs = new ArrayList<SectionCrumb>();

    // This is used to data-bind when this entity form is submitted
    protected Map<String, Field> fields = null;

    // This is used in cases where there is a sub-form on this page that is dynamically
    // rendered based on other values on this entity form. It is keyed by the name of the
    // property that drives the dynamic form.
    protected Map<String, EntityForm> dynamicForms = new LinkedHashMap<String, EntityForm>();

    // These values are used when dynamic forms are in play. They are not rendered to the client,
    // but they can be used when performing actions on the submit event
    protected Map<String, DynamicEntityFormInfo> dynamicFormInfos = new LinkedHashMap<String, DynamicEntityFormInfo>();

    protected List<EntityFormAction> actions = new ArrayList<EntityFormAction>();

    protected Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * @return a flattened, field name keyed representation of all of 
     * the fields in all of the groups for this form. This set will also includes all of the dynamic form
     * fields.
     * 
     * Note that if there collisions between the dynamic form fields and the fields on this form (meaning that they
     * have the same name), then the dynamic form field will be excluded from the map and the preference will be given
     * to first-level entities
     * 
     * @see {@link #getFields(boolean)}
     */
    public Map<String, Field> getFields() {
        if (fields == null) {
            Map<String, Field> map = new LinkedHashMap<String, Field>();
            for (Tab tab : tabs) {
                for (FieldGroup group : tab.getFieldGroups()) {
                    for (Field field : group.getFields()) {
                        map.put(field.getName(), field);
                    }
                }
            }
            fields = map;
        }

        for (Entry<String, EntityForm> entry : dynamicForms.entrySet()) {
            Map<String, Field> dynamicFormFields = entry.getValue().getFields();
            for (Entry<String, Field> dynamicField : dynamicFormFields.entrySet()) {
                if (fields.containsKey(dynamicField.getKey())) {
                    LOG.info("Excluding dynamic field " + dynamicField.getKey() + " as there is already an occurrance in" +
                            " this entityForm");
                } else {
                    fields.put(dynamicField.getKey(), dynamicField.getValue());
                }
            }
        }

        return fields;
    }

    /**
     * Clears out the cached 'fields' variable which is used to render the form on the frontend. Use this method
     * if you want to force the entityForm to rebuild itself based on the tabs and groups that have been assigned and 
     * populated
     */
    public void clearFieldsMap() {
        fields = null;
    }

    public List<ListGrid> getAllListGrids() {
        List<ListGrid> list = new ArrayList<ListGrid>();
        for (Tab tab : tabs) {
            for (ListGrid lg : tab.getListGrids()) {
                list.add(lg);
            }
        }
        return list;
    }

    /**
     * Convenience method for grabbing a grid by its collection field name. This is very similar to {@link #findField(String)}
     * but differs in that this only searches through the sub collections for the current entity
     * 
     * @param collectionFieldName the field name of the collection on the top-level entity
     * @return
     */
    public ListGrid findListGrid(String collectionFieldName) {
        for (ListGrid grid : getAllListGrids()) {
            if (grid.getSubCollectionFieldName().equals(collectionFieldName)) {
                return grid;
            }
        }
        return null;
    }

    public Tab findTab(String tabTitle) {
        for (Tab tab : tabs) {
            if (tab.getTitle() != null && tab.getTitle().equals(tabTitle)) {
                return tab;
            }
        }
        return null;
    }

    public Tab findTabForField(String fieldName) {
        fieldName = sanitizeFieldName(fieldName);
        for (Tab tab : tabs) {
            for (FieldGroup fieldGroup : tab.getFieldGroups()) {
                for (Field field : fieldGroup.getFields()) {
                    if (field.getName().equals(fieldName)) {
                        return tab;
                    }
                }
            }
        }
        return null;
    }

    public Field findField(String fieldName) {
        fieldName = sanitizeFieldName(fieldName);
        for (Tab tab : tabs) {
            for (FieldGroup fieldGroup : tab.getFieldGroups()) {
                for (Field field : fieldGroup.getFields()) {
                    if (field.getName().equals(fieldName)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Since this field name could come from the frontend (where all fields are referenced like fields[name].value,
     * we need to strip that part out to look up the real field name in this entity
     * @param fieldName
     * @return
     */
    public String sanitizeFieldName(String fieldName) {
        if (fieldName.contains("[")) {
            fieldName = fieldName.substring(fieldName.indexOf('[') + 1, fieldName.indexOf(']'));
        }
        return fieldName;
    }

    public Field removeField(String fieldName) {
        Field fieldToRemove = null;
        FieldGroup containingGroup = null;

        findField: {
            for (Tab tab : tabs) {
                for (FieldGroup fieldGroup : tab.getFieldGroups()) {
                    for (Field field : fieldGroup.getFields()) {
                        if (field.getName().equals(fieldName)) {
                            fieldToRemove = field;
                            containingGroup = fieldGroup;
                            break findField;
                        }
                    }
                }
            }
        }

        if (fieldToRemove != null) {
            containingGroup.removeField(fieldToRemove);
        }

        if (fields != null) {
            fieldToRemove = fields.remove(fieldName);
        }

        return fieldToRemove;
    }

    public void removeTab(Tab tab) {
        tabs.remove(tab);
    }

    public void removeTab(String tabName) {
        if (tabs != null) {
            Iterator<Tab> tabIterator = tabs.iterator();
            while (tabIterator.hasNext()) {
                Tab currentTab = tabIterator.next();
                if (tabName.equals(currentTab.getTitle())) {
                    tabIterator.remove();
                }
            }
        }
    }

    public ListGrid removeListGrid(String subCollectionFieldName) {
        ListGrid lgToRemove = null;
        Tab containingTab = null;

        findLg: {
            for (Tab tab : tabs) {
                for (ListGrid lg : tab.getListGrids()) {
                    if (subCollectionFieldName.equals(lg.getSubCollectionFieldName())) {
                        lgToRemove = lg;
                        containingTab = tab;
                        break findLg;
                    }
                }
            }
        }

        if (lgToRemove != null) {
            containingTab.removeListGrid(lgToRemove);
        }

        if (containingTab != null && containingTab.getListGrids().size() == 0 && containingTab.getFields().size() == 0) {
            removeTab(containingTab);
        }

        return lgToRemove;
    }

    public void addHiddenField(Field field) {
        if (StringUtils.isBlank(field.getFieldType())) {
            field.setFieldType(SupportedFieldType.HIDDEN.toString());
        }
        addField(field, HIDDEN_GROUP, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(Field field) {
        addField(field, DEFAULT_GROUP_NAME, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addMapKeyField(Field field) {
        addField(field, MAP_KEY_GROUP, 0, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(Field field, String groupName, Integer groupOrder, String tabName, Integer tabOrder) {
        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        groupOrder = groupOrder == null ? DEFAULT_GROUP_ORDER : groupOrder;
        tabName = tabName == null ? DEFAULT_TAB_NAME : tabName;
        tabOrder = tabOrder == null ? DEFAULT_TAB_ORDER : tabOrder;

        // Tabs and groups should be looked up by their display, translated name since 2 unique strings can display the same
        // thing when they are looked up in message bundles after display
        // When displayed on the form the duplicate groups and tabs look funny
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getMessageSource() != null) {
            groupName = context.getMessageSource().getMessage(groupName, null, groupName, context.getJavaLocale());
            tabName = context.getMessageSource().getMessage(tabName, null, tabName, context.getJavaLocale());
        }

        Tab tab = findTab(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            tab.setOrder(tabOrder);
            tabs.add(tab);
        }

        FieldGroup fieldGroup = tab.findGroup(groupName);
        if (fieldGroup == null) {
            fieldGroup = new FieldGroup();
            fieldGroup.setTitle(groupName);
            fieldGroup.setOrder(groupOrder);
            tab.getFieldGroups().add(fieldGroup);
        }

        fieldGroup.addField(field);
    }

    public void addListGrid(ListGrid listGrid, String tabName, Integer tabOrder) {
        // Tabs should be looked up and referenced by their display name
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null && context.getMessageSource() != null) {
            tabName = context.getMessageSource().getMessage(tabName, null, tabName, context.getJavaLocale());
        }
        Tab tab = findTab(tabName);
        if (tab == null) {
            tab = new Tab();
            tab.setTitle(tabName);
            tab.setOrder(tabOrder);
            tabs.add(tab);
        }

        tab.getListGrids().add(listGrid);
    }

    /**
     * Uses a zero based position.   Use 0 to add to the top of the list.
     * @param position
     * @param action
     */
    public void addAction(int position, EntityFormAction action) {
        if (actions.size() > position) {
            actions.add(position, action);
        } else {
            actions.add(action);
        }
    }

    public void addAction(EntityFormAction action) {
        actions.add(action);
    }

    public void removeAction(EntityFormAction action) {
        actions.remove(action);
    }

    public void removeAllActions() {
        actions.clear();
    }

    public EntityForm getDynamicForm(String name) {
        return getDynamicForms().get(name);
    }

    public void putDynamicForm(String name, EntityForm ef) {
        getDynamicForms().put(name, ef);
    }

    public DynamicEntityFormInfo getDynamicFormInfo(String name) {
        return getDynamicFormInfos().get(name);
    }

    public void putDynamicFormInfo(String name, DynamicEntityFormInfo info) {
        getDynamicFormInfos().put(name, info);
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public Boolean getPreventSubmit() {
        return preventSubmit;
    }

    public void setPreventSubmit() {
        this.preventSubmit = true;
    }

    public void setReadOnly() {
        setReadOnly(true);
    }

    public void setReadOnly(boolean readOnly) {
        if (getFields() != null) {
            for (Entry<String, Field> entry : getFields().entrySet()) {
                entry.getValue().setReadOnly(readOnly);
            }
        }

        if (getAllListGrids() != null) {
            for (ListGrid lg : getAllListGrids()) {
                lg.setReadOnly(readOnly);
            }
        }

        if (getDynamicForms() != null) {
            for (Entry<String, EntityForm> entry : getDynamicForms().entrySet()) {
                entry.getValue().setReadOnly(readOnly);
            }
        }

        actions.clear();
        this.readOnly = readOnly;
    }

    public List<EntityFormAction> getActions() {
        List<EntityFormAction> clonedActions = new ArrayList<EntityFormAction>(actions);
        Collections.reverse(clonedActions);
        return Collections.unmodifiableList(clonedActions);
    }

    public FieldGroup collapseToOneFieldGroup() {
        Tab newTab = new Tab();
        FieldGroup newFg = new FieldGroup();
        newTab.getFieldGroups().add(newFg);

        for (Tab tab : getTabs()) {
            for (FieldGroup fg : tab.getFieldGroups()) {
                for (Field field : fg.getFields()) {
                    newFg.addField(field);
                }
            }
        }

        getTabs().clear();
        getTabs().add(newTab);

        return newFg;
    }

    public String getTranslationCeilingEntity() {
        return translationCeilingEntity == null ? ceilingEntityClassname : translationCeilingEntity;
    }

    public void setTranslationCeilingEntity(String translationCeilingEntity) {
        this.translationCeilingEntity = translationCeilingEntity;
    }

    public String getTranslationId() {
        return translationId == null ? id : translationId;
    }

    public void setTranslationId(String translationId) {
        this.translationId = translationId;
    }

    /* *********************** */
    /* GENERIC GETTERS/SETTERS */
    /* *********************** */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    public String getCeilingEntityClassname() {
        return ceilingEntityClassname;
    }

    public void setCeilingEntityClassname(String ceilingEntityClassname) {
        this.ceilingEntityClassname = ceilingEntityClassname;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getMainEntityName() {
        return StringUtils.isBlank(mainEntityName) ? "" : mainEntityName;
    }

    public void setMainEntityName(String mainEntityName) {
        this.mainEntityName = mainEntityName;
    }

    public String getSectionKey() {
        return sectionKey.charAt(0) == '/' ? sectionKey : '/' + sectionKey;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public Set<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Set<Tab> tabs) {
        this.tabs = tabs;
    }

    public Map<String, EntityForm> getDynamicForms() {
        return dynamicForms;
    }

    public void setDynamicForms(Map<String, EntityForm> dynamicForms) {
        this.dynamicForms = dynamicForms;
    }

    public Map<String, DynamicEntityFormInfo> getDynamicFormInfos() {
        return dynamicFormInfos;
    }

    public void setDynamicFormInfos(Map<String, DynamicEntityFormInfo> dynamicFormInfos) {
        this.dynamicFormInfos = dynamicFormInfos;
    }

    public void setActions(List<EntityFormAction> actions) {
        this.actions = actions;
    }

    public List<SectionCrumb> getSectionCrumbsImpl() {
        return sectionCrumbs;
    }

    public void setSectionCrumbsImpl(List<SectionCrumb> sectionCrumbs) {
        if (sectionCrumbs == null) {
            this.sectionCrumbs.clear();
            return;
        }
        this.sectionCrumbs = sectionCrumbs;
    }

    public void setSectionCrumbs(String crumbs) {
        List<SectionCrumb> myCrumbs = new ArrayList<SectionCrumb>();
        if (!StringUtils.isEmpty(crumbs)) {
            String[] crumbParts = crumbs.split(",");
            for (String part : crumbParts) {
                SectionCrumb crumb = new SectionCrumb();
                String[] crumbPieces = part.split("--");
                crumb.setSectionIdentifier(crumbPieces[0]);
                crumb.setSectionId(crumbPieces[1]);
                if (!myCrumbs.contains(crumb)) {
                    myCrumbs.add(crumb);
                }
            }
        }
        sectionCrumbs = myCrumbs;
    }

    public String getSectionCrumbs() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (SectionCrumb section : sectionCrumbs) {
            sb.append(section.getSectionIdentifier());
            sb.append("--");
            sb.append(section.getSectionId());
            if (index < sectionCrumbs.size() - 1) {
                sb.append(",");
            }
            index++;
        }
        return sb.toString();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}

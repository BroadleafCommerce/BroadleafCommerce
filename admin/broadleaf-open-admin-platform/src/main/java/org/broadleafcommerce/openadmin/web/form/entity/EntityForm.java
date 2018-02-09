/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.GroupMetadata;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class EntityForm {

    protected static final Log LOG = LogFactory.getLog(EntityForm.class);

    public static final String HIDDEN_GROUP = "hiddenGroup";
    public static final String MAP_KEY_GROUP = "keyGroup";
    public static final String DEFAULT_GROUP_NAME = "General";
    public static final Integer DEFAULT_GROUP_ORDER = 99999;
    public static final Integer DEFAULT_COLUMN = 0;
    public static final String DEFAULT_TAB_NAME = "General";
    public static final Integer DEFAULT_TAB_ORDER = 99999;

    protected String id;
    protected String parentId;
    protected String idProperty = "id";
    protected String ceilingEntityClassname;
    protected String entityType;
    protected String mainEntityName;
    protected String sectionKey;
    protected String encType;
    protected Boolean readOnly = false;

    /**
     * special member used for only for a Translation entity form. Set at the controller, in order to populate a hidden field in the form
     * indicating that there were errors and the form should not be allowed to submit.
     */
    protected Boolean preventSubmit = false;

    /**
     * a string representation of a Javascript object containing a map of fields => errors
     * Useful when filling a translation form, as the (only) way to determine to which fields error messaging needs to be attached
     */
    protected String jsErrorMap;

    protected String translationCeilingEntity;
    protected String translationId;

    protected TreeSet<Tab> tabs = new TreeSet<Tab>(new Comparator<Tab>() {

        @Override
        public int compare(Tab o1, Tab o2) {
            return new CompareToBuilder()
                    .append(o1.getOrder(), o2.getOrder())
                    .append(o1.getTitle(), o2.getTitle())
                    .append(o1.getKey(), o2.getKey())
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
                if (fields.containsKey(dynamicField.getKey()) && LOG.isDebugEnabled()) {
                    LOG.debug("Excluding dynamic field " + StringUtil.sanitize(dynamicField.getKey()) +
                            " as there is already an occurrence in this entityForm");
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
            for (FieldGroup group : tab.getFieldGroups()){
                for (ListGrid lg : group.getListGrids()) {
                    list.add(lg);
                }
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

    public FieldGroup findGroup(String groupName) {
        for (Tab tab : tabs) {
            FieldGroup fieldGroup = tab.findGroupByKey(groupName);
            if (fieldGroup != null) {
                return fieldGroup;
            }
        }
        return null;
    }

    public Tab findTab(String tabKey) {
        for (Tab tab : tabs) {
            if (tab.getKey() != null && tab.getKey().equals(tabKey)) {
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
        Map<String, Field> fields = getFields();

        return fields.get(fieldName);
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

    public void removeGroup(FieldGroup group) {
        for (Tab tab : getTabs()) {
            tab.removeFieldGroup(group);
        }
    }

    public void removeTab(Tab tab) {
        tabs.remove(tab);
    }

    public void removeTab(String tabName) {
        if (tabs != null) {
            Iterator<Tab> tabIterator = tabs.iterator();
            while (tabIterator.hasNext()) {
                Tab currentTab = tabIterator.next();
                if (tabName.equals(currentTab.getKey())
                        || tabName.equals(currentTab.getTitle())) {
                    tabIterator.remove();
                }
            }
        }
    }

    public ListGrid removeListGrid(String subCollectionFieldName) {
        ListGrid lgToRemove = null;
        Tab containingTab = null;
        FieldGroup containingGroup = null;

        findLg:
        {
            for (Tab tab : tabs) {
                for (ListGrid lg : tab.getListGrids()) {
                    if (subCollectionFieldName.equals(lg.getSubCollectionFieldName())) {
                        lgToRemove = lg;
                        containingTab = tab;
                        break findLg;
                    }
                }
                for (FieldGroup group : tab.getFieldGroups()) {
                    for (ListGrid lg : group.getListGrids()) {
                        if (subCollectionFieldName.equals(lg.getSubCollectionFieldName())) {
                            lgToRemove = lg;
                            containingTab = tab;
                            containingGroup = group;
                            break findLg;
                        }
                    }
                }
            }
        }

        if (lgToRemove != null && containingGroup != null) {
            containingGroup.removeListGrid(lgToRemove);
        } else if (lgToRemove != null && containingGroup == null) {
            containingTab.removeListGrid(lgToRemove);
        }

        if (containingGroup != null && containingGroup.getGroupItems().isEmpty()) {
            removeGroup(containingGroup);
        } else if (containingTab != null && containingTab.getListGrids().isEmpty() && containingTab.getFields().isEmpty()) {
            removeTab(containingTab);
        }

        return lgToRemove;
    }

    public void addHiddenField(ClassMetadata cmd, Field field) {
        if (StringUtils.isBlank(field.getFieldType())) {
            field.setFieldType(SupportedFieldType.HIDDEN.toString());
        }
        addField(cmd, field, HIDDEN_GROUP, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(ClassMetadata cmd, Field field) {
        addField(cmd, field, DEFAULT_GROUP_NAME, DEFAULT_GROUP_ORDER, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addMapKeyField(ClassMetadata cmd, Field field) {
        addField(cmd, field, MAP_KEY_GROUP, 0, DEFAULT_TAB_NAME, DEFAULT_TAB_ORDER);
    }

    public void addField(ClassMetadata cmd, Field field, String groupName, Integer groupOrder, String tabName, Integer tabOrder) {
        // Note: If a field creates a new tab/group (expected to be a rare occurrence), the firstTab/firstGroup may change
        //      as fields are added for a given EntityForm.
        Tab firstTab = tabs.isEmpty() ? null : tabs.first();
        FieldGroup firstGroup = firstTab == null || firstTab.getFieldGroups().isEmpty() ? null : ((TreeSet<FieldGroup>) firstTab.getFieldGroups()).first();

        tabName = tabName == null ? (firstTab == null || firstTab.getKey() == null ? DEFAULT_TAB_NAME : firstTab.getKey()) : tabName;
        tabOrder = tabOrder == null ? (firstTab == null || firstTab.getOrder() == null ? DEFAULT_TAB_ORDER : firstTab.getOrder()) : tabOrder;
        groupName = groupName == null ? (firstGroup == null || firstGroup.getKey() == null ? DEFAULT_GROUP_NAME : firstGroup.getKey()) : groupName;
        groupOrder = groupOrder == null ? (firstGroup == null || firstGroup.getOrder() == null ? DEFAULT_GROUP_ORDER : firstGroup.getOrder()) : groupOrder;

        // Check CMD for Tab/Group name overrides so that Tabs/Groups can be properly found by their display names
        boolean groupFound = false;
        Map<String, TabMetadata> tabMetadataMap = cmd.getTabAndGroupMetadata();
        if (tabMetadataMap != null) {
            for (String tabKey : tabMetadataMap.keySet()) {
                Map<String, GroupMetadata> groupMetadataMap = tabMetadataMap.get(tabKey).getGroupMetadata();
                for (String groupKey : groupMetadataMap.keySet()) {
                    if (groupKey.equals(groupName) || groupMetadataMap.get(groupKey).getGroupName().equals(groupName)) {
                        groupName = groupMetadataMap.get(groupKey).getGroupName();
                        groupFound = true;
                        break;
                    }
                }
                if (groupFound) {
                    break;
                }
                if ((tabKey.equals(tabName)) ||
                        (tabMetadataMap.get(tabKey).getTabName() != null
                                && tabMetadataMap.get(tabKey).getTabName().equals(tabName))) {
                    tabName = tabMetadataMap.get(tabKey).getTabName();
                }
            }
        }

        FieldGroup fieldGroup = findGroup(groupName);
        if (fieldGroup == null) {
            Tab tab = findTab(tabName);
            if (tab == null) {
                tab = new Tab();
                tab.setKey(tabName);
                tab.setTitle(BLCMessageUtils.getMessage(tabName));
                tab.setOrder(tabOrder);
                tabs.add(tab);
            }

            // Add new group for the field to be placed into
            // If group exists, this code will not run
            fieldGroup = new FieldGroup();
            fieldGroup.setKey(groupName);
            fieldGroup.setTitle(BLCMessageUtils.getMessage(groupName));
            fieldGroup.setOrder(groupOrder);
            tab.getFieldGroups().add(fieldGroup);
        }

        fieldGroup.addField(field);

        // Make sure to add the field to the fields "cache".
        // If getFields() was called before this field was added, the "cache" was set. Since we're
        // adding another field here, we need to add the field to the fields "cache".
        // If the fields map is null, then the "cache" is not set. Therefore, we should not add this field,
        // but instead wait for getFields() to build the entire map.
        if (fields != null) {
            fields.put(field.getName(), field);
        }
    }

    public void addListGrid(ClassMetadata cmd, ListGrid listGrid, String tabName, Integer tabOrder, String groupName, boolean isTabPresent) {
        tabName = tabName == null ? DEFAULT_TAB_NAME : tabName;
        tabOrder = tabOrder == null ? DEFAULT_TAB_ORDER : tabOrder;

        // Check CMD for Tab/Group name overrides so that Tabs/Groups can be properly found by their display names
        boolean groupFound = false;
        Map<String, TabMetadata> tabMetadataMap = cmd.getTabAndGroupMetadata();
        for (String tabKey : tabMetadataMap.keySet()) {
            Map<String, GroupMetadata> groupMetadataMap = tabMetadataMap.get(tabKey).getGroupMetadata();
            for (String groupKey : groupMetadataMap.keySet()) {
                if (groupKey.equals(groupName) || groupMetadataMap.get(groupKey).getGroupName().equals(groupName)) {
                    groupName = groupMetadataMap.get(groupKey).getGroupName();
                    groupFound = true;
                    break;
                }
            }
            if (groupFound) {
                break;
            }
            if (tabKey.equals(tabName) || tabMetadataMap.get(tabKey).getTabName().equals(tabName)) {
                tabName = tabMetadataMap.get(tabKey).getTabName();
            }
        }

        FieldGroup fieldGroup = findGroup(groupName);
        Tab tab = findTab(tabName);
        if (fieldGroup != null) {
            fieldGroup.addListGrid(listGrid);
        } else if (fieldGroup == null && tab != null) {
            tab.getListGrids().add(listGrid);
        } else {
            tab = new Tab();
            tab.setKey(tabName);
            tab.setTitle(BLCMessageUtils.getMessage(tabName));
            tab.setOrder(tabOrder);
            tab.setTabsPresent(isTabPresent);
            tabs.add(tab);
            tab.getListGrids().add(listGrid);
        }
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
                lg.setIsReadOnly(readOnly);
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

    public EntityFormAction findActionById(String id) {
        for (EntityFormAction action : getActions()) {
            if (Objects.equals(action.getId(), id)) {
                return action;
            }
        }

        return null;
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

    public String getEncType() {
        return encType;
    }

    /**
     * Changes the encoding type on the built {@code <form>}
     */
    public void setEncType(String encType) {
        this.encType = encType;
    }

    public String getSectionKey() {
        if (sectionKey == null) {
            return null;
        }
        return sectionKey.charAt(0) == '/' ? sectionKey : '/' + sectionKey;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public Set<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Set<Tab> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
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

    public String getJsErrorMap() {
        return jsErrorMap;
    }

    public void setJsErrorMap(String jsErrorMap) {
        this.jsErrorMap = jsErrorMap;
    }

    public String addTabFromTabMetadata(TabMetadata tabMetadata) {
        Tab newTab = new Tab();
        newTab.setKey(tabMetadata.getTabName());
        if (tabMetadata.getTabName() != null) {
            newTab.setTitle(BLCMessageUtils.getMessage(tabMetadata.getTabName()));
        }
        newTab.setOrder(tabMetadata.getTabOrder());
        tabs.add(newTab);
        return newTab.getKey();
    }

    public void addGroupFromGroupMetadata(GroupMetadata groupMetadata, String unprocessedTabName) {
        FieldGroup newGroup = new FieldGroup();
        newGroup.setKey(groupMetadata.getGroupName());
        newGroup.setTitle(BLCMessageUtils.getMessage(groupMetadata.getGroupName()));
        newGroup.setOrder(groupMetadata.getGroupOrder());
        newGroup.setColumn(groupMetadata.getColumn());
        newGroup.setIsUntitled(groupMetadata.getUntitled());
        newGroup.setToolTip(groupMetadata.getTooltip());
        newGroup.setCollapsed(groupMetadata.getCollapsed());
        newGroup.setToolTip(groupMetadata.getTooltip());

        Tab tab = findTab(unprocessedTabName);
        if (groupMetadata.getColumn() != DEFAULT_COLUMN) {
            tab.setIsMultiColumn(true);
        }

        tab.getFieldGroups().add(newGroup);
    }

}

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
package org.broadleafcommerce.common.presentation.override;

/**
 * @author Jeff Fischer
 */
public class PropertyType {


    public static class AdminTabPresentation {
        public static final String NAME = "name";
        public static final String ORDER = "order";
    }

    public static class AdminGroupPresentation {
        public static final String NAME = "name";
        public static final String ORDER = "order";
        public static final String COLUMN = "column";
        public static final String UNTITLED = "untitled";
        public static final String TOOLTIP = "tooltip";
        public static final String COLLAPSED = "collapsed";
    }

    public static class AdminPresentation {
        public static final String FRIENDLYNAME = "friendlyName";
        public static final String ADDFRIENDLYNAME = "addFriendlyName";
        public static final String SECURITYLEVEL = "securityLevel";
        public static final String ORDER = "order";
        public static final String GRIDORDER = "gridOrder";
        public static final String VISIBILITY = "visibility";
        public static final String FIELDTYPE = "fieldType";
        public static final String LARGEENTRY = "largeEntry";
        public static final String PROMINENT = "prominent";
        public static final String COLUMNWIDTH = "columnWidth";
        public static final String BROADLEAFENUMERATION = "broadleafEnumeration";
        public static final String HIDEENUMERATIONIFEMPTY = "hideEnumerationIfEmpty";
        public static final String FIELDCOMPONENTRENDERER = "fieldComponentRenderer";
        public static final String GRIDFIELDCOMPONENTRENDERER = "gridFieldComponentRenderer";
        public static final String REQUIREDOVERRIDE = "requiredOverride";
        public static final String EXCLUDED = "excluded";
        public static final String TOOLTIP = "tooltip";
        public static final String HELPTEXT = "helpText";
        public static final String HINT = "hint";
        public static final String SHOWIFPROPERTY = "showIfProperty";
        public static final String SHOWIFFIELDEQUALS = "showIfFieldEquals";
        public static final String CURRENCYCODEFIELD = "currencyCodeField";
        public static final String RULEIDENTIFIER = "ruleIdentifier";
        public static final String READONLY = "readOnly";
        public static final String VALIDATIONCONFIGURATIONS = "validationConfigurations";
        public static final String DEFAULTVALUE = "defaultValue";
        public static final String GROUP = "group";
        public static final String TAB = "tab";
        public static final String CANLINKTOEXTERNALENTITY = "canLinkToExternalEntity";
        public static final String TRANSLATABLE = "translatable";
        public static final String ASSOCIATEDFIELDNAME = "associatedFieldName";

        @Deprecated
        public static final String GROUPORDER = "groupOrder";
        @Deprecated
        public static final String GROUPCOLLAPSED = "groupCollapsed";
        @Deprecated
        public static final String TABORDER = "tabOrder";
    }

    public static class AdminPresentationToOneLookup {
        public static final String LOOKUPDISPLAYPROPERTY = "lookupDisplayProperty";
        public static final String USESERVERSIDEINSPECTIONCACHE = "useServerSideInspectionCache";
        public static final String LOOKUPTYPE = "lookupType";
        public static final String CUSTOMCRITERIA = "customCriteria";
        public static final String FORCEPOPULATECHILDPROPERTIES = "forcePopulateChildProperties";
        public static final String ENABLETYPEAHEADLOOKUP = "enableTypeaheadLookup";
    }

    public static class AdminPresentationDataDrivenEnumeration {
        public static final String OPTIONLISTENTITY = "optionListEntity";
        public static final String OPTIONVALUEFIELDNAME = "optionValueFieldName";
        public static final String OPTIONDISPLAYFIELDNAME = "optionDisplayFieldName";
        public static final String OPTIONCANEDITVALUES = "optionCanEditValues";
        public static final String OPTIONFILTERPARAMS = "optionFilterParams";
        public static final String OPTIONHIDEIFEMPTY = "optionHideIfEmpty";
    }

    public static class AdminPresentationAdornedTargetCollection {
        public static final String FRIENDLYNAME = "friendlyName";
        public static final String SECURITYLEVEL = "securityLevel";
        public static final String EXCLUDED = "excluded";
        public static final String SHOWIFPROPERTY = "showIfProperty";
        public static final String SHOWIFFIELDEQUALS = "showIfFieldEquals";
        public static final String READONLY = "readOnly";
        public static final String USESERVERSIDEINSPECTIONCACHE = "useServerSideInspectionCache";
        public static final String PARENTOBJECTPROPERTY = "parentObjectProperty";
        public static final String PARENTOBJECTIDPROPERTY = "parentObjectIdProperty";
        public static final String TARGETOBJECTPROPERTY = "targetObjectProperty";
        public static final String MAINTAINEDADORNEDTARGETFIELDS = "maintainedAdornedTargetFields";
        public static final String GRIDVISIBLEFIELDS = "gridVisibleFields";
        public static final String SELECTIZEVISIBLEFIELD = "selectizeVisibleField";
        public static final String TARGETOBJECTIDPROPERTY = "targetObjectIdProperty";
        public static final String JOINENTITYCLASS = "joinEntityClass";
        public static final String SORTPROPERTY = "sortProperty";
        public static final String SORTASCENDING = "sortAscending";
        public static final String IGNOREADORNEDPROPERTIES = "ignoreAdornedProperties";
        public static final String ORDER = "order";
        public static final String CUSTOMCRITERIA = "customCriteria";
        public static final String CURRENCYCODEFIELD = "currencyCodeField";
        public static final String OPERATIONTYPES = "operationTypes";
        public static final String GROUP = "group";
        public static final String ADORNEDTARGETADDTYPE = "adornedTargetAddType";

        @Deprecated
        public static final String TAB = "tab";
        @Deprecated
        public static final String TABORDER = "tabOrder";
    }

    public static class AdminPresentationCollection {
        public static final String FRIENDLYNAME = "friendlyName";
        public static final String ADDFRIENDLYNAME = "addFriendlyName";
        public static final String SECURITYLEVEL = "securityLevel";
        public static final String EXCLUDED = "excluded";
        public static final String READONLY = "readOnly";
        public static final String USESERVERSIDEINSPECTIONCACHE = "useServerSideInspectionCache";
        public static final String ADDTYPE = "addType";
        public static final String SELECTIZEVISIBLEFIELD = "selectizeVisibleField";
        public static final String MANYTOFIELD = "manyToField";
        public static final String ORDER = "order";
        public static final String SORTPROPERTY = "sortProperty";
        public static final String SORTASCENDING = "sortAscending";
        public static final String CUSTOMCRITERIA = "customCriteria";
        public static final String OPERATIONTYPES = "operationTypes";
        public static final String SHOWIFPROPERTY = "showIfProperty";
        public static final String SHOWIFFIELDEQUALS = "showIfFieldEquals";
        public static final String CURRENCYCODEFIELD = "currencyCodeField";
        public static final String GROUP = "group";

        @Deprecated
        public static final String TAB = "tab";
        @Deprecated
        public static final String TABORDER = "tabOrder";
    }

    public static class AdminPresentationMap {
        public static final String FRIENDLYNAME = "friendlyName";
        public static final String SECURITYLEVEL = "securityLevel";
        public static final String EXCLUDED = "excluded";
        public static final String READONLY = "readOnly";
        public static final String USESERVERSIDEINSPECTIONCACHE = "useServerSideInspectionCache";
        public static final String ORDER = "order";
        public static final String KEYCLASS = "keyClass";
        public static final String MAPKEYVALUEPROPERTY = "mapKeyValueProperty";
        public static final String KEYPROPERTYFRIENDLYNAME = "keyPropertyFriendlyName";
        public static final String VALUECLASS = "valueClass";
        public static final String DELETEENTITYUPONREMOVE = "deleteEntityUponRemove";
        public static final String VALUEPROPERTYFRIENDLYNAME = "valuePropertyFriendlyName";
        public static final String ISSIMPLEVALUE = "isSimpleValue";
        public static final String MEDIAFIELD = "mediaField";
        public static final String KEYS = "keys";
        public static final String FORCEFREEFORMKEYS = "forceFreeFormKeys";
        public static final String MANYTOFIELD = "manyToField";
        public static final String MAPKEYOPTIONENTITYCLASS = "mapKeyOptionEntityClass";
        public static final String MAPKEYOPTIONENTITYDISPLAYFIELD = "mapKeyOptionEntityDisplayField";
        public static final String MAPKEYOPTIONENTITYVALUEFIELD = "mapKeyOptionEntityValueField";
        public static final String CUSTOMCRITERIA = "customCriteria";
        public static final String OPERATIONTYPES = "operationTypes";
        public static final String SHOWIFPROPERTY = "showIfProperty";
        public static final String SHOWIFFIELDEQUALS = "showIfFieldEquals";
        public static final String CURRENCYCODEFIELD = "currencyCodeField";
        public static final String GROUP = "group";

        @Deprecated
        public static final String TAB = "tab";
        @Deprecated
        public static final String TABORDER = "tabOrder";
    }
}

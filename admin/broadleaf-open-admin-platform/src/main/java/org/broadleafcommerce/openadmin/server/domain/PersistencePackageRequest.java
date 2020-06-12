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
package org.broadleafcommerce.openadmin.server.domain;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.util.BLCArrayUtils;
import org.broadleafcommerce.openadmin.dto.*;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.server.service.type.FetchType;

import java.util.*;

/**
 * A DTO class used to seed a persistence package.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PersistencePackageRequest {

    protected Type type;
    protected String ceilingEntityClassname;
    protected String securityCeilingEntityClassname;
    protected String configKey;
    protected AdornedTargetList adornedList;
    protected MapStructure mapStructure;
    protected Entity entity;
    protected ForeignKey foreignKey;
    protected Integer startIndex;
    protected Integer maxIndex;
    protected Integer maxResults;
    protected Long lastId;
    protected Long firstId;
    protected Integer upperCount;
    protected Integer lowerCount;
    protected Integer pageSize;
    protected Boolean presentationFetch;
    protected SectionCrumb[] sectionCrumbs;
    protected String sectionEntityField;
    protected String requestingEntityName;
    protected String msg;
    protected Map<String, PersistencePackageRequest> subRequests = new LinkedHashMap<String, PersistencePackageRequest>();
    protected boolean validateUnsubmittedProperties = true;
    protected boolean isUpdateLookupType = false;
    protected boolean isTreeCollection = false;

    protected OperationTypes operationTypesOverride = null;

    // These properties are accessed via getters and setters that operate on arrays.
    // We back them with a list so that we can have the convenience .add methods
    protected List<ForeignKey> additionalForeignKeys = new ArrayList<ForeignKey>();
    protected List<String> customCriteria = new ArrayList<String>();
    protected List<FilterAndSortCriteria> filterAndSortCriteria = new ArrayList<FilterAndSortCriteria>();

    public enum Type {
        STANDARD,
        ADORNED,
        MAP
    }

    /* ******************* */
    /* STATIC INITIALIZERS */
    /* ******************* */

    public static PersistencePackageRequest standard() {
        return new PersistencePackageRequest(Type.STANDARD);
    }

    public static PersistencePackageRequest adorned() {
        return new PersistencePackageRequest(Type.ADORNED);
    }

    public static PersistencePackageRequest map() {
        return new PersistencePackageRequest(Type.MAP);
    }

    /**
     * Creates a semi-populate PersistencePacakageRequest based on the specified Metadata. This initializer
     * will copy over persistence perspective items from the metadata as well as set the appropriate OperationTypes
     * as specified in the annotation/xml configuration for the field.
     * 
     * @param md
     * @return the newly created PersistencePackageRequest
     */
    public static PersistencePackageRequest fromMetadata(FieldMetadata md, List<SectionCrumb> sectionCrumbs) {
        final PersistencePackageRequest request = new PersistencePackageRequest();

        md.accept(new MetadataVisitor() {

            @Override
            public void visit(BasicFieldMetadata fmd) {
                request.setType(Type.STANDARD);
                request.setCeilingEntityClassname(fmd.getForeignKeyClass());
                request.setCustomCriteria(fmd.getCustomCriteria());
            }

            @Override
            public void visit(BasicCollectionMetadata fmd) {
                ForeignKey foreignKey = (ForeignKey) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);

                request.setType(Type.STANDARD);
                request.setCeilingEntityClassname(fmd.getCollectionCeilingEntity());
                request.setOperationTypesOverride(fmd.getPersistencePerspective().getOperationTypes());
                request.setForeignKey(foreignKey);
                request.setCustomCriteria(fmd.getCustomCriteria());
            }

            @Override
            public void visit(AdornedTargetCollectionMetadata fmd) {
                AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                request.setType(Type.ADORNED);
                request.setCeilingEntityClassname(fmd.getCollectionCeilingEntity());
                request.setOperationTypesOverride(fmd.getPersistencePerspective().getOperationTypes());
                request.setAdornedList(adornedList);
                request.setCustomCriteria(fmd.getCustomCriteria());
            }

            @Override
            public void visit(MapMetadata fmd) {
                MapStructure mapStructure = (MapStructure) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);

                ForeignKey foreignKey = (ForeignKey) fmd.getPersistencePerspective().
                        getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);

                request.setType(Type.MAP);
                request.setCeilingEntityClassname(foreignKey.getForeignKeyClass());
                request.setOperationTypesOverride(fmd.getPersistencePerspective().getOperationTypes());
                request.setMapStructure(mapStructure);
                request.setForeignKey(foreignKey);
                request.setCustomCriteria(fmd.getCustomCriteria());
            }

            @Override
            public void visit(GroupMetadata gmd) {
                request.setType(Type.STANDARD);
                request.setCeilingEntityClassname(gmd.getOwningClass());
            }

            @Override
            public void visit(TabMetadata tmd) {
                request.setType(Type.STANDARD);
                request.setCeilingEntityClassname(tmd.getOwningClass());
            }
        });
        
        if (sectionCrumbs != null) {
            request.setSectionCrumbs(sectionCrumbs.toArray(new SectionCrumb[sectionCrumbs.size()]));
        }

        return request;
    }

    /* ************ */
    /* CONSTRUCTORS */
    /* ************ */

    public PersistencePackageRequest() {

    }

    public PersistencePackageRequest(Type type) {
        this.type = type;
    }

    /* ************ */
    /* WITH METHODS */
    /* ************ */

    public PersistencePackageRequest withType(Type type) {
        setType(type);
        return this;
    }

    public PersistencePackageRequest withCeilingEntityClassname(String className) {
        setCeilingEntityClassname(className);
        return this;
    }

    public PersistencePackageRequest withSecurityCeilingEntityClassname(String className) {
        setSecurityCeilingEntityClassname(className);
        return this;
    }

    public PersistencePackageRequest withForeignKey(ForeignKey foreignKey) {
        setForeignKey(foreignKey);
        return this;
    }

    public PersistencePackageRequest withConfigKey(String configKey) {
        setConfigKey(configKey);
        return this;
    }

    public PersistencePackageRequest withFilterAndSortCriteria(FilterAndSortCriteria[] filterAndSortCriteria) {
        if (ArrayUtils.isNotEmpty(filterAndSortCriteria)) {
            setFilterAndSortCriteria(filterAndSortCriteria);
        }
        return this;
    }

    public PersistencePackageRequest withAdornedList(AdornedTargetList adornedList) {
        setAdornedList(adornedList);
        return this;
    }

    public PersistencePackageRequest withMapStructure(MapStructure mapStructure) {
        setMapStructure(mapStructure);
        return this;
    }

    public PersistencePackageRequest withCustomCriteria(String[] customCriteria) {
        if (ArrayUtils.isNotEmpty(customCriteria)) {
            setCustomCriteria(customCriteria);
        }
        return this;
    }

    public PersistencePackageRequest withEntity(Entity entity) {
        setEntity(entity);
        return this;
    }
    
    public PersistencePackageRequest withStartIndex(Integer startIndex) {
        setStartIndex(startIndex);
        return this;
    }
    
    public PersistencePackageRequest withMaxIndex(Integer maxIndex) {
        setMaxIndex(maxIndex);
        return this;
    }

    public PersistencePackageRequest withMaxResults(Integer maxResults) {
        setMaxResults(maxResults);
        return this;
    }

    public PersistencePackageRequest withLastId(Long lastId) {
        setLastId(lastId);
        return this;
    }

    public PersistencePackageRequest withFirstId(Long firstId) {
        setFirstId(firstId);
        return this;
    }

    public PersistencePackageRequest withUpperCount(Integer upperCount) {
        setUpperCount(upperCount);
        return this;
    }

    public PersistencePackageRequest withLowerCount(Integer lowerCount) {
        setLowerCount(lowerCount);
        return this;
    }

    public PersistencePackageRequest withPageSize(Integer pageSize) {
        setPageSize(pageSize);
        return this;
    }

    public PersistencePackageRequest withPresentationFetch(Boolean presentationFetch) {
        setPresentationFetch(presentationFetch);
        return this;
    }

    public PersistencePackageRequest withSectionCrumbs(List<SectionCrumb> sectionCrumbs) {
        setSectionCrumbs(sectionCrumbs.toArray(new SectionCrumb[sectionCrumbs.size()]));
        return this;
    }

    public PersistencePackageRequest withSectionEntityField(String sectionEntityField) {
        setSectionEntityField(sectionEntityField);
        return this;
    }
    
    public PersistencePackageRequest withRequestingEntityName(String requestingEntityName) {
        setRequestingEntityName(requestingEntityName);
        return this;
    }

    public PersistencePackageRequest withMsg(String msg) {
        setMsg(msg);
        return this;
    }

    public PersistencePackageRequest withIsUpdateLookupType(boolean isUpdateLookupType) {
        setUpdateLookupType(isUpdateLookupType);
        return this;
    }

    /* *********** */
    /* ADD METHODS */
    /* *********** */

    public PersistencePackageRequest addAdditionalForeignKey(ForeignKey foreignKey) {
        additionalForeignKeys.add(foreignKey);
        return this;
    }

    public PersistencePackageRequest addSubRequest(String infoPropertyName, PersistencePackageRequest subRequest) {
        subRequests.put(infoPropertyName, subRequest);
        return this;
    }

    public PersistencePackageRequest addCustomCriteria(String customCriteria) {
        if (this.customCriteria == null) {
            this.customCriteria = new ArrayList<>();
        }
        
        if (StringUtils.isNotBlank(customCriteria)) {
            this.customCriteria.add(customCriteria);
        }
        return this;
    }

    public PersistencePackageRequest addCustomCriteria(String[] customCriteriaList) {
        if (customCriteriaList != null && customCriteriaList.length > 0) {
            if (this.customCriteria == null) {
                this.customCriteria = new ArrayList<>(Arrays.asList(customCriteriaList));
            } else {
                this.customCriteria.addAll(new ArrayList<>(Arrays.asList(customCriteriaList)));
            }
        }

        return this;
    }

    public PersistencePackageRequest addFilterAndSortCriteria(FilterAndSortCriteria filterAndSortCriteria) {
        this.filterAndSortCriteria.add(filterAndSortCriteria);
        return this;
    }
    
    public PersistencePackageRequest addFilterAndSortCriteria(FilterAndSortCriteria[] filterAndSortCriteria) {
        if (filterAndSortCriteria != null) {
            this.filterAndSortCriteria.addAll(Arrays.asList(filterAndSortCriteria));
        }
        return this;
    }
    
    public PersistencePackageRequest addFilterAndSortCriteria(List<FilterAndSortCriteria> filterAndSortCriteria) {
        this.filterAndSortCriteria.addAll(filterAndSortCriteria);
        return this;
    }

    /* ************** */
    /* REMOVE METHODS */
    /* ************** */
    
    public PersistencePackageRequest removeFilterAndSortCriteria(String name) {
        Iterator<FilterAndSortCriteria> it = filterAndSortCriteria.listIterator();
        while (it.hasNext()) {
            FilterAndSortCriteria fasc = it.next();
            if (fasc.getPropertyId().equals(name)) {
                it.remove();
            }
        }
        return this;
    }

    public PersistencePackageRequest clearFilterAndSortCriteria() {
        Iterator<FilterAndSortCriteria> it = filterAndSortCriteria.listIterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        return this;
    }

    /* ************************ */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************ */

    public String[] getCustomCriteria() {
        String[] arr = new String[this.customCriteria.size()];
        arr = this.customCriteria.toArray(arr);
        return arr;
    }
    
    public ForeignKey[] getAdditionalForeignKeys() {
        ForeignKey[] arr = new ForeignKey[this.additionalForeignKeys.size()];
        arr = this.additionalForeignKeys.toArray(arr);
        return arr;
    }
    
    public void setAdditionalForeignKeys(ForeignKey[] additionalForeignKeys) {
        this.additionalForeignKeys.addAll(Arrays.asList(additionalForeignKeys));
    }

    public void setCustomCriteria(String[] customCriteria) {
        if (customCriteria == null || customCriteria.length == 0) {
            this.customCriteria = new ArrayList<String>();
        } else {
            this.customCriteria = BLCArrayUtils.asList(customCriteria);
        }
    }

    public FilterAndSortCriteria[] getFilterAndSortCriteria() {
        FilterAndSortCriteria[] arr = new FilterAndSortCriteria[this.filterAndSortCriteria.size()];
        arr = this.filterAndSortCriteria.toArray(arr);
        return arr;
    }

    public void setFilterAndSortCriteria(FilterAndSortCriteria[] filterAndSortCriteria) {
        this.filterAndSortCriteria.addAll(Arrays.asList(filterAndSortCriteria));
    }

    /* ************************** */
    /* STANDARD GETTERS / SETTERS */
    /* ************************** */

    public ForeignKey getForeignKey() {
        return foreignKey;
    }
    
    public void setForeignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Returns the entity that should be checked for security purposes.   If no value is defined explicitly, returns the 
     * value for {@link #getCeilingEntityClassname()}.
     * 
     * @return
     */
    public String getSecurityCeilingEntityClassname() {
        if (securityCeilingEntityClassname != null) {
            return securityCeilingEntityClassname;
        } else {
            return getCeilingEntityClassname();
        }
    }

    public void setSecurityCeilingEntityClassname(String securityCeilingEntityClassname) {
        this.securityCeilingEntityClassname = securityCeilingEntityClassname;
    }

    public String getCeilingEntityClassname() {
        return ceilingEntityClassname;
    }
    
    public void setCeilingEntityClassname(String ceilingEntityClassname) {
        this.ceilingEntityClassname = ceilingEntityClassname;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public AdornedTargetList getAdornedList() {
        return adornedList;
    }

    public void setAdornedList(AdornedTargetList adornedList) {
        this.adornedList = adornedList;
    }

    public MapStructure getMapStructure() {
        return mapStructure;
    }

    public void setMapStructure(MapStructure mapStructure) {
        this.mapStructure = mapStructure;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public OperationTypes getOperationTypesOverride() {
        return operationTypesOverride;
    }

    public void setOperationTypesOverride(OperationTypes operationTypesOverride) {
        this.operationTypesOverride = operationTypesOverride;
    }
    
    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(Integer maxIndex) {
        this.maxIndex = maxIndex;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public SectionCrumb[] getSectionCrumbs() {
        return sectionCrumbs;
    }

    public void setSectionCrumbs(SectionCrumb[] sectionCrumbs) {
        this.sectionCrumbs = sectionCrumbs;
    }

    public String getSectionEntityField() {
        return sectionEntityField;
    }

    public void setSectionEntityField(String sectionEntityField) {
        this.sectionEntityField = sectionEntityField;
    }
    
    public String getRequestingEntityName() {
        return requestingEntityName;
    }
    
    public void setRequestingEntityName(String requestingEntityName) {
        this.requestingEntityName = requestingEntityName;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, PersistencePackageRequest> getSubRequests() {
        return subRequests;
    }

    public void setSubRequests(Map<String, PersistencePackageRequest> subRequests) {
        this.subRequests = subRequests;
    }
    public boolean isValidateUnsubmittedProperties() {
        return validateUnsubmittedProperties;
    }

    public void setValidateUnsubmittedProperties(boolean validateUnsubmittedProperties) {
        this.validateUnsubmittedProperties = validateUnsubmittedProperties;
    }

    public boolean isUpdateLookupType() {
        return isUpdateLookupType;
    }
    
    public void setUpdateLookupType(boolean isUpdateLookupType) {
        this.isUpdateLookupType = isUpdateLookupType;
    }

    public boolean isTreeCollection() {
        return isTreeCollection;
    }

    public void setIsTreeCollection(boolean isTreeCollection) {
        this.isTreeCollection = isTreeCollection;
    }

    /**
     * Intended for usage with other than {@link FetchType#DEFAULT}. Denotes the last primary key value of the previous
     * page of records.
     *
     * @return
     */
    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    /**
     * Intended for usage with other than {@link FetchType#DEFAULT}. Denotes the first primary key value of the previous
     * page or records.
     */
    public Long getFirstId() {
        return firstId;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    /**
     * Intended for usage with other than {@link FetchType#DEFAULT}. Denotes the cumulative total count of the previous page
     * of records. For example, if this was the second page of records and each page contained 5 records, the upperCount
     * would be 10.
     *
     * @return
     */
    public Integer getUpperCount() {
        return upperCount;
    }

    public void setUpperCount(Integer upperCount) {
        this.upperCount = upperCount;
    }

    /**
     * Intended for usage with other than {@link FetchType#DEFAULT}. Denotes the cumulative starting count of the previous
     * page of records. For example, if this was the second page of records and each page contained 5 records, the lowerCount
     * would be 5.
     */
    public Integer getLowerCount() {
        return lowerCount;
    }

    public void setLowerCount(Integer lowerCount) {
        this.lowerCount = lowerCount;
    }

    /**
     * The number of records to retrieve in a single page. If null, defaults to the "admin.default.max.results" property value.
     *
     * @return
     */
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Whether or not this represents a fetch request from the presentation layer.
     *
     * @return
     */
    public Boolean getPresentationFetch() {
        return presentationFetch;
    }

    public void setPresentationFetch(Boolean presentationFetch) {
        this.presentationFetch = presentationFetch;
    }
}

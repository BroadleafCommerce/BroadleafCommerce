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

package org.broadleafcommerce.openadmin.server.domain;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.OperationTypes;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A DTO class used to seed a persistence package.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PersistencePackageRequest {

    protected Type type;
    protected String ceilingEntityClassname;
    protected String configKey;
    protected AdornedTargetList adornedList;
    protected MapStructure mapStructure;
    protected Entity entity;
    protected ForeignKey foreignKey;
    protected Integer startIndex;
    protected Integer maxIndex;
    protected Map<String, PersistencePackageRequest> subRequests = new LinkedHashMap<String, PersistencePackageRequest>();
    protected boolean validateUnsubmittedProperties = true;

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
     * Creates a semi-populate PersistencePacakageRequest based on the specified FieldMetadata. This initializer
     * will copy over persistence perspective items from the metadata as well as set the appropriate OperationTypes
     * as specified in the annotation/xml configuration for the field.
     * 
     * @param md
     * @return the newly created PersistencePackageRequest
     */
    public static PersistencePackageRequest fromMetadata(FieldMetadata md) {
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
        });
        
        if (md instanceof CollectionMetadata) {
            request.setCustomCriteria(((CollectionMetadata) md).getCustomCriteria());
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
        if (StringUtils.isNotBlank(customCriteria)) {
            this.customCriteria.add(customCriteria);
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
        this.customCriteria.addAll(Arrays.asList(customCriteria));
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
    
}

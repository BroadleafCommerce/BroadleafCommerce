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
package org.broadleafcommerce.openadmin.dto;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersistencePackage implements Serializable, StateDescriptor {

    private static final long serialVersionUID = 1L;
    
    protected String ceilingEntityFullyQualifiedClassname;
    protected String securityCeilingEntityFullyQualifiedClassname;
    protected String sectionEntityField;
    protected String fetchTypeFullyQualifiedClassname;
    protected PersistencePerspective persistencePerspective;
    protected String[] customCriteria;
    protected Entity entity;
    protected String csrfToken;
    protected String requestingEntityName;
    protected Map<String, PersistencePackage> subPackages = new LinkedHashMap<String, PersistencePackage>();
    protected boolean validateUnsubmittedProperties = true;
    protected SectionCrumb[] sectionCrumbs;
    protected Map<ChangeType, List<PersistencePackage>> deferredOperations = new LinkedHashMap<ChangeType, List<PersistencePackage>>();

    //internalUsage
    protected boolean isProcessedInternal = false;

    protected boolean isTreeCollection = false;

    public PersistencePackage(String ceilingEntityFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, String csrfToken) {
        this(ceilingEntityFullyQualifiedClassname, null, entity, persistencePerspective, customCriteria, csrfToken);
    }
    
    public PersistencePackage(String ceilingEntityFullyQualifiedClassname, String fetchTypeFullyQualifiedClassname, Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, String csrfToken) {
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
        this.fetchTypeFullyQualifiedClassname = fetchTypeFullyQualifiedClassname;
        this.persistencePerspective = persistencePerspective;
        this.entity = entity;
        this.customCriteria = customCriteria;
        this.csrfToken = csrfToken;
    }
    
    public PersistencePackage() {
        //do nothing
    }

    @Override
    public Property findProperty(String name) {
        return entity.findProperty(name);
    }

    @Override
    public Property[] getProperties() {
        return entity.getProperties();
    }

    @Override
    public Map<String, Property> getPMap() {
        return entity.getPMap();
    }

    public String getCeilingEntityFullyQualifiedClassname() {
        return ceilingEntityFullyQualifiedClassname;
    }
    
    public void setCeilingEntityFullyQualifiedClassname(
            String ceilingEntityFullyQualifiedClassname) {
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
    }
    
    public String getSecurityCeilingEntityFullyQualifiedClassname() {
        if (StringUtils.isBlank(securityCeilingEntityFullyQualifiedClassname)) {
            return ceilingEntityFullyQualifiedClassname;
        }
        return securityCeilingEntityFullyQualifiedClassname;
    }

    public void setSecurityCeilingEntityFullyQualifiedClassname(
            String securityCeilingEntityFullyQualifiedClassname) {
        this.securityCeilingEntityFullyQualifiedClassname = securityCeilingEntityFullyQualifiedClassname;
    }

    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }
    
    public void setPersistencePerspective(
            PersistencePerspective persistencePerspective) {
        this.persistencePerspective = persistencePerspective;
    }
    
    public String[] getCustomCriteria() {
        return customCriteria;
    }
    
    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    public void addCustomCriteria(String criteria) {
        customCriteria = ArrayUtils.add(customCriteria, criteria);
    }

    public void removeCustomCriteria(String criteria) {
        int pos = getCriteriaIndex(criteria);
        if (pos >= 0) {
            customCriteria = ArrayUtils.remove(customCriteria, pos);
        }
    }

    public int getCriteriaIndex(String criteria) {
        return ArrayUtils.indexOf(customCriteria, criteria);
    }

    public boolean containsCriteria(String criteria) {
        return ArrayUtils.contains(customCriteria, criteria);
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getFetchTypeFullyQualifiedClassname() {
        return fetchTypeFullyQualifiedClassname;
    }

    public void setFetchTypeFullyQualifiedClassname(String fetchTypeFullyQualifiedClassname) {
        this.fetchTypeFullyQualifiedClassname = fetchTypeFullyQualifiedClassname;
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

    public Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems() {
        if (persistencePerspective != null) {
            return persistencePerspective.getPersistencePerspectiveItems();
        }
        return new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
    }

    public Map<String, PersistencePackage> getSubPackages() {
        return subPackages;
    }

    public void setSubPackages(Map<String, PersistencePackage> subPackages) {
        this.subPackages = subPackages;
    }

    public boolean isValidateUnsubmittedProperties() {
        return validateUnsubmittedProperties;
    }

    public void setValidateUnsubmittedProperties(boolean validateUnsubmittedProperties) {
        this.validateUnsubmittedProperties = validateUnsubmittedProperties;
    }

    public SectionCrumb[] getSectionCrumbs() {
        return sectionCrumbs;
    }

    public void setSectionCrumbs(SectionCrumb[] sectionCrumbs) {
        this.sectionCrumbs = sectionCrumbs;
    }

    public SectionCrumb getClosetCrumb(String myCeiling) {
        if (ArrayUtils.isEmpty(sectionCrumbs)) {
            return new SectionCrumb();
        } else {
            SectionCrumb previous = sectionCrumbs[sectionCrumbs.length-1];
            for (SectionCrumb sectionCrumb : sectionCrumbs) {
                if (sectionCrumb.getSectionIdentifier().equals(myCeiling)) {
                    break;
                } else {
                    previous = sectionCrumb;
                }
            }
            return previous;
        }
    }

    public SectionCrumb getBottomCrumb() {
        if (ArrayUtils.isEmpty(sectionCrumbs)) {
            return new SectionCrumb();
        }
        return sectionCrumbs[sectionCrumbs.length-1];
    }

    public SectionCrumb getTopCrumb() {
        if (ArrayUtils.isEmpty(sectionCrumbs)) {
            return new SectionCrumb();
        }
        return sectionCrumbs[0];
    }

    /**
     * Retrieve any PersistencePackages that should be executed after the current PersistencePackage is fully
     * processed. These packages are arranged according to the CRUD operation that should be performed.
     *
     * @return
     */
    public Map<ChangeType, List<PersistencePackage>> getDeferredOperations() {
        return deferredOperations;
    }

    public void setDeferredOperations(Map<ChangeType, List<PersistencePackage>> deferredOperations) {
        this.deferredOperations = deferredOperations;
    }

    public void addDeferredOperation(ChangeType changeType, PersistencePackage persistencePackage) {
        List<PersistencePackage> changes;
        if (!deferredOperations.containsKey(changeType)) {
            changes = new ArrayList<PersistencePackage>();
            deferredOperations.put(changeType, changes);
        } else {
            changes = deferredOperations.get(changeType);
        }
        changes.add(persistencePackage);
    }

    /**
     * Internally used field when passing the persistence package through the admin pipeline
     *
     * @return whether or not this persistence package has been exposed to a internal processing step
     */
    public boolean isProcessedInternal() {
        return isProcessedInternal;
    }

    /**
     * Internally used field when passing the persistence package through the admin pipeline
     *
     * @param isProcessedInternal whether or not this persistence package has been exposed to a internal processing step
     */
    public void setProcessedInternal(boolean isProcessedInternal) {
        this.isProcessedInternal = isProcessedInternal;
    }

    public boolean isTreeCollection() {
        return isTreeCollection;
    }

    public void setIsTreeCollection(boolean isTreeCollection) {
        this.isTreeCollection = isTreeCollection;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersistencePackage{");
        sb.append("ceilingEntityFullyQualifiedClassname='").append(ceilingEntityFullyQualifiedClassname).append('\'');
        sb.append(", securityCeilingEntityFullyQualifiedClassname='").append
                (securityCeilingEntityFullyQualifiedClassname).append('\'');
        sb.append(", sectionEntityField='").append(sectionEntityField).append('\'');
        sb.append(", fetchTypeFullyQualifiedClassname='").append(fetchTypeFullyQualifiedClassname).append('\'');
        sb.append(", persistencePerspective=").append(persistencePerspective);
        sb.append(", customCriteria=").append(Arrays.toString(customCriteria));
        sb.append(", entity=").append(entity);
        sb.append(", csrfToken='").append(csrfToken).append('\'');
        sb.append(", requestingEntityName='").append(requestingEntityName).append('\'');
        sb.append(", subPackages=").append(subPackages);
        sb.append(", validateUnsubmittedProperties=").append(validateUnsubmittedProperties);
        sb.append(", sectionCrumbs=").append(Arrays.toString(sectionCrumbs));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        PersistencePackage that = (PersistencePackage) o;

        if (ceilingEntityFullyQualifiedClassname != null ? !ceilingEntityFullyQualifiedClassname.equals(that
                .ceilingEntityFullyQualifiedClassname) : that.ceilingEntityFullyQualifiedClassname != null)
            return false;
        if (csrfToken != null ? !csrfToken.equals(that.csrfToken) : that.csrfToken != null) return false;
        if (!Arrays.equals(customCriteria, that.customCriteria)) return false;
        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (fetchTypeFullyQualifiedClassname != null ? !fetchTypeFullyQualifiedClassname.equals(that
                .fetchTypeFullyQualifiedClassname) : that.fetchTypeFullyQualifiedClassname != null)
            return false;
        if (persistencePerspective != null ? !persistencePerspective.equals(that.persistencePerspective) : that
                .persistencePerspective != null)
            return false;
        if (sectionEntityField != null ? !sectionEntityField.equals(that.sectionEntityField) : that
                .sectionEntityField != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ceilingEntityFullyQualifiedClassname != null ? ceilingEntityFullyQualifiedClassname.hashCode() : 0;
        result = 31 * result + (sectionEntityField != null ? sectionEntityField.hashCode() : 0);
        result = 31 * result + (fetchTypeFullyQualifiedClassname != null ? fetchTypeFullyQualifiedClassname.hashCode
                () : 0);
        result = 31 * result + (persistencePerspective != null ? persistencePerspective.hashCode() : 0);
        result = 31 * result + (customCriteria != null ? Arrays.hashCode(customCriteria) : 0);
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        result = 31 * result + (csrfToken != null ? csrfToken.hashCode() : 0);
        return result;
    }
}

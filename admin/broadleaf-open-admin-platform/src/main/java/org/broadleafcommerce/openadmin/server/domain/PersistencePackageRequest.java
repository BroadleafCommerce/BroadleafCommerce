package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DTO class used to seed a persistence package.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PersistencePackageRequest {

    protected Type type;
    protected String className;
    protected String configKey;
    protected AdornedTargetList adornedList;
    protected MapStructure mapStructure;
    protected Entity entity;

    // These properties are accessed via getters and setters that operate on arrays.
    // We back them with a list so that we can have the convenience .add methods
    protected List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
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

    public PersistencePackageRequest withClassName(String className) {
        setClassName(className);
        return this;
    }

    public PersistencePackageRequest withForeignKeys(ForeignKey[] foreignKeys) {
        setForeignKeys(foreignKeys);
        return this;
    }

    public PersistencePackageRequest withConfigKey(String configKey) {
        setConfigKey(configKey);
        return this;
    }

    public PersistencePackageRequest withFilterAndSortCriteria(FilterAndSortCriteria[] filterAndSortCriteria) {
        setFilterAndSortCriteria(filterAndSortCriteria);
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
        setCustomCriteria(customCriteria);
        return this;
    }

    public PersistencePackageRequest withEntity(Entity entity) {
        setEntity(entity);
        return this;
    }

    /* *********** */
    /* ADD METHODS */
    /* *********** */

    public PersistencePackageRequest addForeignKey(ForeignKey foreignKey) {
        foreignKeys.add(foreignKey);
        return this;
    }

    public PersistencePackageRequest addCustomCriteria(String customCriteria) {
        this.customCriteria.add(customCriteria);
        return this;
    }

    public PersistencePackageRequest addFilterAndSortCriteria(FilterAndSortCriteria filterAndSortCriteria) {
        this.filterAndSortCriteria.add(filterAndSortCriteria);
        return this;
    }

    /* ************************ */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************ */

    public ForeignKey[] getForeignKeys() {
        ForeignKey[] arr = new ForeignKey[this.foreignKeys.size()];
        arr = this.foreignKeys.toArray(arr);
        return arr;
    }

    public void setForeignKeys(ForeignKey[] foreignKeys) {
        this.foreignKeys = Arrays.asList(foreignKeys);
    }

    public String[] getCustomCriteria() {
        String[] arr = new String[this.customCriteria.size()];
        arr = this.customCriteria.toArray(arr);
        return arr;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = Arrays.asList(customCriteria);
    }

    public FilterAndSortCriteria[] getFilterAndSortCriteria() {
        FilterAndSortCriteria[] arr = new FilterAndSortCriteria[this.filterAndSortCriteria.size()];
        arr = this.filterAndSortCriteria.toArray(arr);
        return arr;
    }

    public void setFilterAndSortCriteria(FilterAndSortCriteria[] filterAndSortCriteria) {
        this.filterAndSortCriteria = Arrays.asList(filterAndSortCriteria);
    }

    /* ************************** */
    /* STANDARD GETTERS / SETTERS */
    /* ************************** */

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

}

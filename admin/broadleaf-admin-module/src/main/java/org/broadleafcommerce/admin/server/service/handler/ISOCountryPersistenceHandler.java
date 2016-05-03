/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.EmptyFilterValues;
import org.broadleafcommerce.openadmin.server.service.persistence.module.PersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * By default, we will filter all ISOCountries to return only those that have names.
 * (i.e. the International Standards Organization has officially assigned the 2 character alpha code to a country or region)
 * @see {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blISOCountryPersistenceHandler")
public class ISOCountryPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(ISOCountryPersistenceHandler.class);

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            Class testClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            return ISOCountry.class.isAssignableFrom(testClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto,
                                  DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        FilterMapping filterMapping = new FilterMapping()
            .withFieldPath(new FieldPath().withTargetProperty("name"))
            .withDirectFilterValues(new EmptyFilterValues())
            .withRestriction(new Restriction()
                .withPredicateProvider(new PredicateProvider<Character, Character>() {
                    @Override
                    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, 
                            From root, String ceilingEntity, String fullPropertyName, Path<Character> explicitPath, 
                            List<Character> directValues) {
                        return builder.isNotNull(explicitPath);
                    }
                })
            );
        cto.getAdditionalFilterMappings().add(filterMapping);
        
        FilterMapping countryRestrictionMapping = new FilterMapping()
            .withDirectFilterValues(new EmptyFilterValues())
            .withRestriction(new Restriction()
                .withPredicateProvider(new PredicateProvider<Character, Character>() {
                    @Override
                    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, 
                            From root, String ceilingEntity, String fullPropertyName, Path<Character> explicitPath, 
                            List<Character> directValues) {
                        CriteriaQuery<Serializable> criteria = fieldPathBuilder.getCriteria();
                        
                        Root<CountryImpl> blcCountry = criteria.from(CountryImpl.class);
                        Predicate join = builder.equal(
                            root.get("alpha2").as(String.class), 
                            blcCountry.get("abbreviation").as(String.class)
                        );
                        
                        return join;
                    }
                })
            );
        cto.getAdditionalFilterMappings().add(countryRestrictionMapping);

        PersistenceModule myModule = helper.getCompatibleModule(persistencePackage.getPersistencePerspective().getOperationTypes().getFetchType());
        return myModule.fetch(persistencePackage, cto);
    }
}

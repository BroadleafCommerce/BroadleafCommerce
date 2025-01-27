/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.dao.SkuMediaDao;
import org.broadleafcommerce.core.catalog.domain.OrderedSkuMediaXref;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import jakarta.annotation.Resource;

@Service("blSkuMediaService")
public class SkuMediaServiceImpl implements SkuMediaService {

    @Resource(name = "blSkuMediaDao")
    protected SkuMediaDao skuMediaDao;

    @Autowired
    protected Environment env;

    @Override
    @Transactional
    public SkuMediaXref save(SkuMediaXref skuMediaXref) {
        return skuMediaDao.save(skuMediaXref);
    }

    @Override
    public List<SkuMediaXref> findSkuMediaBySkuId(Long skuId) {
        List<SkuMediaXref> skuMediaXrefs = skuMediaDao.readSkuMediaBySkuId(skuId);

        if (isOrderedSkuMediaEnabled()) {
            skuMediaXrefs = sort(skuMediaXrefs);
        }

        return skuMediaXrefs;
    }

    protected List<SkuMediaXref> sort(List<SkuMediaXref> skuMediaXrefs) {
        skuMediaXrefs.sort(Comparator.comparing(xref -> ((OrderedSkuMediaXref) xref).getDisplayOrder()));

        return skuMediaXrefs;
    }

    protected boolean isOrderedSkuMediaEnabled() {
        return env.getProperty("sku.media.display-order.enabled", boolean.class, false);
    }

}

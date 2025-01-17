/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.admin.server.service.extension.AdminCatalogServiceExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.util.BLCCollectionUtils;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Phillip Verheyden
 *
 */
@Service("blAdminCatalogService")
public class AdminCatalogServiceImpl implements AdminCatalogService {

    private static final Log LOG = LogFactory.getLog(AdminCatalogServiceImpl.class);

    public static String NO_SKUS_GENERATED_KEY = "noSkusGenerated";
    public static String MAX_SKU_GENERATION_KEY = "maxSkuGenerated";
    public static String NO_PRODUCT_OPTIONS_GENERATED_KEY = "noProductOptionsConfigured";
    public static String FAILED_SKU_GENERATION_KEY = "errorNeedAllowedValue";
    public static String NUMBER_SKUS_GENERATED_KEY = "numberSkusGenerated";
    public static String INCONSISTENT_PERMUTATIONS_KEY = "inconsistentPermutations";

    @Value("${product.sku.generation.max:400}")
    protected int skuMaxGeneration;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name = "blAdminCatalogServiceExtensionManager")
    protected AdminCatalogServiceExtensionManager extensionManager;

    @Override
    public Integer generateSkusFromProduct(Long productId) {
        Product product = catalogService.findProductById(productId);

        if (CollectionUtils.isEmpty(product.getProductOptionXrefs())) {
            return -1;
        }

        List<List<ProductOptionValue>> allPermutations = generatePermutations(0, new ArrayList<ProductOptionValue>(), product.getProductOptions());

        // return -2 to indicate that one of the Product Options used in Sku generation has no Allowed Values
        if (allPermutations == null) {
            return -2;
        }

        LOG.info("Total number of permutations: " + allPermutations.size());
        LOG.debug(allPermutations);

        //determine the permutations that I already have Skus for
        List<List<ProductOptionValue>> previouslyGeneratedPermutations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(product.getAdditionalSkus())) {
            for (Sku additionalSku : product.getAdditionalSkus()) {
                if (CollectionUtils.isNotEmpty(additionalSku.getProductOptionValues())) {
                    previouslyGeneratedPermutations.add(additionalSku.getProductOptionValues());
                }
            }
        }

        List<List<ProductOptionValue>> permutationsToGenerate = new ArrayList<>();
        for (List<ProductOptionValue> permutation : allPermutations) {
            boolean previouslyGenerated = false;
            for (List<ProductOptionValue> generatedPermutation : previouslyGeneratedPermutations) {
                if (isSamePermutation(permutation, generatedPermutation)) {
                    previouslyGenerated = true;
                    break;
                }
            }

            if (!previouslyGenerated) {
                permutationsToGenerate.add(permutation);
            }
        }

        List<List<ProductOptionValue>> inconsistentGeneratedPermutations =
                checkForInconsistentPermutations(allPermutations, previouslyGeneratedPermutations);

        if (CollectionUtils.isNotEmpty(inconsistentGeneratedPermutations)) {
            return 0;
        }

        LOG.info("Total number of permutations to generate: " + permutationsToGenerate.size());

        int numPermutationsCreated = 0;
        if (extensionManager != null && CollectionUtils.isNotEmpty(permutationsToGenerate)) {
            ExtensionResultHolder<Integer> result = new ExtensionResultHolder<>();
            ExtensionResultStatusType resultStatusType = extensionManager.getProxy().persistSkuPermutation(product, permutationsToGenerate, result);
            if (ExtensionResultStatusType.HANDLED == resultStatusType) {
                numPermutationsCreated = result.getResult();
            }
        }

        LOG.info("Total number of permutations generated: " + numPermutationsCreated);
        return numPermutationsCreated;
    }

    @Override
    public Map<String, Object> generateSkus(Long productId) {
        Map<String, Object> result = new HashMap<>();

        Product product = catalogService.findProductById(productId);

        if (CollectionUtils.isEmpty(product.getProductOptionXrefs())) {
            result.put("message", BLCMessageUtils.getMessage(NO_PRODUCT_OPTIONS_GENERATED_KEY));
            return result;
        }

        if (this.checkSkuMaxGeneration(product.getProductOptions())) {
            result.put("message", String.format(BLCMessageUtils.getMessage(MAX_SKU_GENERATION_KEY), skuMaxGeneration));
            return result;
        }

        List<List<ProductOptionValue>> allPermutations = generatePermutations(0, new ArrayList<>(), product.getProductOptions());

        if (allPermutations == null) {
            // one of the Product Options used in Sku generation has no Allowed Values
            result.put("message", BLCMessageUtils.getMessage(FAILED_SKU_GENERATION_KEY));
            result.put("error", "no-allowed-value-error");
            return result;
        }

        LOG.info("Total number of permutations: " + allPermutations.size());
        LOG.debug(allPermutations);

        //determine the permutations that I already have Skus for
        List<List<ProductOptionValue>> previouslyGeneratedPermutations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(product.getAdditionalSkus())) {
            for (Sku additionalSku : product.getAdditionalSkus()) {
                if (CollectionUtils.isNotEmpty(additionalSku.getProductOptionValues())) {
                    previouslyGeneratedPermutations.add(additionalSku.getProductOptionValues());
                }
            }
        }

        List<List<ProductOptionValue>> permutationsToGenerate = new ArrayList<>();
        for (List<ProductOptionValue> permutation : allPermutations) {
            boolean previouslyGenerated = false;
            for (List<ProductOptionValue> generatedPermutation : previouslyGeneratedPermutations) {
                if (isSamePermutation(permutation, generatedPermutation)) {
                    previouslyGenerated = true;
                    break;
                }
            }

            if (!previouslyGenerated) {
                permutationsToGenerate.add(permutation);
            }
        }

        List<List<ProductOptionValue>> inconsistentGeneratedPermutations =
                checkForInconsistentPermutations(allPermutations, previouslyGeneratedPermutations);

        if (CollectionUtils.isNotEmpty(inconsistentGeneratedPermutations)) {
            result.put("message", BLCMessageUtils.getMessage(INCONSISTENT_PERMUTATIONS_KEY));
            return result;
        }

        LOG.info("Total number of permutations to generate: " + permutationsToGenerate.size());

        int numPermutationsCreated = 0;
        if (extensionManager != null && CollectionUtils.isNotEmpty(permutationsToGenerate)) {
            ExtensionResultHolder<Integer> resultHolder = new ExtensionResultHolder<>();
            ExtensionResultStatusType resultStatusType = extensionManager.getProxy().persistSkuPermutation(product, permutationsToGenerate, resultHolder);
            if (ExtensionResultStatusType.HANDLED == resultStatusType) {
                numPermutationsCreated = resultHolder.getResult();
            }
        }

        if (numPermutationsCreated == 0) {
            result.put("message", BLCMessageUtils.getMessage(NO_SKUS_GENERATED_KEY));
        }
        LOG.info("Total number of permutations generated: " + numPermutationsCreated);

        result.put("message", numPermutationsCreated + " " + BLCMessageUtils.getMessage(NUMBER_SKUS_GENERATED_KEY));
        result.put("skusGenerated", numPermutationsCreated);
        return result;
    }

    protected List<List<ProductOptionValue>> checkForInconsistentPermutations(
            List<List<ProductOptionValue>> allPermutations,
            List<List<ProductOptionValue>> previouslyGeneratedPermutations) {
        List<List<ProductOptionValue>> inconsistentGeneratedPermutations = new ArrayList<>();
        for (List<ProductOptionValue> generatedPermutation : previouslyGeneratedPermutations) {
            boolean inconsistentPermutations = true;
            for (List<ProductOptionValue> permutation : allPermutations) {
                if (isSamePermutation(permutation, generatedPermutation)) {
                    inconsistentPermutations = false;
                }
            }

            if (inconsistentPermutations) {
                inconsistentGeneratedPermutations.add(generatedPermutation);
            }
        }
        return inconsistentGeneratedPermutations;
    }

    protected boolean checkSkuMaxGeneration(List<ProductOption> productOptions) {
        boolean beyondAvailable = false;
        int count = productOptions.stream()
                .filter(ProductOption::getUseInSkuGeneration)
                .map(ProductOption::getAllowedValues)
                .mapToInt(List::size)
                .reduce(1, Math::multiplyExact);;
        if (count > skuMaxGeneration) {
            beyondAvailable = true;
        }
        return beyondAvailable;
    }

    protected boolean isSamePermutation(List<ProductOptionValue> perm1, List<ProductOptionValue> perm2) {
        if (perm1.size() == perm2.size()) {
            Collection<Long> perm1Ids = BLCCollectionUtils.collect(perm1, input -> ((ProductOptionValue) input).getId());
            Collection<Long> perm2Ids = BLCCollectionUtils.collect(perm2, input -> ((ProductOptionValue) input).getId());
            return perm1Ids.containsAll(perm2Ids);
        }
        return false;
    }

    /**
     * Generates all the possible permutations for the combinations of given ProductOptions
     * @param currentTypeIndex
     * @param currentPermutation
     * @param options
     * @return a list containing all of the possible combinations of ProductOptionValues based on grouping by the ProductOptionValue
     */
    public List<List<ProductOptionValue>> generatePermutations(
            int currentTypeIndex,
            List<ProductOptionValue> currentPermutation,
            List<ProductOption> options
    ) {
        List<List<ProductOptionValue>> result = new ArrayList<>();
        if (currentTypeIndex == options.size()) {
            if (!currentPermutation.isEmpty()) {
                result.add(currentPermutation);
            }
            return result;
        }

        ProductOption currentOption = options.get(currentTypeIndex);
        List<ProductOptionValue> allowedValues = currentOption.getAllowedValues();
        if (!currentOption.getUseInSkuGeneration()) {
            // This flag means do not generate skus and so do not create permutations for this ProductOption,
            // end it here and return the current list of permutations.
            result.addAll(generatePermutations(currentTypeIndex + 1, currentPermutation, options));
            return result;
        }
        // Check to make sure there is at least 1 Allowed Value, else prevent generation
        if (currentOption.getAllowedValues().isEmpty()) {
            return null;
        }
        for (ProductOptionValue option : allowedValues) {
            List<ProductOptionValue> permutation = new ArrayList<>(currentPermutation);
            permutation.add(option);
            result.addAll(generatePermutations(currentTypeIndex + 1, permutation, options));
        }
        if (allowedValues.size() == 0) {
            // There are still product options left in our array to compute permutations, even though this ProductOption does not have any values associated.
            result.addAll(generatePermutations(currentTypeIndex + 1, currentPermutation, options));
        }

        return result;
    }

    @Override
    public Boolean cloneProduct(Long productId) {
        Product cloneProduct = catalogService.findProductById(productId);
        //initialize the many-to-many to save off
        cloneProduct.getProductOptions().size();
        cloneProduct.getAllParentCategories().size();

        //Detach and save a cloned Sku
        Sku cloneSku = cloneProduct.getDefaultSku();
        cloneSku.getSkuMedia().size();
        em.detach(cloneSku);
        cloneSku.setId(null);

        cloneProduct.setDefaultSku(cloneSku);

        em.detach(cloneProduct);
        cloneProduct.setId(null);
        Product derivedProduct = catalogService.saveProduct(cloneProduct);

        cloneProduct = catalogService.findProductById(productId);
        //Re-associate the new Skus to the new Product
        for (Sku additionalSku : cloneProduct.getAdditionalSkus()) {
            additionalSku.getProductOptionValues().size();
            em.detach(additionalSku);
            additionalSku.setId(null);
            additionalSku.setProduct(derivedProduct);
            catalogService.saveSku(additionalSku);
        }
        return true;
    }
    
}

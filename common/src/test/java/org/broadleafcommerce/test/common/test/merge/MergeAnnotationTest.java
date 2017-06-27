/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.test.common.test.merge;

import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.common.extensibility.context.merge.MergeAnnotationAwareBeanDefinitionRegistryPostProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

/**
 * Some verification tests around {@literal @}Merge {@literal @}AliasFor
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringRunner.class)
public class MergeAnnotationTest {

    @Configuration
    public static class Config1 {
        @Bean
        public static MergeAnnotationAwareBeanDefinitionRegistryPostProcessor mergeProcessor() {
            return new MergeAnnotationAwareBeanDefinitionRegistryPostProcessor();
        }
        @Bean
        public List<String> list1() {
            List<String> modifiableList = new ArrayList<>();
            modifiableList.add("config1");
            return modifiableList;
        }
    }
    
    @Configuration
    public static class Config2 {
        @Merge("list1")
        public List<String> config2() {
            return Arrays.asList("config2");
        }
    }
    
    @Configuration
    public static class Config3 {
        @Merge(targetRef = "list1")
        public List<String> config3() {
            return Arrays.asList("config3");
        }
    }
    
    @Resource(name = "list1")
    public List<String> list1;
    
    @Test
    public void listsMergedTest() {
        Assert.assertTrue(list1.contains("config2"));
        Assert.assertTrue(list1.contains("config3"));
    }
}

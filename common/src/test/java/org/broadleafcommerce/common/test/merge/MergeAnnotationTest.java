/**
 * 
 */
package org.broadleafcommerce.common.test.merge;

import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.common.extensibility.context.merge.MergeAnnotationAwareBeanDefinitionRegistryPostProcessor;
import org.broadleafcommerce.common.test.merge.MergeAnnotationTest.Config1;
import org.broadleafcommerce.common.test.merge.MergeAnnotationTest.Config2;
import org.broadleafcommerce.common.test.merge.MergeAnnotationTest.Config3;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
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

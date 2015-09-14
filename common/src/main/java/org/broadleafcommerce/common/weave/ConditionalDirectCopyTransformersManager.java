package org.broadleafcommerce.common.weave;

/**
 * Responsible for determining if an entity has been conditionally enabled for loadtime weaving for the enterprise and/or
 * multitenant modules. The primary utility of this class is to allow conditional inclusion of additional fields important
 * to these modules to a domain class. Since this behavior requires explicit action by an implementation's codebase, fixes that
 * require schema changes can safely be introduced in a patch release stream.
 * </p>
 * Setup inside a Broadleaf Commerce module is generally performed in a manner similar to this example:
 * {@code
      <bean id="blCommonConditionalDirectCopyTransformerMember" class="org.broadleafcommerce.common.weave.ConditionalDirectCopyTransformMemberDto">
         <property name="templateTokens">
             <util:constant static-field="org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes.MULTITENANT_SITE"/>
         </property>
         <property name="conditionalProperty" value="enable.site.map.mt.disc"/>
     </bean>

     <bean id="blCommonConditionalDirectCopyTransformers" class="org.springframework.beans.factory.config.MapFactoryBean">
         <property name="sourceMap">
             <map>
                 <entry key="org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl" value-ref="blCommonConditionalDirectCopyTransformerMember"/>
             </map>
         </property>
     </bean>

     <bean class="org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor">
         <property name="collectionRef" value="blCommonConditionalDirectCopyTransformers"/>
         <property name="targetRef" value="blConditionalDirectCopyTransformers"/>
     </bean>
 * }
 * The goal is to add configuration for one or more entities and then add that configuration to the "blConditionalDirectCopyTransformers" map in
 * Spring. The activity of this configuration will remain dormant until the "conditionalProperty" is defined and set to true in the implementation's
 * Spring property files (or override property file). At that point, based on the type of templateTokens defined, Hibernate will
 * expect to find and utilize the new columns in the database associated with those templateTokens.
 *
 * @author Jeff Fischer
 */
public interface ConditionalDirectCopyTransformersManager {

    /**
     * Based on the entity name, this method will determine if the associated conditionalProperty from the Spring configuration is true.
     *
     * @param entityName
     * @return
     */
    Boolean isEntityEnabled(String entityName);

    /**
     * Retrieve the direct copy transform config info
     *
     * @param entityName
     * @return
     */
    ConditionalDirectCopyTransformMemberDto getTransformMember(String entityName);

}

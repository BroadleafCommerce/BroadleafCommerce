package org.broadleafcommerce.core.config;

import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyClassTransformer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Configuration
public class CoreConfig {

    @ConditionalOnProperty(name = "sku.media.display-order.enabled")
    @Merge(targetRef = "blMergedClassTransformers", early = true)
    public List<DirectCopyClassTransformer> blCustomerSegmentContentTestTransformers() {
        return Collections.singletonList(new DirectCopyClassTransformer("Ordered Sku Media Xrefs")
                .addXformTemplate("org.broadleafcommerce.core.catalog.domain.SkuMediaXrefImpl",
                        "org.broadleafcommerce.core.catalog.domain.weave.WeaveOrderedSkuMediaXrefImpl"));
    }

}

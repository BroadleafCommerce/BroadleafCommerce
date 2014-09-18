package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.media.domain.Media;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface SkuMediaXref extends Serializable {

    Long getId();

    void setId(Long id);

    Sku getSku();

    void setSku(Sku sku);

    Media getMedia();

    void setMedia(Media media);

    String getKey();

    void setKey(String key);

}

package org.broadleafcommerce.common.media.domain;

public interface MediaXref extends Media {

    Media getMedia();

    void setMedia(Media media);

    String getKey();

    void setKey(String key);

}

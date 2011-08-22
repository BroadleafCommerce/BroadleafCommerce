package org.broadleafcommerce.cms.file.domain;

/**
 * Created by bpolster.
 */
public interface StaticAssetData {
    public Long getId();

    public void setId(Long id);

    public byte[] getData();

    public void setData(byte[] data);
}

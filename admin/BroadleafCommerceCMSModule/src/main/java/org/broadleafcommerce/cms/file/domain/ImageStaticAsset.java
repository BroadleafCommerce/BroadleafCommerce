package org.broadleafcommerce.cms.file.domain;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageStaticAsset extends StaticAsset {

    public Integer getWidth();

    public void setWidth(Integer width);

    public Integer getHeight();

    public void setHeight(Integer height);
}

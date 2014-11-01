/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.cms.file.domain;

import java.sql.Blob;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StaticAssetStorage {

    Long getId();

    void setId(Long id);

    Blob getFileData();

    void setFileData(Blob fileData);

    public Long getStaticAssetId();

    public void setStaticAssetId(Long staticAssetId);

}

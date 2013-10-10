/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.sitemap.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * SiteMapGenerators are typically unaware of what the actual file they need to write to and depend on this utility 
 * to return them the file.
 * 
 * @author bpolster
 */
public interface SiteMapUtility {

    /**
     * Returns a file to write the next part of the SiteMap.   If passed in fileName is null, assumes that 
     * the fileName is the first file being written to. 
     * 
     */
    public OutputStream getSiteMapOutputStream(OutputStream currentOutputStream, int currentFileCount) throws IOException;

}

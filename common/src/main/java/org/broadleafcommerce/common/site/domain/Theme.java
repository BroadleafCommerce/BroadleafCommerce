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

package org.broadleafcommerce.common.site.domain;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface Theme extends Serializable {
    
    public String getName();

    public void setName(String name);

    /**
     * The display name for a site.  Returns blank if no theme if no path is available.   Should return
     * a path that does not start with "/" and that ends with a "/".   For example, "store/".
     * @return
     */
    public String getPath();

    /**
     * Sets the path of the theme.
     * @param path
     */
    public void setPath(String path);
}

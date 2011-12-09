/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.extensibility.cache.ehcache;

import org.broadleafcommerce.profile.extensibility.context.ResourceInputStream;
import org.broadleafcommerce.profile.extensibility.context.merge.MergeXmlConfigResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public class MergeEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean {

    public void setConfigLocations(List<Resource> configLocations) throws BeansException {
        try {
            MergeXmlConfigResource merge = new MergeXmlConfigResource();
            ResourceInputStream[] sources = new ResourceInputStream[configLocations.size()];
            int j=0;
            for (Resource resource : configLocations) {
                sources[j] = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
                j++;
            }
            setConfigLocation(merge.getMergedConfigResource(sources));
        } catch (IOException e) {
            throw new FatalBeanException("Unable to merge cache locations", e);
        }
    }
}

/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.common.util;

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class SortedListFactoryBean extends ListFactoryBean {

    @Override
    protected List createInstance() {
        List response = super.createInstance();
        Collections.sort(response, new Comparator<Ordered>() {
            @Override
            public int compare(Ordered o1, Ordered o2) {
                return new Integer(o1.getOrder()).compareTo(o2.getOrder());
            }
        });

        return response;
    }
}

/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.browsertest.page

/**
 * Common class shared between classes that are using an {@link EntityForm}-like representation for showing a form to a
 * user. Another consumer of this is an {@link AdornedTargetPage} which is not <i>really</i> a pure {@link EntityForm} but
 * has similar form capabilities nonetheless (specifically, how fields are resolved on the page)
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class FieldConverterSupport {

    /**
     * Converts the given field name into its selector representation. For instance, if you pass in 'defaultSku.name' this
     * will give you back input\[name="fields[\\'defaultSku__name\\'\].value"]. This selector can then be used to
     * find the field on the form.
     * @param fieldName a potentially dot-separated field name on the form
     * @return a JQuery selector string
     */
    def String convertFieldName(String fieldName) {
        // not hardcoding input[] to work with textarea as well
        '[name="fields\\[\\\'' + fieldName.replaceAll('\\.', '__') + '\\\'\\].value"]'
    }
    
}

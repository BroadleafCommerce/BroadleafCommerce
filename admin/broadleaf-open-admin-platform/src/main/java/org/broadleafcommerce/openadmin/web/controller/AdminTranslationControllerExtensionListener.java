/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.openadmin.web.form.TranslationForm;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminTranslationControllerExtensionListener {
    
    /**
     * Applies any necessary transformations to the given form. For example, some entity fields might need to be 
     * mapped in a different way.
     * 
     * @param form
     * @return whether or not a transformation was applied
     */
    public boolean applyTransformation(TranslationForm form);

}

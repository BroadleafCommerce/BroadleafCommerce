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
package org.broadleafcommerce.common.presentation.override;

/**
 * List of property types that correlate directly to the annotation properties available in
 * {@link org.broadleafcommerce.common.presentation.AdminPresentation}.
 *
 * @author Jeff Fischer
 */
public enum AdminPresentationPropertyType {
    friendlyName,securityLevel,order,gridOrder,visibility,fieldType,group,groupOrder,groupCollapsed,tab,tabOrder,
    largeEntry,prominent,columnWidth,broadleafEnumeration,requiredOverride,excluded,tooltip,helpText,hint,
    showIfProperty,currencyCodeField,ruleIdentifier,readOnly
}

/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.presentation.client;

/**
 * Define whether a new collection member is searched for or constructed.
 *
 * @author Jeff Fischer
 */
public enum AddMethodType {

    //Collection member created from a normal add form dialog
    PERSIST,

    //Persist an empty entity first before presenting the user with an "edit" stage form. It's up to the implementation
    //to bypass validation, account for non-nullable db fields, etc... (if applicable). This is useful for collection
    //members that themselves have collections.
    PERSIST_EMPTY,

    //Collection member chosen from a lookup listgrid dialog
    LOOKUP,

    //Collection member chosen from a selectize dropdown
    SELECTIZE_LOOKUP,

    //Collection members can be updated via a lookup listgrid dialog
    LOOKUP_FOR_UPDATE

}

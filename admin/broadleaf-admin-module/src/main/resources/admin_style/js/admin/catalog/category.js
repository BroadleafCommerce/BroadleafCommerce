/*
 * #%L
 * BroadleafCommerce Admin Module
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
$(document).ready(function() {
    
    $('body').on('click', 'button.show-category-list-view', function() {
        $('.category-list').show();
        $('.category-tree').hide();
        $('button.show-category-list-view').addClass('active');
        $('button.show-category-tree-view').removeClass('active');
    });
    
    $('body').on('click', 'button.show-category-tree-view', function() {
        $('.category-list').hide();
        $('.category-tree').show();
        $('button.show-category-list-view').removeClass('active');
        $('button.show-category-tree-view').addClass('active');
    });

    $('body').on('click', 'button.show-category-tree-view-modal', function() {
        var currentUrl = window.location.href;
        currentUrl = BLCAdmin.history.getUrlWithParameter('tree', true, null, currentUrl);
        BLCAdmin.showLinkAsModal(currentUrl);
        return false;
    });
    
});

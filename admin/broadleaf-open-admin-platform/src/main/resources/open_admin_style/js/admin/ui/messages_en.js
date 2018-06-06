/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
(function($, BLCAdmin) {
    
    BLCAdmin.messages = {
        // List grid messages
        saved : 'Saved',
        reorder : 'Reorder',
        done : 'Done',
        updated : 'Updated',
        deleted : 'Deleted',
        duplicated : 'Duplicated',
        
        // Rule builder messages
        rule : 'Rule',
        subCondition : 'Sub-Condition',
        entireCondition : 'Entire Condition',
        booleanTrue : 'True',
        booleanFalse : 'False',
        
        // Asset messages
        selectUploadAsset : 'Select / Upload Asset',
        
        // Modal messages
        error : 'Error',
        forbidden403 : '403 Forbidden',
        staleContent : '409 The request could not be completed due to a conflict with the current state of the target resource, likely due to stale state. Please Refresh.',
        errorOccurred : 'An error occurred',
        loading : 'Loading',
        
        // Session timer messages
        sessionCountdown: 'Your session expires in <span>',
        sessionCountdownEnd: '</span> seconds',

        problemSaving : 'There was a problem saving. See errors below',
        problemDeleting : 'There was a problem deleting this record. See errors below',
        problemDuplicating : 'There was a problem duplicating this record. See errors below',
        problemReverting : 'There was a problem reverting this record.',
        globalErrors : 'Global Errors',

        // Media attributes modal messages
        primaryMediaAttrsFormTitle : 'Update primary media attrs',
        primaryMediaAttrsTitle : 'Title',
        primaryMediaAttrsAltText : 'Alt Text',
        primaryMediaAttrsTags : 'Tags',
        primaryMediaAttrsBtnApply : 'Apply',
    };
            
})(jQuery, BLCAdmin);

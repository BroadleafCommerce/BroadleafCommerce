////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.automation.tabularData
{

import mx.automation.AutomationManager;
import mx.automation.IAutomationObject;
import mx.automation.IAutomationTabularData;
import mx.collections.CursorBookmark;
import mx.collections.errors.ItemPendingError;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.Tree;
import mx.core.mx_internal;
use namespace mx_internal;

/**
 *  @private
 */
public class TreeTabularData extends ListTabularData
{

    private var tree:Tree;

    /**
     *  Constructor
     */
    public function TreeTabularData(l:Tree)
    {
		super(l);

        tree = l;
    }

    /**
     *  @inheritDoc
     */
    override public function get numRows():int
    {
        return tree.collectionLength;
    }

}
}

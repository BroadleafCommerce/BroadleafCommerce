////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.binding
{

import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.IViewCursor;
import mx.core.mx_internal;
import mx.events.CollectionEvent;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class RepeaterItemWatcher extends Watcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Constructor.
	 */
    public function RepeaterItemWatcher(dataProviderWatcher:PropertyWatcher)
    {
		super();

        this.dataProviderWatcher = dataProviderWatcher;
    }

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    private var dataProviderWatcher:PropertyWatcher;

	/**
	 *  @private
	 */
    private var clones:Array;

	/**
	 *  @private
	 */
    private var original:Boolean = true;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Watcher
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
    override public function updateParent(parent:Object):void
    {
        var dataProvider:ICollectionView;

        if (dataProviderWatcher)
        {
            dataProvider = ICollectionView(dataProviderWatcher.value);
            if (dataProvider != null)
            {
                dataProvider.removeEventListener(CollectionEvent.COLLECTION_CHANGE, changedHandler, false);
            }
        }

        dataProviderWatcher = PropertyWatcher(parent);
        dataProvider = ICollectionView(dataProviderWatcher.value);

        if (dataProvider)
        {
            if (original)
            {
                dataProvider.addEventListener(CollectionEvent.COLLECTION_CHANGE, changedHandler, false, 0, true);
                updateClones(dataProvider);
            }
            else
            {
                wrapUpdate(function():void
                {
                    var iterator:IViewCursor = dataProvider.createCursor();
                    iterator.seek(CursorBookmark.FIRST, cloneIndex);
                    value = iterator.current;
                    updateChildren();
                });
            }
        }
    }

    /**
     *  @private
     *  Handles "Change" events sent by calls to Collection APIs
     *  on the Repeater's dataProvider.
     */
    private function changedHandler(collectionEvent:CollectionEvent):void
    {
        var dataProvider:ICollectionView = ICollectionView(dataProviderWatcher.value);

        if (dataProvider)
            updateClones(dataProvider);
    }

	/**
	 *  @private
	 */
    override protected function shallowClone():Watcher
    {
        return new RepeaterItemWatcher(dataProviderWatcher);
    }

	/**
	 *  @private
	 */
    private function updateClones(dataProvider:ICollectionView):void
    {
        if (clones)
            clones = clones.splice(0, dataProvider.length);
        else
            clones = [];

        for (var i:int = 0; i < dataProvider.length; i++)
        {
            var clone:RepeaterItemWatcher = RepeaterItemWatcher(clones[i]);
                
            if (!clone)
            {
                clone = RepeaterItemWatcher(deepClone(i));
                clone.original = false;
                clones[i] = clone;
            }

            clone.updateParent(dataProviderWatcher);
        }
    }
}

}

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

import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class ArrayElementWatcher extends Watcher
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 *  Constructor
	 */
    public function ArrayElementWatcher(document:Object,
                                        accessorFunc:Function,
                                        listeners:Array)
    {
		super(listeners);

        this.document = document;
        this.accessorFunc = accessorFunc;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
	private var document:Object;
    
    /**
	 *  @private
	 */
	private var accessorFunc:Function;

    /**
	 *  @private
	 */
    public var arrayWatcher:Watcher;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

    /**
	 *  @private
	 */
    override public function updateParent(parent:Object):void
    {
        if (arrayWatcher.value != null)
        {
            wrapUpdate(function():void
			{
				value = arrayWatcher.value[accessorFunc.apply(document)];
				updateChildren();
			});
        }
    }

    /**
	 *  @private
	 */
    override protected function shallowClone():Watcher
    {
        return new ArrayElementWatcher(document, accessorFunc, listeners);
    }
}

}

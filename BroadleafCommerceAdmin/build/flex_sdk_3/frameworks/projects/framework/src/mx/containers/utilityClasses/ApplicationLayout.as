////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers.utilityClasses
{

import mx.core.Container;
import mx.core.EdgeMetrics;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 *  The ApplicationLayout class is for internal use only.
 */
public class ApplicationLayout extends BoxLayout
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function ApplicationLayout()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Lay out children as per Application layout rules.
	 */
	override public function updateDisplayList(unscaledWidth:Number,
											   unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);
		
		var target:Container = super.target;

		// If there are scrollbars, and any children are at negative
		// co-ordinates, make adjustments to bring them into the visible area.
		if ((target.horizontalScrollBar && getHorizontalAlignValue() > 0) ||
			(target.verticalScrollBar && getVerticalAlignValue() > 0))
		{
			var paddingLeft:Number = target.getStyle("paddingLeft");
			var paddingTop:Number = target.getStyle("paddingTop");
			var oX:Number = 0;
			var oY:Number = 0;

			var n:int = target.numChildren;
			var i:int;
			var child:IFlexDisplayObject;

			for (i = 0; i < n; i++)
			{
				child = IFlexDisplayObject(target.getChildAt(i));

				if (child.x < paddingLeft)
					oX = Math.max(oX, paddingLeft - child.x);

				if (child.y < paddingTop)
					oY = Math.max(oY, paddingTop - child.y);
			}

			if (oX != 0 || oY != 0)
			{
				for (i = 0; i < n; i++)
				{
					child = IFlexDisplayObject(target.getChildAt(i));
					child.move(child.x + oX, child.y + oY);
				}
			}
		}
	}
}

}

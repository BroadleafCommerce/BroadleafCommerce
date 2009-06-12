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

package mx.containers.utilityClasses
{

import mx.core.Container;
import mx.core.IUIComponent;
import mx.core.FlexVersion;

[ExcludeClass]

/**
 *  @private
 *  The Flex class is for internal use only.
 */
public class Flex
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  This function sets the width of each child
	 *  so that the widths add up to spaceForChildren.
	 *  Each child is set to its preferred width
	 *  if its percentWidth is zero.
	 *  If it's percentWidth is a positive number
	 *  the child grows depending on the size of its parent
	 *  The height of each child is set to its preferred height.
	 *  The return value is any extra space that's left over
	 *  after growing all children to their maxWidth.
	 *
	 *  @param parent The parent container of the children.
	 *
	 *  @param spaceForChildren The space that is to be
	 *  distributed across all the children.
	 *
	 *  @param h height for all children.
	 *
	 *  @result Any extra space that's left over
	 *  after growing all children to their maxWidth.
	 */
	public static function flexChildWidthsProportionally(
								parent:Container,
								spaceForChildren:Number,
								h:Number):Number
	{
		var spaceToDistribute:Number = spaceForChildren;
		var totalPercentWidth:Number = 0;
		var childInfoArray:Array = [];
		var childInfo:FlexChildInfo;
		var child:IUIComponent;
		var i:int;

		// If the child is flexible, store information about it in the
		// childInfoArray. For non-flexible children, just set the child's
		// width and height immediately.
		//
		// Also calculate the sum of all widthFlexes, and calculate the 
		// sum of the width of all non-flexible children.
		var n:int = parent.numChildren;
		for (i = 0; i < n; i++)
		{
			child = IUIComponent(parent.getChildAt(i));

			var percentWidth:Number = child.percentWidth;
			var percentHeight:Number = child.percentHeight;
			var height:Number;
			
			if (!isNaN(percentHeight) && child.includeInLayout)
			{
				height = Math.max(child.minHeight,
					Math.min(child.maxHeight,
					((percentHeight >= 100) ? h : h * percentHeight / 100)));
			}
			else
			{
				height = child.getExplicitOrMeasuredHeight();
			}
			
			if (!isNaN(percentWidth) && child.includeInLayout)
			{
				totalPercentWidth += percentWidth;

				childInfo = new FlexChildInfo();
				childInfo.percent = percentWidth;
				childInfo.min = child.minWidth;
				childInfo.max = child.maxWidth;
				childInfo.height = height;
				childInfo.child = child;
				
				childInfoArray.push(childInfo);
			}
			else
			{
				var width:Number = child.getExplicitOrMeasuredWidth();
				// if scaled and zoom is playing, best to let the sizes be non-integer
				// otherwise the rounding creates an error that accumulates in some components like List
				if (child.scaleX == 1 && child.scaleY == 1)
				{
					child.setActualSize(Math.floor(width),
										Math.floor(height));
				}
				else
				{
					child.setActualSize(width, height);
				}

				if (child.includeInLayout)
				{
					// Need to account for the actual child width since 
					// setActualSize may trigger a Resize effect, which 
					// could change the size of the component.
					spaceToDistribute -= child.width;
				}
			}
		}

		// Distribute the extra space among the children.
		if (totalPercentWidth)
		{
			spaceToDistribute = flexChildrenProportionally(spaceForChildren,
				spaceToDistribute, totalPercentWidth, childInfoArray);

			// Set the widths and heights of the flexible children
			n = childInfoArray.length;
			for (i = 0; i < n; i++)
			{
				childInfo = childInfoArray[i];
				child = childInfo.child;			

				// if scaled and zoom is playing, best to let the sizes be non-integer
				// otherwise the rounding creates an error that accumulates in some components like List
				if (child.scaleX == 1 && child.scaleY == 1)
				{
					child.setActualSize(Math.floor(childInfo.size),
										Math.floor(childInfo.height));
				}
				else
				{
					child.setActualSize(childInfo.size, childInfo.height);
				}
			}
			
			if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        	{
            	distributeExtraWidth(parent, spaceForChildren);
        	}
		}

		return spaceToDistribute;
	}

	/**
	 *  This function sets the height of each child
	 *  so that the heights add up to spaceForChildren. 
	 *  Each child is set to its preferred height
	 *  if its percentHeight is zero.
	 *  If its percentHeight is a positive number,
	 *  the child grows (or shrinks) to consume its share of extra space.
	 *  The width of each child is set to its preferred width.
	 *  The return value is any extra space that's left over
	 *  after growing all children to their maxHeight.
	 *
	 *  @param parent The parent container of the children.
	 *
	 *  @param spaceForChildren The space that is to be 
	 *  distributed across all children .
	 *
	 *  @param w width for all children.
	 */
	public static function flexChildHeightsProportionally(
								parent:Container,
								spaceForChildren:Number,
								w:Number):Number
	{
		var spaceToDistribute:Number = spaceForChildren;
		var totalPercentHeight:Number = 0;
		var childInfoArray:Array = [];
		var childInfo:FlexChildInfo;
		var child:IUIComponent;
		var i:int;

		// If the child is flexible, store information about it in the
		// childInfoArray. For non-flexible children, just set the child's
		// width and height immediately.
		//
		// Also calculate the sum of all percentHeights, and calculate the 
		// sum of the height of all non-flexible children.
		var n:int = parent.numChildren;
		for (i = 0; i < n; i++)
		{
			child = IUIComponent(parent.getChildAt(i));

			var percentWidth:Number = child.percentWidth;
			var percentHeight:Number = child.percentHeight;
			var width:Number;
			
			if (!isNaN(percentWidth) && child.includeInLayout)
			{
				width = Math.max(child.minWidth,
					Math.min(child.maxWidth,
					((percentWidth >= 100) ? w : w * percentWidth / 100)));
			}
			else
			{
				width = child.getExplicitOrMeasuredWidth();
			}
		
			if (!isNaN(percentHeight) && child.includeInLayout)
			{
				totalPercentHeight += percentHeight;

				childInfo = new FlexChildInfo();
				childInfo.percent = percentHeight;
				childInfo.min = child.minHeight;
				childInfo.max = child.maxHeight;
				childInfo.width = width;
				childInfo.child = child;
				
				childInfoArray.push(childInfo);
			}
			else
			{
				var height:Number = child.getExplicitOrMeasuredHeight();
				// if scaled and zoom is playing, best to let the sizes be non-integer
				// otherwise the rounding creates an error that accumulates in some components like List
				if (child.scaleX == 1 && child.scaleY == 1)
				{
					child.setActualSize(Math.floor(width),
										Math.floor(height));
				}
				else
				{
					child.setActualSize(width, height);
				}

				if (child.includeInLayout)
				{
					// Need to account for the actual child height since 
					// setActualSize may trigger a Resize effect, which 
					// could change the size of the component.
					spaceToDistribute -= child.height;
				}
			}
		}

		// Distribute the extra space among the children.
		if (totalPercentHeight)
		{
			spaceToDistribute = flexChildrenProportionally(spaceForChildren,
				spaceToDistribute, totalPercentHeight, childInfoArray);

			// Set the widths and heights of the flexible children
			n = childInfoArray.length;
			for (i = 0; i < n; i++)
			{
				childInfo = childInfoArray[i];
				child = childInfo.child;			

				// if scaled and zoom is playing, best to let the sizes be non-integer
				// otherwise the rounding creates an error that accumulates in some components like List
				if (child.scaleX == 1 && child.scaleY == 1)
				{
					child.setActualSize(Math.floor(childInfo.width),
										Math.floor(childInfo.size));
				}
				else
				{
					child.setActualSize(childInfo.width, childInfo.size);
				}
			}
			
			if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        	{
            	distributeExtraHeight(parent, spaceForChildren);
        	}
		}
		
		return spaceToDistribute;
	}

	/**
	 *  This function distributes excess space among the flexible children.
	 *  It does so with a view to keep the children's overall size
	 *  close the ratios specified by their percent.
	 *
	 *  @param spaceForChildren The total space for all children
	 *
	 *  @param spaceToDistribute The space that needs to be distributed
	 *  among the flexible children.
	 *
	 *  @param childInfoArray An array of Objects. When this function
	 *  is called, each object should define the following properties:
	 *  - percent: the percentWidth or percentHeight of the child (depending
	 *  on whether we're growing in a horizontal or vertical direction)
	 *  - min: the minimum width (or height) for that child
	 *  - max: the maximum width (or height) for that child
	 *
	 *  @return When this function finishes executing, a "size" property
	 *  will be defined for each child object. The size property contains
	 *  the portion of the spaceToDistribute to be distributed to the child.
	 *  Ideally, the sum of all size properties is spaceToDistribute.
	 *  If all the children hit their minWidth/maxWidth/minHeight/maxHeight
	 *  before the space was distributed, then the remaining unused space
	 *  is returned. Otherwise, the return value is zero.
	 */
	public static function flexChildrenProportionally(
								spaceForChildren:Number,
								spaceToDistribute:Number,
								totalPercent:Number,
								childInfoArray:Array):Number
	{
		// The algorithm iterivately attempts to break down the space that 
		// is consumed by "flexible" containers into ratios that are related
		// to the percentWidth/percentHeight of the participating containers.
		
		var numChildren:int = childInfoArray.length;
		var flexConsumed:Number; // space consumed by flexible compontents
		var done:Boolean;
		
		// We now do something a little tricky so that we can 
		// support partial filling of the space. If our total
		// percent < 100% then we can trim off some space.
		var unused:Number = spaceToDistribute -
							(spaceForChildren * totalPercent / 100);
		if (unused > 0)
			spaceToDistribute -= unused;

		// Continue as long as there are some remaining flexible children.
		// The "done" flag isn't strictly necessary, except that it catches
		// cases where round-off error causes totalPercent to not exactly
		// equal zero.
		do
		{
			flexConsumed = 0; // space consumed by flexible compontents
			done = true; // we are optimistic
			
			// Space for flexible children is the total amount of space
			// available minus the amount of space consumed by non-flexible
			// components.Divide that space in proportion to the percent
			// of the child
			var spacePerPercent:Number = spaceToDistribute / totalPercent;
			
			// Attempt to divide out the space using our percent amounts,
			// if we hit its limit then that control becomes 'non-flexible'
			// and we run the whole space to distribute calculation again.
			for (var i:int = 0; i < numChildren; i++)
			{
				var childInfo:FlexChildInfo = childInfoArray[i];

				// Set its size in proportion to its percent.
				var size:Number = childInfo.percent * spacePerPercent;

				// If our flexiblity calc say grow/shrink more than we are
				// allowed, then we grow/shrink whatever we can, remove
				// ourselves from the array for the next pass, and start
				// the loop over again so that the space that we weren't
				// able to consume / release can be re-used by others.
				if (size < childInfo.min)
				{
					var min:Number = childInfo.min;
					childInfo.size = min;
					
					// Move this object to the end of the array
					// and decrement the length of the array. 
					// This is slightly expensive, but we don't expect
					// to hit these min/max limits very often.
					childInfoArray[i] = childInfoArray[--numChildren];
					childInfoArray[numChildren] = childInfo;

					totalPercent -= childInfo.percent;
					spaceToDistribute -= min;
					done = false;
					break;
				}
				else if (size > childInfo.max)
				{
					var max:Number = childInfo.max;
					childInfo.size = max;

					childInfoArray[i] = childInfoArray[--numChildren];
					childInfoArray[numChildren] = childInfo;

					totalPercent -= childInfo.percent;
					spaceToDistribute -= max;
					done = false;
					break;
				}
				else
				{
					// All is well, let's carry on...
					childInfo.size = size;
					flexConsumed += size;
				}
			}
		} 
		while (!done);

		return Math.max(0, Math.floor(spaceToDistribute - flexConsumed))
	}
	
	/**
	 *  This function distributes excess space among the flexible children
	 *  because of rounding errors where we want to keep children's dimensions 
	 *  full pixel amounts.  This only distributes the extra space 
	 *  if there was some rounding down and there are still 
	 *  flexible children.
	 *
	 *  @param parent The parent container of the children.
	 * 
	 *  @param spaceForChildren The total space for all children
	 */
	public static function distributeExtraHeight(
								parent:Container,
								spaceForChildren:Number):void
	{
		// We should only get here after distributing the majority of the 
		// space already.  This is done in flexChildHeightsProportionally.
		// Strategy here is to keep adding 1 pixel at a time to each 
		// component that's flexible (percentHeight defined and hasn't
		// reached maxHeight yet).  We could use another approach where
		// we add more than a pixel at a time, but we'd have to first 
		// calculate exactly how many flexible components we have first
		// and see how much space we can add to them without hitting
		// their maxHeight.  Since we're just dealing with rounding 
		// issues, we should only make one pass here (if we hit maxHeight
		// problems, we might make more than one, but not many more).
		
		// We just distribute from the top-down and don't care about 
		// who was "rounded down the most"
		
		// First check if we should distribute any extra space.  To do 
		// this, we check to see if someone suffers from rounding error.
		var n:int = parent.numChildren;
		var wantToGrow:Boolean = false;
		var i:int;
		var percentHeight:Number;
		var spaceToDistribute:Number = spaceForChildren;
		var spaceUsed:Number = 0;
		var child:IUIComponent;
		var childHeight:Number;	
		var wantSpace:Number;
		
		for (i = 0; i < n; i++)
		{
			child = IUIComponent(parent.getChildAt(i));
			
			if (!child.includeInLayout)
				continue;
				
			childHeight = child.height;
			percentHeight = child.percentHeight;
			
			spaceUsed += childHeight;
			
			if (!isNaN(percentHeight))
			{
				wantSpace = Math.ceil(percentHeight/100 * spaceForChildren);
				
				if (wantSpace > childHeight)
					wantToGrow = true;
			}
		}
		
		// No need to distribute extra size
		if (!wantToGrow)
			return;

		// Start distributing...
		spaceToDistribute -= spaceUsed;
		
		// If we still have components that will let us 
		// distribute to them
		var stillFlexibleComponents:Boolean = true;	
		
		while (stillFlexibleComponents && spaceToDistribute > 0)
		{
			// Start optimistically
			stillFlexibleComponents = false;
			
			for (i = 0; i < n; i++)
			{
				child = IUIComponent(parent.getChildAt(i));
				childHeight = child.height;
				percentHeight = child.percentHeight
				
				// if they have a percentHeight, and we won't reach their
				// maxHeight by giving them one more pixel, then 
				// give them a pixel
				if (!isNaN(percentHeight) && 
						child.includeInLayout && 
						childHeight < child.maxHeight)
				{
					wantSpace = Math.ceil(percentHeight/100 * spaceForChildren);
				
					if (wantSpace > childHeight)
					{
						child.setActualSize(child.width, childHeight+1);
						spaceToDistribute--;
						stillFlexibleComponents = true;
						
						if (spaceToDistribute == 0)
							return;
					}
				}
			}
		}
	}
	
	/**
	 *  This function distributes excess space among the flexible children
	 *  because of rounding errors where we want to keep children's dimensions 
	 *  full pixel amounts.  This only distributes the extra space 
	 *  if there was some rounding down and there are still 
	 *  flexible children.
	 *
	 *  @param parent The parent container of the children.
	 * 
	 *  @param spaceForChildren The total space for all children
	 */
	public static function distributeExtraWidth(
								parent:Container,
								spaceForChildren:Number):void
	{
		// We should only get here after distributing the majority of the 
		// space already.  This is done in flexChildWidthsProportionally.
		// Strategy here is to keep adding 1 pixel at a time to each 
		// component that's flexible (percentWidth defined and hasn't
		// reached maxWidth yet).  We could use another approach where
		// we add more than a pixel at a time, but we'd have to first 
		// calculate exactly how many flexible components we have first
		// and see how much space we can add to them without hitting
		// their maxWidth.  Since we're just dealing with rounding 
		// issues, we should only make one pass here (if we hit maxWidth
		// problems, we might make more than one, but not many more).
		
		// We just distribute from the top-down and don't care about 
		// who was "rounded down the most"
		
		// First check if we should distribute any extra space.  To do 
		// this, we check to see if someone suffers from rounding error.
		var n:int = parent.numChildren;
		var wantToGrow:Boolean = false;
		var i:int;
		var percentWidth:Number;
		var spaceToDistribute:Number = spaceForChildren;
		var spaceUsed:Number = 0;
		var child:IUIComponent;
		var childWidth:Number;	
		var wantSpace:Number;
		
		for (i = 0; i < n; i++)
		{
			child = IUIComponent(parent.getChildAt(i));
			
			if (!child.includeInLayout)
				continue;
				
			childWidth = child.width;
			percentWidth = child.percentWidth;
			
			spaceUsed += childWidth;
			
			if (!isNaN(percentWidth))
			{
				wantSpace = Math.ceil(percentWidth/100 * spaceForChildren);
				
				if (wantSpace > childWidth)
					wantToGrow = true;
			}
		}
		
		// No need to distribute extra size
		if (!wantToGrow)
			return;

		// Start distributing...
		spaceToDistribute -= spaceUsed;
		
		// If we still have components that will let us 
		// distribute to them
		var stillFlexibleComponents:Boolean = true;	
		
		while (stillFlexibleComponents && spaceToDistribute > 0)
		{
			// Start optimistically
			stillFlexibleComponents = false;
			
			for (i = 0; i < n; i++)
			{
				child = IUIComponent(parent.getChildAt(i));
				childWidth = child.width;
				percentWidth = child.percentWidth
				
				// if they have a percentWidth, and we won't reach their
				// maxWidth by giving them one more pixel, then 
				// give them a pixel
				if (!isNaN(percentWidth) && 
						child.includeInLayout && 
						childWidth < child.maxWidth)
				{
					wantSpace = Math.ceil(percentWidth / 100 * spaceForChildren);
				
					if (wantSpace > childWidth)
					{
						child.setActualSize(childWidth  +1, child.height);
						spaceToDistribute--;
						stillFlexibleComponents = true;
						
						if (spaceToDistribute == 0)
							return;
					}
				}
			}
		}
	}
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers.utilityClasses
{

import flash.display.DisplayObject;
import flash.geom.Rectangle;
import mx.containers.Canvas;
import mx.containers.errors.ConstraintError;
import mx.core.Container;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IConstraintClient;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.events.ChildExistenceChangedEvent;
import mx.events.MoveEvent;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.styles.StyleManager;
import flash.utils.Dictionary;

[ExcludeClass]

[ResourceBundle("containers")]

/**
 *  @private
 *  The CanvasLayout class is for internal use only.
 */
public class CanvasLayout extends Layout
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var r:Rectangle = new Rectangle();

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Restrict a number to a particular min and max.
	 */
	private function bound(a:Number, min:Number, max:Number):Number
	{
		if (a < min)
			a = min;
		else if (a > max)
			a = max;
		else
			a = Math.floor(a);

		return a;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	private var _contentArea:Rectangle;
	
	//Arrays that keep track of children spanning
	//content size columns or rows. 
	private var colSpanChildren:Array = [];
	private var rowSpanChildren:Array = [];
	
	private var constraintCache:Dictionary = new Dictionary(true);
	
	private var constraintRegionsInUse:Boolean = false;

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function CanvasLayout()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  target
	//----------------------------------

	/**
	 *  @private
	 */
	override public function set target(value:Container):void
	{
		var target:Container = super.target;
		if (value != target)
		{
			var i:int;
			var n:int;

			if (target)
			{
				// Start listening for child existence events.
				// We want to track the movement of children
				// so we can update our size every time a
				// child moves.

				target.removeEventListener(
						ChildExistenceChangedEvent.CHILD_ADD,
						target_childAddHandler);
				target.removeEventListener(
						ChildExistenceChangedEvent.CHILD_REMOVE,
						target_childRemoveHandler);

				n = target.numChildren;
				for (i = 0; i < n; i++)
				{
					DisplayObject(target.getChildAt(i)).removeEventListener(
						MoveEvent.MOVE, child_moveHandler);
				}
			}

			if (value)
			{
				value.addEventListener(
						ChildExistenceChangedEvent.CHILD_ADD,
						target_childAddHandler);
				value.addEventListener(
						ChildExistenceChangedEvent.CHILD_REMOVE,
						target_childRemoveHandler);

				n = value.numChildren;
				for (i = 0; i < n; i++)
				{
					DisplayObject(value.getChildAt(i)).addEventListener(
						MoveEvent.MOVE, child_moveHandler);
				}
			}

			super.target = value;
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Measure container as per Canvas layout rules.
	 */
	override public function measure():void
	{
		var target:Container = super.target;
		var w:Number = 0;
		var h:Number = 0;
		var i:Number = 0;
		
		var vm:EdgeMetrics = target.viewMetrics;
		
		for (i = 0; i < target.numChildren; i++)
		{
			var child:IUIComponent = target.getChildAt(i) as IUIComponent;
			parseConstraints(child);
		}
		
		//We need to NaN out content-sized columns and rows width/height values
		//so that new values are calculated correctly and we avoid stale values
		for (i = 0; i < IConstraintLayout(target).constraintColumns.length; i++)
		{
			var col:ConstraintColumn = IConstraintLayout(target).constraintColumns[i];
			if (col.mx_internal::contentSize)
				col.mx_internal::_width = NaN;
		}
		for (i = 0; i < IConstraintLayout(target).constraintRows.length; i++)
		{
			var row:ConstraintRow = IConstraintLayout(target).constraintRows[i];
			if (row.mx_internal::contentSize)
				row.mx_internal::_height = NaN;
		}
		
		measureColumnsAndRows();
		
		_contentArea = null;
		var contentArea:Rectangle = measureContentArea();
		
		// Only add viewMetrics padding
		// if children are bigger than existing size.
		target.measuredWidth = contentArea.width + vm.left + vm.right;
		target.measuredHeight = contentArea.height + vm.top + vm.bottom;
	}

	/**
	 *  @private
	 *  Lay out children as per Canvas layout rules.
	 */
	override public function updateDisplayList(unscaledWidth:Number,
											   unscaledHeight:Number):void
	{
		var i:int;
		var child:IUIComponent;
		var target:Container = super.target;	
		var n:int = target.numChildren;
		// viewMetrics include scrollbars during updateDisplayList, but not
		// during measure. In order to avoid a race condition when the 
		// scrollable area is within a scrollbar's width of the view metrics,
		// we use the non-update viewMetrics, which don't include scrollbars.
		target.mx_internal::doingLayout = false;
		var vm:EdgeMetrics = target.viewMetrics;
		target.mx_internal::doingLayout = true;
		
		var viewableWidth:Number = unscaledWidth - vm.left - vm.right;
		var viewableHeight:Number = unscaledHeight - vm.top - vm.bottom;
		
		if (IConstraintLayout(target).constraintColumns.length > 0 ||
			IConstraintLayout(target).constraintRows.length > 0)
				constraintRegionsInUse = true;
		if (constraintRegionsInUse)
		{
			for (i = 0; i < n; i++)
			{
				child = target.getChildAt(i) as IUIComponent;
				parseConstraints(child);
			}
			
			//We need to NaN out content-sized columns and rows width/height values
			//so that new values are calculated correctly and we avoid stale values
			for (i = 0; i < IConstraintLayout(target).constraintColumns.length; i++)
			{
				var col:ConstraintColumn = IConstraintLayout(target).constraintColumns[i];
				if (col.mx_internal::contentSize)
					col.mx_internal::_width = NaN;
			}
			for (i = 0; i < IConstraintLayout(target).constraintRows.length; i++)
			{
				var row:ConstraintRow = IConstraintLayout(target).constraintRows[i];
				if (row.mx_internal::contentSize)
					row.mx_internal::_height = NaN;
			}
			
			measureColumnsAndRows();
		}
		
		// Apply the CSS styles left, top, right, bottom,
		// horizontalCenter, and verticalCenter;
		// these override x, y, width, and height if specified.
		for (i = 0; i < n; i++)
		{
			child = target.getChildAt(i) as IUIComponent;
			applyAnchorStylesDuringUpdateDisplayList(viewableWidth, viewableHeight, child);
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Figure out the content area based on whether there are 
	 *  ConstraintColumn instances or ConstraintRow instances 
	 *  specified and the constraint style values. 
	 */
	private function applyAnchorStylesDuringMeasure(child:IUIComponent,
													r:Rectangle):void
	{
		var constraintChild:IConstraintClient = child as IConstraintClient;
		if (!constraintChild)
			return;
		//Calculate constraint boundaries if it has not been calculated
		//already 
		var childInfo:ChildConstraintInfo = constraintCache[constraintChild];
		if (!childInfo)
			childInfo = parseConstraints(child);
		var left:Number = childInfo.left;
		var right:Number = childInfo.right;
		var horizontalCenter:Number = childInfo.hc;
		var top:Number = childInfo.top;
		var bottom:Number = childInfo.bottom;
		var verticalCenter:Number = childInfo.vc;
		
		var cols:Array = IConstraintLayout(target).constraintColumns;
		var rows:Array = IConstraintLayout(target).constraintRows;
		
		var i:int;
		var holder:Number = 0;
		
		if (!cols.length > 0)
		{
			if (!isNaN(horizontalCenter))
			{
				r.x = Math.round((target.width - child.width) / 2 + horizontalCenter);
			}
			else if (!isNaN(left) && !isNaN(right))
			{
				r.x = left;
				r.width += right;
			}
			else if (!isNaN(left))
			{
				r.x = left;
			}
			else if (!isNaN(right))
			{
				r.x = 0;
				r.width += right;
			}
		}
		else //sum up the column widths
		{
			r.x = 0;
			for (i = 0; i < cols.length; i++)
			{
				holder += ConstraintColumn(cols[i]).width;
			}
			r.width = holder;
		}
		
		if (!rows.length > 0)
		{
			if (!isNaN(verticalCenter))
			{
				r.y = Math.round((target.height - child.height) / 2 + verticalCenter);
			}
			else if (!isNaN(top) && !isNaN(bottom))
			{
				r.y = top;
				r.height += bottom;
			}
			else if (!isNaN(top))
			{
				r.y = top;
			}
			else if (!isNaN(bottom))
			{
				r.y = 0;
				r.height += bottom;
			}
		}
		else //sum up the row heights
		{
			holder = 0;
			r.y = 0;
			for (i = 0; i < rows.length; i++)
			{
				holder += ConstraintRow(rows[i]).height;
			}
			r.height = holder;
		}
	}
	
	/**
	 *  @private
	 *  Here is a description of the layout algorithm.
	 *  It is described in terms of horizontal coordinates,
	 *  but the vertical ones are similar.
	 *
	 *  1. First the actual width for the child is determined.
	 *
	 *  1a. If both left and right anchors are specified,
	 *  the actual width is determined by them.
	 *  However, the actual width is subject to the child's
	 *  minWidth.
	 *
	 *  1b. Otherwise, if a percentWidth was specified,
	 *  this percentage is applied to the 
	 *  ConstraintColumn/Parent's content width
	 *  (the widest specified point of content, or the width of
	 *  the parent/column, whichever is greater).
	 *  The actual width is subject to the child's
	 *  minWidth and maxWidth.
	 *
	 *  1c. Otherwise, if an explicitWidth was specified,
	 *  this is used as the actual width.
	 *
	 *  1d. Otherwise, the measuredWidth is used is used as the
	 *  actual width.
	 *
	 *  2. Then the x coordinate of the child is determined.
	 *
	 *  Note:If a baseline constraint is specified, the center
	 *  of the child (y position) is placed relative to the 
	 *  ConstraintRow specified. 
	 * 
	 *  2a. If a horizonalCenter anchor is specified,
	 *  the center of the child is placed relative to the center
	 *  of the parent/column. 
	 *
	 *  2b. Otherwise, if a left anchor is specified,
	 *  the left edge of the child is placed there.
	 *
	 *  2c. Otherwise, if a right anchor is specified,
	 *  the right edge of the child is placed there.
	 *
	 *  2d. Otherwise, the child is left at its previously set
	 *  x coordinate.
	 *
	 *  3. If the width is a percentage, try to make sure it
	 *  doesn't overflow the content width (while still honoring
	 *  minWidth). We need to wait
	 *  until after the x coordinate is set to test this.
	 */
	private function applyAnchorStylesDuringUpdateDisplayList(
							availableWidth:Number,
							availableHeight:Number,
							child:IUIComponent = null):void
	{	
		var constraintChild:IConstraintClient = child as IConstraintClient;
		if (!constraintChild)
			return;
		var childInfo:ChildConstraintInfo = parseConstraints(child);
		//Variables to track the offsets
		var left:Number = childInfo.left;
		var right:Number = childInfo.right;
		var horizontalCenter:Number = childInfo.hc;
		var top:Number = childInfo.top;
		var bottom:Number = childInfo.bottom;
		var verticalCenter:Number = childInfo.vc;
		var baseline:Number = childInfo.baseline;

		//Variables to track the boundaries from which
		//the offsets are calculated from. If null, the 
		//boundary is the parent container edge. 
		var leftBoundary:String = childInfo.leftBoundary;
		var rightBoundary:String = childInfo.rightBoundary;
		var hcBoundary:String = childInfo.hcBoundary;
		var topBoundary:String = childInfo.topBoundary;
		var bottomBoundary:String = childInfo.bottomBoundary;
		var vcBoundary:String = childInfo.vcBoundary;
		var baselineBoundary:String = childInfo.baselineBoundary;

		var i:int;
		var w:Number;
		var h:Number;
		
		var x:Number;
		var y:Number;
		var message:String;
		var checkWidth:Boolean = false;
		var checkHeight:Boolean = false;
	
		//If we are to evaluate the left, right, and horizontalCenter
		//styles relative to the parent boundaries, parentBoundariesLR will
		//be true
		var parentBoundariesLR:Boolean = (!hcBoundary && !leftBoundary && !rightBoundary);
		//If we are to evaluate the top, bottom, verticalCenter and baseline
		//styles relative to the parent boundaries, parentBoundariesTB will
		//be true
		var parentBoundariesTB:Boolean = (!vcBoundary && !topBoundary && !bottomBoundary && !baselineBoundary);
	
		var leftHolder:Number = 0;
		var rightHolder:Number = availableWidth;
		var topHolder:Number = 0;
		var bottomHolder:Number = availableHeight;
		var vcHolder:Number;
		var hcHolder:Number;
		var vcY:Number;
		var hcX:Number;
		var baselineY:Number;
		//If we are not evaluating left, right, and horizontalCenter
		//relative to the parent container edges, we need to match
		//the column specified in the constraint expression with the
		//actual ConstraintColumn instance so later we can determine how
		//much space the control has to live in. 
		if (!parentBoundariesLR)
		{
			var matchLeft:Boolean = leftBoundary ? true : false;
			var matchRight:Boolean = rightBoundary ? true : false;
			var matchHC:Boolean = hcBoundary ? true : false;
			for (i = 0; i < IConstraintLayout(target).constraintColumns.length; i++)
			{		
				var col:ConstraintColumn = ConstraintColumn(IConstraintLayout(target).constraintColumns[i]);
				if (matchLeft)
				{
					if (leftBoundary == col.id)
					{
						leftHolder = col.x;
						matchLeft = false;
					}
				}	
				if (matchRight)
				{
					if (rightBoundary == col.id)
					{
						rightHolder = col.x + col.width;
						matchRight = false;
					}
				}	
				if (matchHC)
				{
					if (hcBoundary == col.id)
					{
						hcHolder = col.width;
						hcX = col.x;
						matchHC = false;
					}
				}	
			}
			//Erorr throwing - we could not match one of the boundaries to the
			//declared constraintColumns 
			if (matchLeft)
			{
				message = resourceManager.getString(
					"containers", "columnNotFound", [ leftBoundary ]);
           		throw new ConstraintError(message);
			}	
			if (matchRight)
			{
				message = resourceManager.getString(
					"containers", "columnNotFound", [ rightBoundary ]);
           		throw new ConstraintError(message);
			}
			if (matchHC)
			{
				message = resourceManager.getString(
					"containers", "columnNotFound", [ hcBoundary ]);
           		throw new ConstraintError(message);
			}
		}
		else if (!parentBoundariesLR)
		{
			//The left, right or horizontalCenter style has been set to
			//a non-parent region, but no columns were declared 
			message = resourceManager.getString(
				"containers", "noColumnsFound");
			throw new ConstraintError(message);
		}
		
		//The width of the region which
		//the control will live in. 
		availableWidth = Math.round(rightHolder - leftHolder);

		// If a percentage size is specified for a child,
		// it specifies a percentage of the parent's content size
		// minus any specified left, top, right, or bottom
		// anchors for this child.
		// Also, respect the child's minimum and maximum sizes.
		if (!isNaN(left) && !isNaN(right))
		{
			w = availableWidth - left - right;
			if (w < child.minWidth)
				w = child.minWidth;
		}
		else if (!isNaN(child.percentWidth))
		{
			w = child.percentWidth / 100 * availableWidth;
			w = bound(w, child.minWidth, child.maxWidth);
			
			checkWidth = true;
		}
		else
		{
			w = child.getExplicitOrMeasuredWidth();
		}

		//If we are not evaluating top, bottom, and verticalCenter
		//relative to the parent container edges, we need to match
		//the row specified in the constraint expression with the
		//actual ConstraintRow instance so later we can determine how
		//much space the control has to live in. 
		if (!parentBoundariesTB && IConstraintLayout(target).constraintRows.length > 0)
		{
			var matchTop:Boolean = topBoundary ? true : false;
			var matchBottom:Boolean = bottomBoundary ? true : false;
			var matchVC:Boolean = vcBoundary ? true : false;
			var matchBaseline:Boolean = baselineBoundary ? true : false;
			
			for (i = 0; i < IConstraintLayout(target).constraintRows.length; i++)
			{		
				var row:ConstraintRow = ConstraintRow(IConstraintLayout(target).constraintRows[i]);
				if (matchTop)
				{
					if (topBoundary == row.id)
					{
						topHolder = row.y;
						matchTop = false;
					}
				}	
				if (matchBottom)
				{
					if (bottomBoundary == row.id)
					{
						bottomHolder = row.y + row.height;
						matchBottom = false;
					}
				}	
				if (matchVC)
				{
					if (vcBoundary == row.id)
					{
						vcHolder = row.height;
						vcY = row.y;
						matchVC = false;
					}
				}
				if (matchBaseline)
				{
					if (baselineBoundary == row.id)
					{
						baselineY = row.y;
						matchBaseline = false;
					}
				}	
			}
			//Erorr throwing - we could not match one of the boundaries to the
			//declared constraintRows 
			if (matchTop)
			{
				message = resourceManager.getString(
					"containers", "rowNotFound", [ topBoundary ]);
           		throw new ConstraintError(message);
			}
			if (matchBottom)
			{
				message = resourceManager.getString(
					"containers", "rowNotFound", [ bottomBoundary ]);
           		throw new ConstraintError(message);
			}
			if (matchVC)
			{
				message = resourceManager.getString(
					"containers", "rowNotFound", [ vcBoundary ]);
           		throw new ConstraintError(message);
			}
			if (matchBaseline)
			{
				message = resourceManager.getString(
					"containers", "rowNotFound", [ baselineBoundary ]);
           		throw new ConstraintError(message);
			}
		}
		else if (!parentBoundariesTB && !(IConstraintLayout(target).constraintRows.length > 0))
		{
			//The top, bottom or verticalCenter style has been set to
			//a non-parent region, but no rows were declared 
			message = resourceManager.getString(
				"containers", "noRowsFound");
			throw new ConstraintError(message);
		}
		
		//The height of the region which
		//the control will live in. 
		availableHeight = Math.round(bottomHolder - topHolder);
		if (!isNaN(top) && !isNaN(bottom))
		{
			h = availableHeight - top - bottom;
			if (h < child.minHeight)
				h = child.minHeight;
		}
		else if (!isNaN(child.percentHeight))
		{
			h = child.percentHeight / 100 * availableHeight;
			h = bound(h, child.minHeight, child.maxHeight);
			
			checkHeight = true;
		}
		else
		{
			h = child.getExplicitOrMeasuredHeight();
		}
		
		// The left, right, and horizontalCenter styles 
		// affect the child's x and/or its actual width.
		if (!isNaN(horizontalCenter))
		{
			if (hcBoundary)
				x = Math.round((hcHolder - w) / 2 + horizontalCenter + hcX);
			else
				x = Math.round((availableWidth - w) / 2 + horizontalCenter);
		}
		else if (!isNaN(left))
		{
			if (leftBoundary)
				x = leftHolder + left;
			else 
				x = left;
		}
		else if (!isNaN(right))
		{
			if (rightBoundary)
				x = rightHolder - right - w;
			else 
				x = availableWidth - right - w;
		}

		// The top, bottom, verticalCenter and baseline styles
		// affect the child's y and/or its actual height.
		if (!isNaN(baseline))
		{
			if (baselineBoundary)
			{
				//trace(child.name, child.baselinePosition);
				y = (baselineY - child.baselinePosition) + baseline;
			}
			else 
				y = baseline;
		}
		if (!isNaN(verticalCenter))
		{
			if (vcBoundary)
			{
				y = Math.round((vcHolder - h) / 2 + verticalCenter + vcY);
			}
			else 
				y = Math.round((availableHeight - h) / 2 + verticalCenter);
		}
		else if (!isNaN(top))
		{
			if (topBoundary)
				y = topHolder + top;
			else
				y = top;
		}
		else if (!isNaN(bottom))
		{
			if (bottomBoundary)
				y = bottomHolder - bottom - h;
			else 
				y = availableHeight - bottom - h;
		}
		
		x = isNaN(x) ? child.x : x;
		y = isNaN(y) ? child.y : y;
		
		child.move(x, y);
		
		// One last test here. If the width/height is a percentage,
		// limit the width/height to the available content width/height, 
		// but honor the minWidth/minHeight.
		if (checkWidth)
		{
			if (x + w > availableWidth)
				w = Math.max(availableWidth - x, child.minWidth);
		}
		
		if (checkHeight)
		{
			if (y + h > availableHeight)
				h = Math.max(availableHeight - y, child.minHeight);
		}
		
		if (!isNaN(w) && !isNaN(h))
			child.setActualSize(w, h);
	}
	
	/** 
	 *  @private
	 *  This function measures the bounds of the content area.
	 *  It looks at each child included in the layout, and determines
	 *  right and bottom edge.
	 *
	 *  When we are laying out the children, we use the larger of the
	 *  content area and viewable area to determine percentages and 
	 *  the edges for constraints.
	 *  
	 *  If the child has a percentageWidth or both left and right values
	 *  set, the minWidth is used for determining its area. Otherwise
	 *  the explicit or measured width is used. The same rules apply in 
	 *  the vertical direction.
	 */
	private function measureContentArea():Rectangle
	{
		if (_contentArea)
			return _contentArea;
		var i:int;
		_contentArea = new Rectangle();
		var n:int = target.numChildren;
		
		//Special case where there are no children but there
		//are columns or rows
		if (n == 0 && constraintRegionsInUse)
		{
			var cols:Array = IConstraintLayout(target).constraintColumns;
			var rows:Array = IConstraintLayout(target).constraintRows;
			//The right of the contentArea rectangle is the x position of the last
			//column plus its width. If there are no columns, its 0.
			if (cols.length > 0)
				_contentArea.right = cols[cols.length-1].x + cols[cols.length-1].width;
			else 
				_contentArea.right = 0;
			//The bottom of the contentArea rectangle is the y position of the last row
			//plus its height. If there are no rows, its 0;
			if (rows.length > 0)
				_contentArea.bottom = rows[rows.length-1].y + rows[rows.length-1].height;
			else _contentArea.bottom = 0;
		}
		
		for (i = 0; i < n; i++)
		{
			var child:IUIComponent = target.getChildAt(i) as IUIComponent;
			var childConstraints:LayoutConstraints = getLayoutConstraints(child);
		
			if (!child.includeInLayout)
				continue;
				
			var cx:Number = child.x;
			var cy:Number = child.y;
			var pw:Number = child.getExplicitOrMeasuredWidth();
			var ph:Number = child.getExplicitOrMeasuredHeight();
			
			//Behavior check - 3.0 behavior respects explicitWidth in addition
			//to left and right constraints
			if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
			{
				if (!isNaN(child.percentWidth) ||
					(childConstraints && 
						!isNaN(childConstraints.left) && 
						!isNaN(childConstraints.right)))
				{
					pw = child.minWidth;
				}
			}
			else
			{
				if (!isNaN(child.percentWidth) ||
					(childConstraints && 
						!isNaN(childConstraints.left) && 
						!isNaN(childConstraints.right) &&
						isNaN(child.explicitWidth)))
				{
					pw = child.minWidth;
				}
			}
			//Behavior check - 3.0 behavior respects explicitHeight in addition
			//to top and bottom constraints
			if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
			{
				if (!isNaN(child.percentHeight) ||
						(childConstraints && 
							!isNaN(childConstraints.top) && 
							!isNaN(childConstraints.bottom)))
				{
					ph = child.minHeight;
				}
			}
			else 
			{
				if (!isNaN(child.percentHeight) ||
						(childConstraints && 
							!isNaN(childConstraints.top) && 
							!isNaN(childConstraints.bottom) &&
							isNaN(child.explicitHeight)))
				{
					ph = child.minHeight;
				}
			}
			r.x = cx
			r.y = cy
			r.width = pw;
			r.height = ph;
			applyAnchorStylesDuringMeasure(child, r);
			cx = r.x;
			cy = r.y;
			pw = r.width;
			ph = r.height;

			if (isNaN(cx))
				cx = child.x;
			if (isNaN(cy))
				cy = child.y;

			var rightEdge:Number = cx;
			var bottomEdge:Number = cy;

			if (isNaN(pw))
				pw = child.width;

			if (isNaN(ph))
				ph = child.height;

			rightEdge += pw;
			bottomEdge += ph;

			_contentArea.right = Math.max(_contentArea.right, rightEdge);
			_contentArea.bottom = Math.max(_contentArea.bottom, bottomEdge);
		}
		return _contentArea;
	}
	
	/** 
	 *  @private
	 */
	private function parseConstraints(child:IUIComponent = null):ChildConstraintInfo
	{
		var constraints:LayoutConstraints = getLayoutConstraints(child);
		if (!constraints)
			return null;
		//Variables to track the offsets
		var left:Number;
		var right:Number;
		var horizontalCenter:Number;
		var top:Number;
		var bottom:Number;
		var verticalCenter:Number;
		var baseline:Number;

		//Variables to track the boundaries from which
		//the offsets are calculated from. If null, the 
		//boundary is the parent container edge. 
		var leftBoundary:String;
		var rightBoundary:String;
		var hcBoundary:String;		
		var topBoundary:String;
		var bottomBoundary:String;
		var vcBoundary:String;
		var baselineBoundary:String;
 		
 		//Evaluate the constraint expression and store the offsets
 		//and boundaries.
 		var temp:Array; 
 		while (true)
 		{
 			temp = parseConstraintExp(constraints.left);
 			if (!temp)
 				left = NaN;
 			else if (temp.length == 1)
 				left = Number(temp[0]);
 			else
 			{
 				leftBoundary = temp[0];
 				left = temp[1];
 			}
	
 			temp = parseConstraintExp(constraints.right);
 			if (!temp)
 				right = NaN;
 			else if (temp.length == 1)
 				right = Number(temp[0]);
 			else
 			{
 				rightBoundary = temp[0];
 				right = temp[1];
 			}
 				
 			temp = parseConstraintExp(constraints.horizontalCenter);
 			if (!temp)
 				horizontalCenter = NaN;
 			else if (temp.length == 1)
 				horizontalCenter = Number(temp[0]);
 			else
 			{
 				hcBoundary = temp[0];
 				horizontalCenter = temp[1];
 			}
 				
 			temp = parseConstraintExp(constraints.top);
 			if (!temp)
 				top = NaN;
 			else if (temp.length == 1)
 				top = Number(temp[0]);
 			else
 			{
 				topBoundary = temp[0];
 				top = temp[1];
 			}
 				
 			temp = parseConstraintExp(constraints.bottom);
 			if (!temp)
 				bottom = NaN;
 			else if (temp.length == 1)
 				bottom = Number(temp[0]);
 			else
 			{
 				bottomBoundary = temp[0];
 				bottom = temp[1];
 			}
 				
 			temp = parseConstraintExp(constraints.verticalCenter);
 			if (!temp)
 				verticalCenter = NaN;
 			else if (temp.length == 1)
 				verticalCenter = Number(temp[0]);
 			else
 			{
 				vcBoundary = temp[0];
 				verticalCenter = temp[1];
 			}
 			temp = parseConstraintExp(constraints.baseline);
 			if (!temp)
 				baseline = NaN;
 			else if (temp.length == 1)
 				baseline = Number(temp[0]);
 			else
 			{
 				baselineBoundary = temp[0];
 				baseline = temp[1];
 			}
 
 			break;
 		}
 		
 		//Store entries for the children who span columns/rows in
 		//the colSpanChildren and rowSpanChildren arrays.
 		var i:int;
 		var colEntry:ContentColumnChild = new ContentColumnChild();
 		var pushEntry:Boolean = false;
 		var leftIndex:Number = 0;
 		var rightIndex:Number = 0;
 		var hcIndex:Number = 0;
 		
 		for (i = 0; i < IConstraintLayout(target).constraintColumns.length; i++)
 		{
 			var col:ConstraintColumn = IConstraintLayout(target).constraintColumns[i];
 			if (col.mx_internal::contentSize)
 			{
 				if (col.id == leftBoundary)
 				{
 					colEntry.leftCol = col;
 					colEntry.leftOffset = left;
 					colEntry.left = leftIndex = i;
 					pushEntry = true;
 				}
 				if (col.id == rightBoundary)
 				{
 					colEntry.rightCol = col;
 					colEntry.rightOffset = right;
 					colEntry.right = rightIndex = i + 1;
 					pushEntry = true;
 				}
 				if (col.id == hcBoundary)
 				{
 					colEntry.hcCol = col;
 					colEntry.hcOffset = horizontalCenter;
 					colEntry.hc = hcIndex = i + 1;
 					pushEntry = true;
 				}
 			}
 		}
 		
 		//Figure out the bounding columns, 
 		//span value and the child spanning and push that 
 		//information onto colSpanChildren for evaluation
 		//when measuring content sized columns
 		if (pushEntry)
 		{
	 		colEntry.child = child;
	 		if (colEntry.leftCol && !colEntry.rightCol || 
	 			colEntry.rightCol && !colEntry.leftCol ||
	 			colEntry.hcCol)
	 			{
	 				colEntry.span = 1;
	 			}
	 		else
	 			colEntry.span = rightIndex - leftIndex;
	 		
	 		//push the entry if it's not there already 
	 		var found:Boolean = false;
 			for (i = 0; i < colSpanChildren.length; i++)
 			{
 				if (colEntry.child == colSpanChildren[i].child)
 				{
 					found = true;
 					break;
 				}		
 			}
 			
 			if (!found)
 				colSpanChildren.push(colEntry);
 		}
 		pushEntry = false;
		
		var rowEntry:ContentRowChild = new ContentRowChild();
		var topIndex:Number = 0;
 		var bottomIndex:Number = 0;
 		var vcIndex:Number = 0;
 		var baselineIndex:Number = 0;
		for (i = 0; i < IConstraintLayout(target).constraintRows.length; i++)
		{
			var row:ConstraintRow = IConstraintLayout(target).constraintRows[i];
			if (row.mx_internal::contentSize)
			{
				if (row.id == topBoundary)
				{
					rowEntry.topRow = row;
					rowEntry.topOffset = top;
					rowEntry.top = topIndex = i;
					pushEntry = true;
				}
				if (row.id == bottomBoundary)
				{
					rowEntry.bottomRow = row;
					rowEntry.bottomOffset = bottom;
					rowEntry.bottom = bottomIndex = i + 1;
					pushEntry = true;
				}
				if (row.id == vcBoundary)
				{
					rowEntry.vcRow = row;
					rowEntry.vcOffset = verticalCenter;
					rowEntry.vc = vcIndex = i + 1;
					pushEntry = true;
				}
				if (row.id == baselineBoundary)
				{
					rowEntry.baselineRow = row;
					rowEntry.baselineOffset = baseline;
					rowEntry.baseline = baselineIndex = i + 1;
					pushEntry = true;
				}
			}
		}
		//Figure out the bounding rows, 
 		//span value and the child spanning and push that 
 		//information onto rowSpanChildren for evaluation
 		//when measuring content sized rows
		if (pushEntry)
		{
	 		rowEntry.child = child;
	 		if (rowEntry.topRow && !rowEntry.bottomRow || 
	 			rowEntry.bottomRow && !rowEntry.topRow ||
	 			rowEntry.vcRow || rowEntry.baselineRow)
	 			{
	 				rowEntry.span = 1;
	 			}
	 		else
	 			rowEntry.span = bottomIndex - topIndex;
	 			
	 		//push the entry if it's not there already 
 			found = false;
 			for (i = 0; i < rowSpanChildren.length; i++)
 			{
 				if (rowEntry.child == rowSpanChildren[i].child)
 				{
 					found = true;
 					break;
 				}
 			}
 			
 			if (!found)
 				rowSpanChildren.push(rowEntry);
 		}
 		//Cache constraint styles for future lookup
 		var info:ChildConstraintInfo = new ChildConstraintInfo(left, right, horizontalCenter,
												top, bottom, verticalCenter, baseline, leftBoundary, 
												rightBoundary, hcBoundary, topBoundary, bottomBoundary, 
												vcBoundary, baselineBoundary); 
		constraintCache[child] = info;												
		return info;
	}

	/** 
	 *  @private
	 *  This function measures the ConstraintColumns and 
	 *  and ConstraintRows partitioning a Canvas and sets
	 *  up their x/y positions. 
	 * 
	 *  The algorithm works like this (in the horizontal 
	 *  direction):
	 *  1. Fixed columns honor their pixel values.
	 * 
	 *  2. Content sized columns whose children span
	 *  only that column assume the width of the widest child. 
	 * 
	 *  3. Those Content sized columns that span multiple 
	 *  columns do the following:
	 *    a. Sort the children by order of how many columns they
	 *    are spanning.
	 *    b. For children spanning a single column, make each 
	 *    column as wide as the preferred size of the child.
	 *    c. For subsequent children, divide the remainder space
	 *    equally between shared columns. 
	 * 
	 *  4. Remaining space is shared between the percentage size
	 *  columns. 
	 * 
	 *  5. x positions are set based on the column widths
	 * 
	 */
	private function measureColumnsAndRows():void
	{
		var cols:Array = IConstraintLayout(target).constraintColumns;
		var rows:Array = IConstraintLayout(target).constraintRows;
		if (!rows.length > 0 && !cols.length > 0)
		{
			constraintRegionsInUse = false;
			return;
		}
		else
			constraintRegionsInUse = true;
		var i:int;
		var k:int;
		var canvasX:Number = 0;
		var canvasY:Number = 0;
		var vm:EdgeMetrics = Container(target).viewMetrics;
		var availableWidth:Number = Container(target).width - vm.left - vm.right;
		var availableHeight:Number = Container(target).height - vm.top - vm.bottom;
		var fixedSize:Array = [];
		var percentageSize:Array = [];
		var contentSize:Array = [];
		var cc:ConstraintColumn;
		var cr:ConstraintRow;
		var spaceToDistribute:Number;
		var w:Number;
		var h:Number;
		var remainingSpace:Number;

		if (cols.length > 0)
		{
			for (i = 0; i < cols.length; i++)
			{
				cc = cols[i];
				if (!isNaN(cc.percentWidth))
					percentageSize.push(cc);
				else if (!isNaN(cc.width) && !cc.mx_internal::contentSize)
					fixedSize.push(cc);
				else 
				{
					contentSize.push(cc);
					cc.mx_internal::contentSize = true;
				}
			}
			//fixed size columns 
			for (i = 0; i < fixedSize.length; i++)
			{
				cc = ConstraintColumn(fixedSize[i]);
				availableWidth = availableWidth - cc.width;
			}
			//content size columns
			if (contentSize.length > 0)
			{
				//first we figure allocate space to those columns
				//with children spanning them
				if (colSpanChildren.length > 0)
				{
					colSpanChildren.sortOn("span");
					for (k = 0; k < colSpanChildren.length; k++)
					{
						var colEntry:ContentColumnChild = colSpanChildren[k];
						//For those children that span 1 column, give that column
						//the max preferred width of the child;
						if (colEntry.span == 1)
						{	
							//Match the columns
							if (colEntry.hcCol)
								cc = ConstraintColumn(cols[cols.indexOf(colEntry.hcCol)]);
							else if (colEntry.leftCol)
								cc = ConstraintColumn(cols[cols.indexOf(colEntry.leftCol)]);
							else if (colEntry.rightCol)
								cc = ConstraintColumn(cols[cols.indexOf(colEntry.rightCol)]);			
							//Use preferred size if left and right are specified
							w = colEntry.child.getExplicitOrMeasuredWidth();
							//Now we add in offsets
							if (colEntry.hcOffset)
								w += colEntry.hcOffset;
							else 
							{
								if (colEntry.leftOffset)
									w += colEntry.leftOffset;
								if (colEntry.rightOffset)
									w += colEntry.rightOffset;
							}
							//width may have been set by a previous pass - so we want to take the max 
							if (!isNaN(cc.width))
								w = Math.max(cc.width, w);
							w = bound(w, cc.minWidth, cc.maxWidth);
							cc.setActualWidth(w);
							availableWidth -= cc.width;
						}
						//otherwise we share space amongst the spanned columns
						else
						{
							availableWidth = shareColumnSpace(colEntry, availableWidth);
						}
					}
					//reset
					colSpanChildren = [];
				}
				//now for those content size columns that don't have widths
				//give them their minWidth or 0. 
				for (i = 0; i < contentSize.length; i++)
				{
					cc = contentSize[i];
					if (!cc.width)
					{
						w = bound(0, cc.minWidth, 0);
						cc.setActualWidth(w);
					}
				}
			}
			//percentage size columns 
			remainingSpace = availableWidth;
			for (i = 0; i < percentageSize.length; i++)
			{
				cc = ConstraintColumn(percentageSize[i]);
				if (remainingSpace <= 0)
					w = 0;
				else 
					w = Math.round((remainingSpace * cc.percentWidth)/100);
				w = bound(w, cc.minWidth, cc.maxWidth);
				cc.setActualWidth(w);
				availableWidth -= w;
			}

			//In the order they were declared, set up the x positions
			for (i = 0; i < cols.length; i++)
			{
				cc = ConstraintColumn(cols[i]);
				cc.x = canvasX;
				canvasX += cc.width;
			}
		}
		
		fixedSize = [];
		percentageSize = [];
		contentSize = [];
		if (rows.length > 0)
		{
			for (i = 0; i < rows.length; i++)
			{
				cr = rows[i];
				if (!isNaN(cr.percentHeight))
				{
					percentageSize.push(cr);
				}
				else if (!isNaN(cr.height) && !cr.mx_internal::contentSize)
					fixedSize.push(cr);
				else
				{
					contentSize.push(cr);
					cr.mx_internal::contentSize = true;
				}
			}
			//fixed size rows 
			for (i = 0; i < fixedSize.length; i++)
			{
				cr = ConstraintRow(fixedSize[i]);
				availableHeight = availableHeight - cr.height;
			}
			//content size rows
			if (contentSize.length > 0)
			{
				//first we figure allocate space to those rows
				//with children spanning them
				if (rowSpanChildren.length > 0)
				{
					rowSpanChildren.sortOn("span");
					for (k = 0; k < rowSpanChildren.length; k++)
					{
						var rowEntry:ContentRowChild = rowSpanChildren[k];
						//For those children that span 1 row, give that row
						//the max preferred height of the child;
						if (rowEntry.span == 1)
						{
							//Match the rows
							if (rowEntry.vcRow)
								cr = ConstraintRow(rows[rows.indexOf(rowEntry.vcRow)]);
							else if (rowEntry.baselineRow)
								cr = ConstraintRow(rows[rows.indexOf(rowEntry.baselineRow)]);
							else if (rowEntry.topRow)
								cr = ConstraintRow(rows[rows.indexOf(rowEntry.topRow)]);
							else if (rowEntry.bottomRow)
								cr = ConstraintRow(rows[rows.indexOf(rowEntry.bottomRow)]);
							//Use preferred size if both top and bottom are specified
							h = rowEntry.child.getExplicitOrMeasuredHeight();
							//Now we add in offsets
							if (rowEntry.baselineOffset)
								h += rowEntry.baselineOffset;
							else if (rowEntry.vcOffset)
								h += rowEntry.vcOffset;
							else 
							{
								if (rowEntry.topOffset)
									h += rowEntry.topOffset;
								if (rowEntry.bottomOffset)
									h += rowEntry.bottomOffset;
							}
							//height may have been set by a previous pass - so we want to take the max 
							if (!isNaN(cr.height))
								h = Math.max(cr.height, h);
							h = bound(h, cr.minHeight, cr.maxHeight);
							cr.setActualHeight(h);
							availableHeight -= cr.height;
						}
						//otherwise we share space amongst the spanned rows
						else
						{
							availableHeight = shareRowSpace(rowEntry, availableHeight);
						}
					}
					//reset
					rowSpanChildren = [];
				}
				//now for those content size rows that don't have heights
				//give them their minHeight or 0. 
				for (i = 0; i < contentSize.length; i++)
				{
					cr = ConstraintRow(contentSize[i]);
					if (!cr.height)
					{
						h = bound(0, cr.minHeight, 0);
						cr.setActualHeight(h);
					}
				}
			}
			//percentage size rows 
			remainingSpace = availableHeight;
			for (i = 0; i < percentageSize.length; i++)
			{
				cr = ConstraintRow(percentageSize[i]);
				if (remainingSpace <= 0)
					h = 0;
				else 
					h = Math.round((remainingSpace * cr.percentHeight)/100);
				h = bound(h, cr.minHeight, cr.maxHeight);
				cr.setActualHeight(h);
				availableHeight -= h;
			}
			//In the order they were declared, set up the y positions
			for (i = 0; i < rows.length; i++)
			{
				cr = rows[i];
				cr.y = canvasY;
				canvasY += cr.height;
			}
		}
	}

	/**
	 *  @private
	 *  Shares available space between content-size columns that have content
	 *  spanning them.
	 */
	private function shareColumnSpace(entry:ContentColumnChild, availableWidth:Number):Number
	{
		var leftCol:ConstraintColumn = entry.leftCol;
		var rightCol:ConstraintColumn = entry.rightCol;
		var child:IUIComponent = entry.child;
		var leftWidth:Number = 0;
		var rightWidth:Number = 0;
		var right:Number = entry.rightOffset ? entry.rightOffset : 0;
		var left:Number = entry.leftOffset ? entry.leftOffset : 0;
		
		if (leftCol && leftCol.width)
			leftWidth += leftCol.width;
		else if (rightCol && !leftCol)
		{
			leftCol = IConstraintLayout(target).constraintColumns[entry.right - 2];
			if (leftCol && leftCol.width)
				leftWidth += leftCol.width;
		}
		if (rightCol && rightCol.width)
			rightWidth += rightCol.width;
		else if (leftCol && !rightCol)
		{
			rightCol = IConstraintLayout(target).constraintColumns[entry.left + 1];
			if (rightCol && rightCol.width)
				rightWidth += rightCol.width;
		}

		if (leftCol && isNaN(leftCol.width))
		{
			leftCol.setActualWidth(Math.max(0, leftCol.maxWidth));
 		}
 		if (rightCol && isNaN(rightCol.width))
 		{
			rightCol.setActualWidth(Math.max(0, rightCol.maxWidth));
 		}	

		var childWidth:Number = child.getExplicitOrMeasuredWidth();
		if (childWidth)
		{
			var tempLeftWidth:Number;
			var tempRightWidth:Number;
			if (!entry.leftCol)
			{
				if (childWidth > leftWidth)
					tempRightWidth = childWidth - leftWidth + right;
				else
					tempRightWidth = childWidth + right;
			}
			if (!entry.rightCol)
			{
				if (childWidth > rightWidth)
					tempLeftWidth = childWidth - rightWidth + left;
				else
					tempLeftWidth = childWidth + left;
			}
			if (entry.leftCol && entry.rightCol)
			{
				var share:Number = childWidth/Number(entry.span);
				if ((share + left) < leftWidth)
				{
					tempLeftWidth = leftWidth;
					tempRightWidth = (childWidth - (leftWidth - left)) + right;
				}
				else 
 					tempLeftWidth = share + left;
				if ((share + right) < rightWidth)
				{
					tempRightWidth = rightWidth;
					tempLeftWidth = (childWidth - (rightWidth - right)) + left;
				}
				else
					tempRightWidth = share + right;
			}	
			//set the left
			tempLeftWidth = bound(tempLeftWidth, leftCol.minWidth, leftCol.maxWidth);
			leftCol.setActualWidth(tempLeftWidth);
			availableWidth -= tempLeftWidth;
			//set the right
			tempRightWidth = bound(tempRightWidth, rightCol.minWidth, rightCol.maxWidth);
			rightCol.setActualWidth(tempRightWidth);
			availableWidth -= tempRightWidth;
		}
		return availableWidth;
	}
	
	/**
	 *  @private
	 *  Shares available space between content-size rows that have content
	 *  spanning them.
	 */
	private function shareRowSpace(entry:ContentRowChild, availableHeight:Number):Number
	{
		var topRow:ConstraintRow = entry.topRow;
		var bottomRow:ConstraintRow = entry.bottomRow;
		var child:IUIComponent = entry.child;
		var topHeight:Number = 0;
		var bottomHeight:Number = 0;
		var top:Number = entry.topOffset ? entry.topOffset : 0;
		var bottom:Number = entry.bottomOffset ? entry.bottomOffset : 0;
		
		if (topRow && topRow.height)
			topHeight += topRow.height;
		else if (bottomRow && !topRow)
		{
			topRow = IConstraintLayout(target).constraintRows[entry.bottom - 2];
			if (topRow && topRow.height)
				topHeight += topRow.height;
		}
		if (bottomRow && bottomRow.height)
			bottomHeight += bottomRow.height;
		else if (topRow && !bottomRow)
		{
			bottomRow = IConstraintLayout(target).constraintRows[entry.top + 1];
			if (bottomRow && bottomRow.height)
				bottomHeight += bottomRow.height;
		}
		if (topRow && isNaN(topRow.height))
		{
			topRow.setActualHeight(Math.max(0, topRow.maxHeight));
  		}
    	if (bottomRow && isNaN(bottomRow.height))
    	{
    		bottomRow.setActualHeight(Math.max(0, bottomRow.height));	
    	}

		var childHeight:Number = child.getExplicitOrMeasuredHeight();
		if (childHeight)
		{
			var tempTopHeight:Number;
			var tempBtmHeight:Number;
			if (!entry.topRow)
			{
				if (childHeight > topHeight)
					tempBtmHeight = childHeight - topHeight + bottom;
				else
					tempBtmHeight = childHeight + bottom;
			}
			if (!entry.bottomRow)
			{
				if (childHeight > bottomHeight)
					tempTopHeight = childHeight - bottomHeight + top;
				else
					tempTopHeight = childHeight + top;
			}
			if (entry.topRow && entry.bottomRow)
			{
				var share:Number = childHeight/Number(entry.span);
				if ((share + top) < topHeight)
				{
					tempTopHeight = topHeight;
					tempBtmHeight = (childHeight - (topHeight - top)) + bottom;
				}
				else 
 					tempTopHeight = share + top;
				if ((share + bottom) < bottomHeight)
				{
					tempBtmHeight = bottomHeight;
					tempTopHeight = (childHeight - (bottomHeight - bottom)) + top;
				}
				else 
 					tempBtmHeight = share + bottom;
			}	
			//set the bottom
			tempBtmHeight = bound(tempBtmHeight, bottomRow.minHeight, bottomRow.maxHeight);
			bottomRow.setActualHeight(tempBtmHeight);
			availableHeight -= tempBtmHeight;
			//set the top 
			tempTopHeight = bound(tempTopHeight, topRow.minHeight, topRow.maxHeight);
			topRow.setActualHeight(tempTopHeight);
			availableHeight -= tempTopHeight;
		}
		return availableHeight;
	}

	/**
	 *  @private
	 *  Collect all the layout constraints for this child and package
	 *  into a LayoutConstraints object.
	 *  Returns null if the child is not an IConstraintClient.
	 */
	private function getLayoutConstraints(child:IUIComponent):LayoutConstraints
	{
		var constraintChild:IConstraintClient = child as IConstraintClient;
		
		if (!constraintChild)
			return null;
			
		var constraints:LayoutConstraints = new LayoutConstraints();
		
		constraints.baseline = constraintChild.getConstraintValue("baseline");
		constraints.bottom = constraintChild.getConstraintValue("bottom");
		constraints.horizontalCenter = constraintChild.getConstraintValue("horizontalCenter");
		constraints.left = constraintChild.getConstraintValue("left");
		constraints.right = constraintChild.getConstraintValue("right");
		constraints.top = constraintChild.getConstraintValue("top");
		constraints.verticalCenter = constraintChild.getConstraintValue("verticalCenter");
		
		return constraints;
	}
	
	/**
	 *  @private
	 *  Parses a constraint expression, like left="col1:10" 
	 *  so that an array is returned where the first value is
	 *  the boundary (ie: "col1") and the second value is 
	 *  the offset (ie: 10)
	 */
	private function parseConstraintExp(val:String):Array
	{
		if (!val)
			return null;
		// Replace colons with spaces
		var temp:String = val.replace(/:/g, " ");
		
		// Split the string into an array 
		var args:Array = temp.split(/\s+/);
		return args;
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  If a child has been added, listen for its move event.
	 */
	private function target_childAddHandler(
								event:ChildExistenceChangedEvent):void
	{
		DisplayObject(event.relatedObject).addEventListener(
			MoveEvent.MOVE, child_moveHandler);
	}

	/**
	 *  @private
	 *  If a child has been removed, stop listening for its move event.
	 */
	private function target_childRemoveHandler(
								event:ChildExistenceChangedEvent):void
	{
		DisplayObject(event.relatedObject).removeEventListener(
			MoveEvent.MOVE, child_moveHandler);
			
		//delete this child from the constraint cache if it exists
		delete constraintCache[event.relatedObject]; 
	}

	/**
	 *  @private
	 *  If a child's position has changed, then the measured preferred
	 *  size of this canvas may have changed.
	 */
	private function child_moveHandler(event:MoveEvent):void
	{
		if (event.target is IUIComponent)
			if (!(IUIComponent(event.target).includeInLayout))
				return;

		var target:Container = super.target;
		if (target)
		{
			target.invalidateSize();
			target.invalidateDisplayList();
			_contentArea = null;
		}
	}
}
}

import mx.containers.utilityClasses.ConstraintColumn;
import mx.core.IUIComponent;
import mx.containers.utilityClasses.ConstraintRow;
	
////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: ChildConstraintInfo
//
////////////////////////////////////////////////////////////////////////////////

class ChildConstraintInfo
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function ChildConstraintInfo(
		left:Number, right:Number, hc:Number,
		top:Number, bottom:Number, vc:Number,
		baseline:Number, leftBoundary:String = null,
		rightBoundary:String = null, hcBoundary:String = null,
		topBoundary:String = null, bottomBoundary:String = null,
		vcBoundary:String = null, baselineBoundary:String = null):void
	{
		super();
		
		// offsets
		this.left = left;
		this.right = right;
		this.hc = hc;
		this.top = top;
		this.bottom = bottom;
		this.vc = vc;
		this.baseline = baseline;
		
		// boundaries (ie: parent, column or row edge)
		this.leftBoundary = leftBoundary;
		this.rightBoundary = rightBoundary;
		this.hcBoundary = hcBoundary;
		this.topBoundary = topBoundary;
		this.bottomBoundary = bottomBoundary;
		this.vcBoundary = vcBoundary;
		this.baselineBoundary = baselineBoundary;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	public var left:Number;
	public var right:Number;
	public var hc:Number;
	public var top:Number;
	public var bottom:Number;
	public var vc:Number;
	public var baseline:Number;
	public var leftBoundary:String;
	public var rightBoundary:String;
	public var hcBoundary:String;
	public var topBoundary:String;
	public var bottomBoundary:String;
	public var vcBoundary:String;
	public var baselineBoundary:String;
	
}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: ContentColumnChild
//
////////////////////////////////////////////////////////////////////////////////

class ContentColumnChild
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function ContentColumnChild():void
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	public var leftCol:ConstraintColumn;
	public var leftOffset:Number;
	public var left:Number;
	public var rightCol:ConstraintColumn;
	public var rightOffset:Number;
	public var right:Number;
	public var hcCol:ConstraintColumn;
	public var hcOffset:Number;
	public var hc:Number;
	public var child:IUIComponent;
	public var span:Number;
}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: ContentRowChild
//
////////////////////////////////////////////////////////////////////////////////

class ContentRowChild
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function ContentRowChild():void
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	public var topRow:ConstraintRow;
	public var topOffset:Number;
	public var top:Number;
	public var bottomRow:ConstraintRow;
	public var bottomOffset:Number;
	public var bottom:Number;
	public var vcRow:ConstraintRow;
	public var vcOffset:Number;
	public var vc:Number;
	public var baselineRow:ConstraintRow;
	public var baselineOffset:Number;
	public var baseline:Number;
	public var child:IUIComponent;
	public var span:Number;
	
}

	
////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: LayoutConstraints
//
////////////////////////////////////////////////////////////////////////////////

class LayoutConstraints
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	public function LayoutConstraints():void
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	public var baseline:*;
	public var bottom:*;
	public var horizontalCenter:*;
	public var left:*;
	public var right:*;
	public var top:*;
	public var verticalCenter:*;
}

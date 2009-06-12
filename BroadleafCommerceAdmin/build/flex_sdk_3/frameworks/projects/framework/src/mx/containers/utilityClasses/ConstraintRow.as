////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers.utilityClasses
{	

import flash.events.Event;
import flash.events.IEventDispatcher;
import mx.core.IInvalidating;
import mx.core.mx_internal;
import mx.core.IMXMLObject;
import flash.events.EventDispatcher;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------
[Exclude(name="container", kind="property")]
	
/**
 *  ConstraintRow class partitions an absolutely
 *  positioned container in the horizontal plane. 
 * 
 *  ConstraintRow instances have 3 sizing options: fixed, percentage,  
 *  and content. These options dictate the position of the constraint row, 
 *  the amount of space the constraint row takes in the container, and 
 *  how the constraint row deals with a change in the size of the container. 
 */
public class ConstraintRow extends EventDispatcher implements IMXMLObject
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
	public function ConstraintRow()
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	mx_internal var contentSize:Boolean = false;
	
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
	
	//----------------------------------
    //  container
    //----------------------------------

    /**
     *  @private
     */
    private var _container:IInvalidating;

    /**
     *  The container being partitioned by this ConstraintRow instance.
     */
    public function get container():IInvalidating
    {
        return _container;
    }

    /**
     *  @private
     */
    public function set container(value:IInvalidating):void
    {
        _container = value;
    }
	
	//----------------------------------
    //  height
    //----------------------------------

    /**
     *  @private
     *  Storage for the height property.
     */
    mx_internal var _height:Number;
	[Bindable("heightChanged")]
    [Inspectable(category="General")]
    [PercentProxy("percentHeight")]

    /**
     *  Number that specifies the height of the ConstraintRow instance, in pixels,
     *  in the parent's coordinates.
     * 
     */
    public function get height():Number
    {
        return _height;
    }

    /**
     *  @private
     */
    public function set height(value:Number):void
    {
		if (explicitHeight != value)
    	{
    		explicitHeight = value;
    		if (_height != value)
    		{
    			_height = value;
    			if (container)
    			{
    				container.invalidateSize();
    				container.invalidateDisplayList();
    			}
    			dispatchEvent(new Event("heightChanged"));
    		}
    	}
    }
	
	//----------------------------------
    //  explicitHeight
    //----------------------------------
    /**
     *  @private
     *  Storage for the explicitHeight property.
     */
    
    private var _explicitHeight:Number;
    [Inspectable(environment="none")]
    [Bindable("explicitHeightChanged")]
    
    /**
     *  Number that specifies the explicit height of the 
     *  ConstraintRow instance, in pixels, in the ConstraintRow 
     *  instance's coordinates.
     *
     */
    public function get explicitHeight():Number
    {
    	return _explicitHeight;
    }
    
    /**
     *  @private
     */
    public function set explicitHeight(value:Number):void
    {
    	if (_explicitHeight == value)
            return;

        // height can be pixel or percent not both
        if (!isNaN(value))
            _percentHeight = NaN;

        _explicitHeight = value;
        
        if (container)
        {
        	container.invalidateSize();
        	container.invalidateDisplayList();
        }
        
        dispatchEvent(new Event("explicitHeightChanged"));
    }
	
	//----------------------------------
    //  id
    //----------------------------------

    /**
     *  @private
     */
    private var _id:String;

    /**
     *  ID of the ConstraintRow instance. This value becomes the instance name 
     *  of the constraint row and should not contain white space or special 
     *  characters. 
     */
    public function get id():String
    {
        return _id;
    }

    /**
     *  @private
     */
    public function set id(value:String):void
    {
        _id = value;
    }
    
    //----------------------------------
    //  maxHeight
    //----------------------------------
    /**
     *  @private
     *  Storage for the maxHeight property.
     */
    private var _explicitMaxHeight:Number;
	[Bindable("maxHeightChanged")]
    [Inspectable(category="Size", defaultValue="10000")]

    /**
     *  Number that specifies the maximum height of the ConstraintRow instance,
     *  in pixels, in the ConstraintRow instance's coordinates.
     * 
     */
    public function get maxHeight():Number
    {
        return _explicitMaxHeight;
    }

    /**
     *  @private
     */
    public function set maxHeight(value:Number):void
    {
    	if (_explicitMaxHeight != value)
    	{
            _explicitMaxHeight = value;
			if (container)
			{
            	container.invalidateSize();
   				container.invalidateDisplayList();
   			}
   			dispatchEvent(new Event("maxHeightChanged"));
    	}
    }
    
    //----------------------------------
    //  minHeight
    //----------------------------------
    /**
     *  @private
     *  Storage for the minHeight property.
     */
    private var _explicitMinHeight:Number;
	[Bindable("minHeightChanged")]
    [Inspectable(category="Size", defaultValue="0")]
    
    /**
     *  Number that specifies the minimum height of the ConstraintRow instance,
     *  in pixels, in the ConstraintRow instance's coordinates.
     * 
     */
    public function get minHeight():Number
    {
        return _explicitMinHeight;
    }

    /**
     *  @private
     */
    public function set minHeight(value:Number):void
    {
    	if (_explicitMinHeight != value)
    	{
            _explicitMinHeight = value;
         	if (container)
			{
            	container.invalidateSize();
   				container.invalidateDisplayList();
   			}   
			dispatchEvent(new Event("minHeightChanged"));
    	}
    }
    
    //----------------------------------
    //  percentHeight
    //----------------------------------
    /**
     *  @private
     *  Storage for the percentHeight property.
     */
    private var _percentHeight:Number;
    [Bindable("percentHeightChanged")]
    [Inspectable(environment="none")]

    /**
     *  Number that specifies the height of a component as a percentage
     *  of its parent's size. Allowed values are 0-100. The default value is NaN.
     *  Setting the <code>width</code> property resets this property to NaN.
     */
    public function get percentHeight():Number
    {
        return _percentHeight;
    }

    /**
     *  @private
     */
    public function set percentHeight(value:Number):void
    {
        if (_percentHeight == value)
            return;

        if (!isNaN(value))
            _explicitHeight = NaN;

        _percentHeight = value;
        
        if (container)
        {
        	container.invalidateSize();
        	container.invalidateDisplayList();
        }   
    }
    
    //----------------------------------
    //  y
    //----------------------------------
	private var _y:Number;
	[Bindable("yChanged")]
	
    /**
	 *  @private
     */
    public function get y():Number
    {
        return _y;
    }

    /**
     *  @private
     */
    public function set y(value:Number):void
    {
        if (value != _y)
        {
        	_y = value;
        	dispatchEvent(new Event("yChanged"));
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods: IMXMLObject
    //
    //--------------------------------------------------------------------------
    
    /**
      *  Called automatically by the MXML compiler when the ConstraintRow
      *  instance is created using an MXML tag.  
      *  If you create the constraint row through ActionScript, you 
      *  must call this method passing in the MXML document and 
      *  <code>null</code> for the <code>id</code>.
      *
      *  @param document The MXML document containing this ConstraintRow.
      *
      *  @param id Ignored.
      */
    public function initialized(document:Object, id:String):void
    {
		this.id = id;
		if (!this.height && !this.percentHeight)
			mx_internal::contentSize = true;
    }
    
    /**
     *  Sizes the ConstraintRow
     *
     *  @param height Height of constaint row computed during parent container
     *  processing.
     */
    public function setActualHeight(h:Number):void
    {
        if (_height != h)
        {
            _height = h;
            dispatchEvent(new Event("heightChanged"));
        }
    }
    
}

}

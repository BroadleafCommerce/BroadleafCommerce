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

package mx.core
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Sprite;
import flash.geom.Rectangle;
import mx.managers.ISystemManager;

/**
 *  The IUIComponent interface defines the basic set of APIs
 *  that you must implement to create a child of a Flex container or list.
 */
public interface IUIComponent extends IFlexDisplayObject
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  The y-coordinate of the baseline
     *  of the first line of text of the component.
     * 
     *  <p>This property is used to implement
     *  the <code>baseline</code> constraint style.
     *  It is also used to align the label of a FormItem
     *  with the controls in the FormItem.</p>
     */
    function get baselinePosition():Number;

    
    //----------------------------------
    //  document
    //----------------------------------

    /**
     *  A reference to the document object associated with this component. 
     *  A document object is an Object at the top of the hierarchy
     *  of a Flex application, MXML component, or ActionScript component.
     */
    function get document():Object

    /**
     *  @private
     */
    function set document(value:Object):void

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  Whether the component can accept user interaction. After setting the <code>enabled</code>
     *  property to <code>false</code>, some components still respond to mouse interactions such 
     *  as mouseOver. As a result, to fully disable UIComponents,
     *  you should also set the value of the <code>mouseEnabled</code> property to <code>false</code>.
     *  If you set the <code>enabled</code> property to <code>false</code>
     *  for a container, Flex dims the color of the container and of all
     *  of its children, and blocks user input to the container
     *  and to all of its children.
     */
    function get enabled():Boolean;

    /**
     *  @private
     */
    function set enabled(value:Boolean):void;

    //----------------------------------
    //  explicitHeight
    //----------------------------------

    /**
     *  The explicitly specified height for the component, 
     *  in pixels, as the component's coordinates.
     *  If no height is explicitly specified, the value is <code>NaN</code>.
     *
     *  @see mx.core.UIComponent#explicitHeight
     */
    function get explicitHeight():Number;

    /**
     *  @private
     */
    function set explicitHeight(value:Number):void;

    //----------------------------------
    //  explicitMaxHeight
    //----------------------------------

    /**
     *  Number that specifies the maximum height of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#explicitMaxHeight
     */
    function get explicitMaxHeight():Number;

    //----------------------------------
    //  explicitMaxWidth
    //----------------------------------

    /**
     *  Number that specifies the maximum width of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#explicitMaxWidth
     */
    function get explicitMaxWidth():Number;

    //----------------------------------
    //  explicitMinHeight
    //----------------------------------

    /**
     *  Number that specifies the minimum height of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#explicitMinHeight
     */
    function get explicitMinHeight():Number;

    //----------------------------------
    //  explicitMinWidth
    //----------------------------------

    /**
     *  Number that specifies the minimum width of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#explicitMinWidth
     */
    function get explicitMinWidth():Number;

    //----------------------------------
    //  explicitWidth
    //----------------------------------

    /**
     *  The explicitly specified width for the component, 
     *  in pixels, as the component's coordinates.
     *  If no width is explicitly specified, the value is <code>NaN</code>.
     *
     *  @see mx.core.UIComponent#explicitWidth
     */
    function get explicitWidth():Number;

    /**
     *  @private
     */
    function set explicitWidth(value:Number):void;
    
    //----------------------------------
    //  focusPane
    //----------------------------------

    /**
     *  A single Sprite object that is shared among components
     *  and used as an overlay for drawing focus.
     *  Components share this object if their parent is a focused component,
     *  not if the component implements the IFocusManagerComponent interface.
     *
     *  @see mx.core.UIComponent#focusPane
     */
    function get focusPane():Sprite;

    /**
     *  @private
     */
    function set focusPane(value:Sprite):void;

    //----------------------------------
    //  includeInLayout
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#includeInLayout
     */
    function get includeInLayout():Boolean;

    /**
     *  @private
     */
    function set includeInLayout(value:Boolean):void;

    //----------------------------------
    //  isPopUp
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#isPopUp
     */
    function get isPopUp():Boolean;

    /**
     *  @private
     */
    function set isPopUp(value:Boolean):void;

    //----------------------------------
    //  maxHeight
    //----------------------------------

    /**
     *  Number that specifies the maximum height of the component, 
     *  in pixels, as the component's coordinates.
     *
     *  @see mx.core.UIComponent#maxHeight
     */
    function get maxHeight():Number;

    //----------------------------------
    //  maxWidth
    //----------------------------------

    /**
     *  Number that specifies the maximum width of the component, 
     *  in pixels, as the component's coordinates.
     *
     *  @see mx.core.UIComponent#maxWidth
     */
    function get maxWidth():Number;

    //----------------------------------
    //  measuredMinHeight
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#measuredMinHeight
     */
    function get measuredMinHeight():Number;

    /**
     *  @private
     */
    function set measuredMinHeight(value:Number):void;

    //----------------------------------
    //  measuredMinWidth
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#measuredMinWidth
     */
    function get measuredMinWidth():Number;

    /**
     *  @private
     */
    function set measuredMinWidth(value:Number):void;

    //----------------------------------
    //  minHeight
    //----------------------------------

    /**
     *  Number that specifies the minimum height of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#minHeight
     */
    function get minHeight():Number;

    //----------------------------------
    //  minWidth
    //----------------------------------

    /**
     *  Number that specifies the minimum width of the component, 
     *  in pixels, as the component's coordinates. 
     *
     *  @see mx.core.UIComponent#minWidth
     */
    function get minWidth():Number;

    //----------------------------------
    //  owner
    //----------------------------------

    /**
     *  Typically the parent container of this component. 
     *  However, if this is a popup component, the owner is 
     *  the component that popped it up.  
     *  For example, the owner of a dropdown list of a ComboBox control
     *  is the ComboBox control itself.
     *  This property is not managed by Flex, but 
     *  by each component. 
     *  Therefore, if you popup a component,
     *  you should set this property accordingly.
     */
    function get owner():DisplayObjectContainer;

    /**
     *  @private
     */
    function set owner(value:DisplayObjectContainer):void;

    //----------------------------------
    //  percentHeight
    //----------------------------------

    /**
     *  Number that specifies the height of a component as a 
     *  percentage of its parent's size.
     *  Allowed values are 0 to 100.     
     */
    function get percentHeight():Number;

    /**
     *  @private
     */
    function set percentHeight(value:Number):void;

    //----------------------------------
    //  percentWidth
    //----------------------------------

    /**
     *  Number that specifies the width of a component as a 
     *  percentage of its parent's size.
     *  Allowed values are 0 to 100.     
     */
    function get percentWidth():Number;

    /**
     *  @private
     */
    function set percentWidth(value:Number):void;

    //----------------------------------
    //  systemManager
    //----------------------------------

    /**
     *  A reference to the SystemManager object for this component.
     */
    function get systemManager():ISystemManager;

    /**
     *  @private
     */
    function set systemManager(value:ISystemManager):void;
    
    //----------------------------------
    //  tweeningProperties
    //----------------------------------

    /**
     *  Used by EffectManager.
     *  Returns non-null if a component
     *  is not using the EffectManager to execute a Tween.
     */
    function get tweeningProperties():Array;

    /**
     *  @private
     */
    function set tweeningProperties(value:Array):void;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Initialize the object.
     *
     *  @see mx.core.UIComponent#initialize()
     */
    function initialize():void;
    
    /**
     *  @copy mx.core.UIComponent#parentChanged()
     */
    function parentChanged(p:DisplayObjectContainer):void;
    
    /**
     *  @copy mx.core.UIComponent#getExplicitOrMeasuredWidth()
     */
    function getExplicitOrMeasuredWidth():Number;

    /**
     *  @copy mx.core.UIComponent#getExplicitOrMeasuredHeight()
     */
    function getExplicitOrMeasuredHeight():Number;
    
    /**
     *  @copy mx.core.UIComponent#setVisible() 
     */
    function setVisible(value:Boolean, noEvent:Boolean = false):void;

    /**
     *  @copy mx.core.UIComponent#owns() 
     */
    function owns(displayObject:DisplayObject):Boolean;
}

}

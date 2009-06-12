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

package mx.managers
{

import flash.display.DisplayObject;
import flash.events.EventDispatcher;
import mx.core.IToolTip;
import mx.core.IUIComponent;
import mx.core.Singleton;
import mx.core.mx_internal;
import mx.effects.IAbstractEffect;

/**
 *  The ToolTipManager lets you set basic ToolTip and error tip functionality,
 *  such as display delay and the disabling of ToolTips.
 *
 *  @see mx.controls.ToolTip
 *  @see mx.validators.Validator
 */
public class ToolTipManager extends EventDispatcher
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Linker dependency on implementation class.
     */
    private static var implClassDependency:ToolTipManagerImpl;

    /**
     *  @private
     *  Storage for the impl getter.
     *  This gets initialized on first access,
     *  not at static initialization time, in order to ensure
     *  that the Singleton registry has already been initialized.
     */
    private static var _impl:IToolTipManager2;

    /**
     *  @private
     *  The singleton instance of ToolTipManagerImpl which was
     *  registered as implementing the IToolTipManager2 interface.
     */
    private static function get impl():IToolTipManager2
    {
        if (!_impl)
        {
            _impl = IToolTipManager2(
                Singleton.getInstance("mx.managers::IToolTipManager2"));
        }
        
        return _impl;
    }

    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentTarget
    //----------------------------------

    /**
     *  The UIComponent that is currently displaying a ToolTip,
     *  or <code>null</code> if none is.
     */
    public static function get currentTarget():DisplayObject
    {
        return impl.currentTarget;
    }
    
    /**
     *  @private
     */
    public static function set currentTarget(value:DisplayObject):void
    {
        impl.currentTarget = value;
    }
    
    //----------------------------------
    //  currentToolTip
    //----------------------------------

    /**
     *  The ToolTip object that is currently visible,
     *  or <code>null</code> if none is shown.
     */
    public static function get currentToolTip():IToolTip
    {
        return impl.currentToolTip;
    }

    /**
     *  @private
     */
    public static function set currentToolTip(value:IToolTip):void
    {
        impl.currentToolTip = value;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  If <code>true</code>, the ToolTipManager will automatically show
     *  ToolTips when the user moves the mouse pointer over components.
     *  If <code>false</code>, no ToolTips will be shown.
     *
     *  @default true
     */
    public static function get enabled():Boolean
    {
        return impl.enabled;
    }

    /**
     *  @private
     */
    public static function set enabled(value:Boolean):void
    {
        impl.enabled = value;
    }

    //----------------------------------
    //  hideDelay
    //----------------------------------

    /**
     *  The amount of time, in milliseconds, that Flex waits
     *  to hide the ToolTip after it appears.
     *  Once Flex hides a ToolTip, the user must move the mouse
     *  off the component and then back onto it to see the ToolTip again.
     *  If you set <code>hideDelay</code> to <code>Infinity</code>,
     *  Flex does not hide the ToolTip until the user triggers an event,
     *  such as moving the mouse off of the component.
     *
     *  @default 10000
     */
    public static function get hideDelay():Number
    {
        return impl.hideDelay;
    }
    
    /**
     *  @private
     */
    public static function set hideDelay(value:Number):void
    {
        impl.hideDelay = value;
    }

    //----------------------------------
    //  hideEffect
    //----------------------------------

    /**
     *  The effect that plays when a ToolTip is hidden,
     *  or <code>null</code> if the ToolTip should disappear with no effect.
     *
     *  @default null
     */
    public static function get hideEffect():IAbstractEffect
    {
        return impl.hideEffect;
    }

    /**
     *  @private
     */
    public static function set hideEffect(value:IAbstractEffect):void
    {
        impl.hideEffect = value;
    }

    //----------------------------------
    //  scrubDelay
    //----------------------------------

    /**
     *  The amount of time, in milliseconds, that a user can take
     *  when moving the mouse between controls before Flex again waits
     *  for the duration of <code>showDelay</code> to display a ToolTip.
     *
     *  <p>This setting is useful if the user moves quickly from one control
     *  to another; after displaying the first ToolTip, Flex will display
     *  the others immediately rather than waiting.
     *  The shorter the setting for <code>scrubDelay</code>, the more
     *  likely that the user must wait for an amount of time specified
     *  by <code>showDelay</code> in order to see the next ToolTip.
     *  A good use of this property is if you have several buttons on a
     *  toolbar, and the user will quickly scan across them to see brief
     *  descriptions of their functionality.</p>
     *
     *  @default 100
     */
    public static function get scrubDelay():Number
    {
        return impl.scrubDelay;
    }

    /**
     *  @private
     */
    public static function set scrubDelay(value:Number):void
    {
        impl.scrubDelay = value;
    }

    //----------------------------------
    //  showDelay
    //----------------------------------

    /**
     *  The amount of time, in milliseconds, that Flex waits
     *  before displaying the ToolTip box once a user
     *  moves the mouse over a component that has a ToolTip.
     *  To make the ToolTip appear instantly, set <code>showDelay</code> to 0.
     *
     *  @default 500
     */
    public static function get showDelay():Number
    {
        return impl.showDelay;
    }

    /**
     *  @private
     */
    public static function set showDelay(value:Number):void
    {
        impl.showDelay = value;
    }

    //----------------------------------
    //  showEffect
    //----------------------------------

    /**
     *  The effect that plays when a ToolTip is shown,
     *  or <code>null</code> if the ToolTip should appear with no effect.
     *
     *  @default null
     */
    public static function get showEffect():IAbstractEffect
    {
        return impl.showEffect;
    }

    /**
     *  @private
     */
    public static function set showEffect(value:IAbstractEffect):void
    {
        impl.showEffect = value;
    }

    //----------------------------------
    //  toolTipClass
    //----------------------------------

    /**
     *  The class to use for creating ToolTips.
     *  
     *  @default mx.controls.ToolTip
     */
    public static function get toolTipClass():Class
    {
        return impl.toolTipClass;
    }

    /**
     *  @private
     */
    public static function set toolTipClass(value:Class):void
    {
        impl.toolTipClass = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Registers a target UIComponent or UITextField, and the text
     *  for its ToolTip, with the ToolTipManager.
     *  This causes the ToolTipManager to display a ToolTip
     *  when the mouse hovers over the target.
     *
     *  <p>This method is called by the setter
     *  for the toolTip property in UIComponent and UITextField.</p>
     *
     *  @param target The UIComponent or UITextField that owns the ToolTip.
     *
     *  @param toolTip The text to display in the ToolTip.
     *  If null, no ToolTip will be displayed when the mouse hovers
     *  over the target.
     */
    mx_internal static function registerToolTip(target:DisplayObject,
                                                oldToolTip:String,
                                                newToolTip:String):void
    {
        impl.registerToolTip(target, oldToolTip, newToolTip);
    }

    /**
     *  Registers a target UIComponent, and the text
     *  for its error tip, with the ToolTipManager.
     *  This causes the ToolTipManager to display an error tip
     *  when the mouse hovers over the target.
     *
     *  <p>This method is called by the setter
     *  for the errorString property in UIComponent.</p>
     *
     *  @param target The UIComponent or UITextField that owns the ToolTip.
     *
     *  @param toolTip The text to display in the ToolTip.
     *  If null, no ToolTip will be displayed when the mouse hovers
     *  over the target.
     */
    mx_internal static function registerErrorString(target:DisplayObject,
                                                    oldErrorString:String,
                                                    newErrorString:String):void
    {
        impl.registerErrorString(target, oldErrorString, newErrorString);
    }

    /**
     *  @private
     *  Objects added to the SystemManager's ToolTip layer don't get
     *  automatically measured or sized, so ToolTipManager has to
     *  measure it and set its size.
     */
    mx_internal static function sizeTip(toolTip:IToolTip):void
    {
        impl.sizeTip(toolTip);
    }

    /**
     *  Creates an instance of the ToolTip class with the specified text
     *  and displays it at the specified location in stage coordinates.
     *
     *  <p>ToolTips appear in their own layer, on top of everything
     *  except cursors.</p>
     *
     *  <p>The standard way of using ToolTips is to let the ToolTipManager
     *  automatically show and hide them as the user moves the mouse over
     *  the objects that have the <code>toolTip</code> property set.
     *  You can turn off this automatic ToolTip management by setting
     *  the ToolTipManager's <code>enabled</code> property to
     *  <code>false</code>.</p>
     *
     *  <p>By contrast, this method&#x2014;along with <code>destroyToolTip()</code>&#x2014;gives 
     *  you programmatic control over ToolTips.
     *  You can show them when and where you choose,
     *  and you can even show more than one at once if you need to.
     *  (The ToolTipManager never does this, because it is generally
     *  confusing to the user).</p>
     *
     *  <p>This method first creates a new instance of ToolTip and calls the 
     *  <code>addChild()</code> method to put it into the SystemManager's
     *  toolTips layer.
     *  If you are showing an error tip, it sets the appropriate styles.
     *  Then it sets the text for the ToolTip, sizes the ToolTip based on
     *  its text, and positions it where you specified.</p>
     *
     *  <p>You must save the reference to the ToolTip that this method
     *  returns so that you can pass it to the <code>destroyToolTip()</code> method.</p>
     *
     *  @param text The text to display in the ToolTip instance.
     *
     *  @param x The horizontal coordinate of the ToolTip in stage coordinates.
     *  In case of multiple stages, the relevant stage is determined
     *  from the <code>context</code> argument.
     *
     *  @param y The vertical coordinate of the ToolTip in stage coordinates.
     *  In case of multiple stages, the relevant stage is determined
     *  from the <code>context</code> argument.
     *
     *  @param errorTipBorderStyle The border style of an error tip. This method 
     *  argument can be null, "errorTipRight", "errorTipAbove", or "errorTipBelow". 
     *  If it is null, then the <code>createToolTip()</code> method creates a normal ToolTip. If it is 
     *  "errorTipRight", "errorTipAbove", or "errorTipBelow", then the <code>createToolTip()</code> 
     *  method creates an error tip, and this parameter determines where the arrow 
     *  of the error tip points to (the error's target). For example, if you pass "errorTipRight", Flex 
     *  positions the error tip (via the x and y arguments) to the 
     *  right of the error target; the arrow is on the left edge of the error tip.
     *
     *  @param context This property is not currently used.
     *
     *  @return The newly created ToolTip.
     *
     */
    public static function createToolTip(text:String, x:Number, y:Number,
                                         errorTipBorderStyle:String = null,
                                         context:IUIComponent = null):IToolTip
    {
        return impl.createToolTip(text, x, y, errorTipBorderStyle, context);
    }

    /**
     *  Destroys a specified ToolTip that was created by the <code>createToolTip()</code> method.
     *
     *  <p>This method calls the <code>removeChild()</code> method to remove the specified
     *  ToolTip from the SystemManager's ToolTips layer.
     *  It will then be garbage-collected unless you keep a
     *  reference to it.</p>
     *
     *  <p>You should not call this method on the ToolTipManager's
     *  <code>currentToolTip</code>.</p>
     *
     *  @param toolTip The ToolTip instance to destroy.
     */
    public static function destroyToolTip(toolTip:IToolTip):void
    {
        return impl.destroyToolTip(toolTip);
    }
}

}

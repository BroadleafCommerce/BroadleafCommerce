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

package mx.states
{

import mx.core.UIComponent;
import mx.styles.IStyleClient;
import mx.styles.StyleManager;

/**
 *  The SetStyle class specifies a style that is in effect only during the parent view state.
 *  You use this class in the <code>overrides</code> property of the State class.
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:SetStyle&gt;</code> tag
 *  has the following attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:SetStyle
 *   <b>Properties</b>
 *   name="null"
 *   target="null"
 *   value"null"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.states.State
 *  @see mx.states.SetEventHandler
 *  @see mx.states.SetProperty
 *  @see mx.effects.SetStyleAction
 *
 *  @includeExample examples/StatesExample.mxml
 */
public class SetStyle implements IOverride
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This is a table of related properties.
     *  Whenever the property being overridden is found in this table,
     *  the related property is also saved and restored.
     */
    private static const RELATED_PROPERTIES:Object =
    {
        left: [ "x" ],
        top: [ "y" ],
        right: [ "x" ],
        bottom: [ "y" ]
    };
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param target The object whose style is being set.
     *  By default, Flex uses the immediate parent of the State object.
     *
     *  @param name The style to set.
     *
     *  @param value The value of the style in the view state.
     */
    public function SetStyle(
            target:IStyleClient = null,
            name:String = null,
            value:Object = null)
    {
        super();

        this.target = target;
        this.name = name;
        this.value = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Storage for the old style value.
     */
    private var oldValue:Object;

    /**
     *  @private
     *  Storage for the old related property values, if used.
     */
    private var oldRelatedValues:Array;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  name
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *
     *  The name of the style to change.
     *  You must set this property, either in 
     *  the SetStyle constructor or by setting
     *  the property value directly.
     */
    public var name:String;

    //----------------------------------
    //  target
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *
     *  The object whose style is being changed.
     *  If the property value is <code>null</code>, Flex uses the
     *  immediate parent of the State object.
     * 
     *  @default null
     */
    public var target:IStyleClient;

    //----------------------------------
    //  value
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *
     *  The new value for the style.
     *
     *  @default null
     */
    public var value:Object;

    //--------------------------------------------------------------------------
    //
    //  IOverride methods
    //
    //--------------------------------------------------------------------------

    /**
     *  IOverride interface method; this class implements it as an empty method.
     * 
     *  @copy IOverride#initialize()
     */
    public function initialize():void
    {
    }

    /**
     *  @inheritDoc
     */
    public function apply(parent:UIComponent):void
    {
        var obj:IStyleClient = target ? target : parent;
        var relatedProps:Array = RELATED_PROPERTIES[name] ?
                                 RELATED_PROPERTIES[name] :
                                 null;

        // Remember the current value so it can be restored
        oldValue = obj.getStyle(name);

        if (relatedProps)
        {
            oldRelatedValues = [];

            for (var i:int = 0; i < relatedProps.length; i++)
                oldRelatedValues[i] = obj[relatedProps[i]];
        }

        // Set new value
        if (value === null)
        {
            obj.clearStyle(name);
        }
        else if (oldValue is Number)
        {
            // The "value" for colors can be several different formats:
            // 0xNNNNNN, #NNNNNN or "red". We can't use
            // StyleManager.isColorStyle() because that only returns true
            // for inheriting color styles and misses non-inheriting styles like
            // backgroundColor.
            if (name.toLowerCase().indexOf("color") != -1)
                obj.setStyle(name, StyleManager.getColorName(value));
            else
                obj.setStyle(name, Number(value));
        }
        else if (oldValue is Boolean)
        {
            obj.setStyle(name, toBoolean(value));
        }
        else
        {
            obj.setStyle(name, value);
        }
    }

    /**
     *  @inheritDoc
     */
    public function remove(parent:UIComponent):void
    {
        var obj:IStyleClient = target ? target : parent;

        // Restore the old value
        if (oldValue is Number)
            obj.setStyle(name, Number(oldValue));
        else if (oldValue is Boolean)
            obj.setStyle(name, toBoolean(oldValue));
        else if (oldValue === null)
            obj.clearStyle(name);
        else
            obj.setStyle(name, oldValue);


        var relatedProps:Array = RELATED_PROPERTIES[name] ?
                                 RELATED_PROPERTIES[name] :
                                 null;

        // Restore related property values, if needed
        if (relatedProps)
        {
            for (var i:int = 0; i < relatedProps.length; i++)
            {
                obj[relatedProps[i]] = oldRelatedValues[i];
            }
        }
    }

    /**
     *  @private
     *  Converts a value to a Boolean true/false.
     */
    private function toBoolean(value:Object):Boolean
    {
        if (value is String)
            return value.toLowerCase() == "true";

        return value != false;
    }
}

}

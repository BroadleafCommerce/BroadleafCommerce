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
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The SetProperty class specifies a property value that is in effect only 
 *  during the parent view state.
 *  You use this class in the <code>overrides</code> property of the State class.
 * 
 *  @mxml
 *
 *  <p>The <code>&lt;mx:SetProperty&gt;</code> tag
 *  has the following attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:SetProperty
 *   <b>Properties</b>
 *   name="null"
 *   target="null"
 *   value="undefined"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.states.State
 *  @see mx.states.SetEventHandler
 *  @see mx.states.SetStyle
 *  @see mx.effects.SetPropertyAction
 *
 *  @includeExample examples/StatesExample.mxml
 */
public class SetProperty implements IOverride
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  This is a table of pseudonyms.
     *  Whenever the property being overridden is found in this table,
     *  the pseudonym is saved/restored instead.
     */
    private static const PSEUDONYMS:Object =
    {
        width: "explicitWidth",
        height: "explicitHeight"
    };

    /**
     *  @private
     *  This is a table of related properties.
     *  Whenever the property being overridden is found in this table,
     *  the related property is also saved and restored.
     */
    private static const RELATED_PROPERTIES:Object =
    {
        explicitWidth: [ "percentWidth" ],
        explicitHeight: [ "percentHeight" ]
    };

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param target The object whose property is being set.
     *  By default, Flex uses the immediate parent of the State object.
     *
     *  @param name The property to set.
     *
     *  @param value The value of the property in the view state.
     */
    public function SetProperty(target:Object = null, name:String = null,
                                value:* = undefined)
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
     *  Storage for the old property value.
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
     *  The name of the property to change.
     *  You must set this property, either in 
     *  the SetProperty constructor or by setting
     *  the property value directly.
     */
    public var name:String;

    //----------------------------------
    //  target
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  The object containing the property to be changed.
     *  If the property value is <code>null</code>, Flex uses the
     *  immediate parent of the State object.
     *
     *  @default null
     */
    public var target:Object;

    //----------------------------------
    //  value
    //----------------------------------

    [Inspectable(category="General")]

    /**
     *  The new value for the property.
     *
     *  @default undefined
     */
    public var value:*;

    //--------------------------------------------------------------------------
    //
    //  Methods: IOverride
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
        var obj:Object = target ? target : parent;
        
        var propName:String = PSEUDONYMS[name] ?
                          PSEUDONYMS[name] :
                          name;

        var relatedProps:Array = RELATED_PROPERTIES[propName] ?
                                 RELATED_PROPERTIES[propName] :
                                 null;

        var newValue:* = value;

        // Remember the current value so it can be restored
        oldValue = obj[propName];

        if (relatedProps)
        {
            oldRelatedValues = [];

            for (var i:int = 0; i < relatedProps.length; i++)
                oldRelatedValues[i] = obj[relatedProps[i]];
        }

        // Special case for width and height. If they are percentage values,
        // set the percentWidth/percentHeight instead.
        if (name == "width" || name == "height")
        {
            if (newValue is String && newValue.indexOf("%") >= 0)
            {
                propName = name == "width" ? "percentWidth" : "percentHeight";
                newValue = newValue.slice(0, newValue.indexOf("%"));
            }
            else
            {
                // Need to set width/height instead of explicitWidth/explicitHeight
                // otherwise width/height are out of sync until the target is validated.
                propName = name;
            }
        }

        // Set new value
        setPropertyValue(obj, propName, newValue, oldValue);
    }

    /**
     *  @inheritDoc
     */
    public function remove(parent:UIComponent):void
    {
        var obj:Object = target ? target : parent;
        
        var propName:String = PSEUDONYMS[name] ?
                          PSEUDONYMS[name] :
                          name;
        
        var relatedProps:Array = RELATED_PROPERTIES[propName] ?
                                 RELATED_PROPERTIES[propName] :
                                 null;

        // Special case for width and height. Restore the "width" and
        // "height" properties instead of explicitWidth/explicitHeight
        // so they can be kept in sync.
        if ((name == "width" || name == "height") && !isNaN(Number(oldValue)))
        {
            propName = name;
        }
        
        // Restore the old value
        setPropertyValue(obj, propName, oldValue, oldValue);

        // Restore related value, if needed
        if (relatedProps)
        {
            for (var i:int = 0; i < relatedProps.length; i++)
            {
                setPropertyValue(obj, relatedProps[i],
                        oldRelatedValues[i], oldRelatedValues[i]);
            }
        }
    }

    /**
     *  @private
     *  Sets the property to a value, coercing if necessary.
     */
    private function setPropertyValue(obj:Object, name:String, value:*,
                                      valueForType:Object):void
    {
        if (valueForType is Number)
            obj[name] = Number(value);
        else if (valueForType is Boolean)
            obj[name] = toBoolean(value);
        else
            obj[name] = value;
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

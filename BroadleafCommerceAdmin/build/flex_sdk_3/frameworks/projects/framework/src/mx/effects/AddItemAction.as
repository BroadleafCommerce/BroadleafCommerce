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

package mx.effects
{

import mx.core.mx_internal;
import mx.effects.effectClasses.PropertyChanges;
import mx.effects.effectClasses.AddItemActionInstance;
import mx.controls.listClasses.ListBase;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The AddItemAction class defines an action effect that determines 
 *  when the item renderer appears in the control for an item being added 
 *  to a list-based control, such as List or TileList, 
 *  or for an item that replaces an existing item in the control.
 *  You can use this class as part of defining custom data effect for the 
 *  list-based classes.
 *   
 *  @mxml
 *
 *  <p>The <code>&lt;mx:AddItemAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds no new tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:AddItemAction
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.effectClasses.AddItemActionInstance
 *
 *  @includeExample examples/AddItemActionEffectExample.mxml
 */
public class AddItemAction extends Effect
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var AFFECTED_PROPERTIES:Array = [ "parent"];

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

//  public var effectHost:ListBase = null
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param target The Object to animate with this effect.
     */
    public function AddItemAction(target:Object = null)
    {
        super(target);

        instanceClass = AddItemActionInstance;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function getAffectedProperties():Array /* of String */
    {
        return AFFECTED_PROPERTIES;
    }
    
    /**
     *  @private
     */
    override protected function initInstance(instance:IEffectInstance):void
    {
        super.initInstance(instance);
        
        var actionInstance:AddItemActionInstance  =
            AddItemActionInstance(instance);

//      actionInstance.effectHost = effectTargetHost;
    }

    // might be other methods we need to override here, but it's not at
    // all clear that they will be applicable.  
}

}

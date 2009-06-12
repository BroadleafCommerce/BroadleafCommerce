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
import mx.effects.effectClasses.RemoveItemActionInstance;
import mx.controls.listClasses.ListBase;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="duration", kind="property")]

/**
 *  The RemoveItemAction class defines an action effect that determines 
 *  when the item renderer disappears from the control for the item renderer 
 *  of an item being removed from a list-based control, such as List or TileList, 
 *  or for an item that is replaced by a new item added to the control. 
 *  You can use this class as part of defining custom data effect for the 
 *  list-based classes.
 *   
 *  @mxml
 *
 *  <p>The <code>&lt;mx:RemoveItemAction&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds no new tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:RemoveItemAction
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.effectClasses.RemoveItemActionInstance
 *
 *  @includeExample examples/AddItemActionEffectExample.mxml
 */
public class RemoveItemAction extends Effect
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
    private static var AFFECTED_PROPERTIES:Array = ["parent"];

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
    public function RemoveItemAction(target:Object = null)
    {
        super(target);

        instanceClass = RemoveItemActionInstance;
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
        
        var actionInstance:RemoveItemActionInstance  =
            RemoveItemActionInstance(instance);

    }
}

}

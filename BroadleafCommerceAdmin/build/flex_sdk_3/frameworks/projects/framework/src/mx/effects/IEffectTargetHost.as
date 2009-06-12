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

/**
 *  The IEffectTargetHost interface defines the interface that lets you access the 
 *  target list-based control of a data effect. 
 *  All list-based controls implement this interface.
 *  This interface enables an instance of an effect class to communicate with the 
 *  list-based control on which the effect is playing. 
 *  This interface is also used for determining whether to play an effect based on if a data item is 
 *  added, removed, or replaced in the target control. 
 *
 *  @see mx.controls.listClasses.ListBase
 */
public interface IEffectTargetHost
{

    /**
     *  Called by an <code>UnconstrainItemAction</code> effect
     *  as part of a data change effect if the item renderers corresponding
     *  to certain data items need to move outside the normal positions
     *  of item renderers in the control. 
     *  The control does not attempt to position the item render for the 
     *  duration of the effect.
     * 
     *  @param item The item renderer that is a target of the effect.
     */
    function unconstrainRenderer(item:Object):void;

    /**
     *  Removes an item renderer if a data change effect is running.
     *  The item renderer must correspond to data that has already
     *  been removed from the data provider collection.
     * 
     *  This function will be called by a <code>RemoveItemAction</code>
     *  effect as part of a data change effect to specify the point
     *  at which a data item ceases to displayed by the control using
     *  an item renderer.
     * 
     *  @param item The item renderer to remove from the control's layout.
     */
    function removeDataEffectItem(target:Object):void;

    /**
     *  Adds an item renderer if a data change effect is running.
     *  The item renderer should correspond to a recently added
     *  data item in the data provider's collection that isn't
     *  yet being displayed.
     * 
     *  <p>This function will be called by an <code>AddItemAction</code>
     *  effect as part of a data change effect to specify the point
     *  at which a data item added to a collection begins to be displayed
     *  by the control using an item renderer.</p>
     * 
     *  @param item The item renderer to add to the control's layout.
     */
    function addDataEffectItem(target:Object):void;

    /**
     *  Returns <code>true</code> or <code>false</code> 
     *  to indicates whether the effect should play on the target.
     *  The EffectTargetFilter class calls this method when you set 
     *  the <code>filter</code> property on a data effect. 
     *  For example, you set <code>filter</code> property 
     *  to <code>addItem</code> or <code>removeItem</code>.
     *
     *  @param target An item renderer
     * 
     *  @param semanticProperty The semantic property of the renderer
     *  whose value will be returned.
     *  
     *  @return <code>true</code> or <code>false</code> 
     *  to indicates whether the effect should play on the target. 
     */
    function getRendererSemanticValue(target:Object,semanticProperty:String):Object;

}

}
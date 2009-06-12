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

package mx.effects
{

import mx.effects.effectClasses.FadeInstance;

/**
 *  The Fade effect animates the <code>alpha</code> property of a component,
 *  either from transparent to opaque, or from opaque to transparent. 
 *  
 *  <p>If you specify the Fade effect for the <code>showEffect</code> 
 *  or <code>hideEffect</code> trigger, and if you omit values for the
 *  <code>alphaFrom</code> and <code>alphaTo</code> properties, 
 *  the effect automatically transitions <code>alpha</code> from 0
 *  to the target's current <code>alpha</code> value on a
 *  <code>showEffect</code> trigger, and from the target's current
 *  <code>alpha</code> value to 0 on a <code>hideEffect</code> trigger.</p> 
 *
 *  <p><b>Note:</b> To use the Fade effect with text,
 *  you must use an embedded font, not a device font. </p> 
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Fade&gt;</code> tag
 *  inherits the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:Fade 
 *    id="ID"
 *    alphaFrom="val"
 *    alphaTo="val"
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.FadeInstance
 *
 *  @includeExample examples/FadeEffectExample.mxml
 */
public class Fade extends TweenEffect
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
    private static var AFFECTED_PROPERTIES:Array = [ "alpha", "visible" ];

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
    public function Fade(target:Object = null)
    {
        super(target);

        instanceClass = FadeInstance;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  alphaFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="undefined")]
    
    /** 
     *  Initial transparency level between 0.0 and 1.0, 
     *  where 0.0 means transparent and 1.0 means fully opaque. 
     * 
     *  <p>If the effect causes the target component to disappear,
     *  the default value is the current value of the target's
     *  <code>alpha</code> property.
     *  If the effect causes the target component to appear,
     *  the default value is 0.0.</p>
     */
    public var alphaFrom:Number;
    
    //----------------------------------
    //  alphaTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]
    
    /** 
     *  Final transparency level,
     *  where 0.0 means transparent and 1.0 means fully opaque.
     *
     *  <p>If the effect causes the target component to disappear,
     *  the default value is 0.0.
     *  If the effect causes the target component to appear,
     *  the default value is the current value of the target's
     *  <code>alpha</code> property.</p>
     */
    public var alphaTo:Number;
    
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
        
        var fadeInstance:FadeInstance = FadeInstance(instance);

        fadeInstance.alphaFrom = alphaFrom;
        fadeInstance.alphaTo = alphaTo;
    }
}

}

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

package mx.effects
{

import mx.effects.effectClasses.PropertyChanges;

/**
 *  The EffectTargetFilter class defines a custom filter that is executed 
 *  by each transition effect on each target of the effect. 
 *
 *  <p>The EffectTargetFilter class defines a
 *  <code>defaultFilterFunction()</code> method that uses the
 *  <code>filterProperties</code> and <code>filterStyles</code> properties
 *  to determine whether to play the effect on each effect target.</p>
 *  
 *  <p>You can also define a custom filter function
 *  to implement your own filtering logic.
 *  To do so, define your filter function, and then specify that function
 *  to an EffectTargetFilter object using the <code>filterFunction</code>
 *  property.</p>
 *  
 *  <p>To configure an effect to use a custom filter, you pass an 
 *  EffectTargetFilter object to the <code>Effect.customFilter</code> property 
 *  of the effect.</p>
 */
public class EffectTargetFilter
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function EffectTargetFilter()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  filterFunction
    //----------------------------------

    /**
     *  A function that defines custom filter logic.
     *  Flex calls this method on every target of the effect.
     *  If the function returns <code>true</code>,
     *  the effect plays on the target;
     *  if it returns <code>false</code>, the target is skipped by the effect.
     *  A custom filter function gives you greater control over filtering
     *  than the <code>Effect.filter</code> property. 
     *
     *  <p>The filter function has the following signature:</p>
     *
     *  <pre>
     *  filterFunc(propChanges:Array, instanceTarget:Object):Boolean
     *  {
     *      // Return true to play the effect on instanceTarget, 
     *      // or false to not play the effect.
     *  } 
     *  </pre>
     *
     *  <p>where:</p>
     *  
     *  <p><code>propChanges</code> - An Array of PropertyChanges objects, 
     *  one object per target component of the effect.
     *  If a property of a target is not modified by the transition,
     *  it is not included in this Array.</p>
     *  
     *  <p><code>instanceTarget</code> - The specific target component
     *  of the effect that you want to filter.
     *  Within the custom filter function, you first search the
     *  <code>propChanges</code> Array for the PropertyChanges object
     *  that matches the <code>instanceTarget</code> argument
     *  by comparing the <code>instanceTarget</code> argument
     *  to the <code>propChanges.target</code> property.</p> 
     *
     *  @see mx.effects.effectClasses.PropertyChanges 
     */
    public var filterFunction:Function = defaultFilterFunctionEx;
        
    //----------------------------------
    //  filterProperties
    //----------------------------------

    /** 
     *  An Array of Strings specifying component properties. 
     *  If any of the properties in the Array changed on the target component, 
     *  play the effect on the target. 
     *
     *  <p>If you define a custom filter function, you can examine the 
     *  <code>filterProperties</code> property from within your function.</p>
     */
    public var filterProperties:Array = [];
    
    //----------------------------------
    //  filterStyles
    //----------------------------------

    /** 
     *  An Array of Strings specifying style properties. 
     *  If any of the style properties in the Array changed on the target component, 
     *  play the effect on the target. 
     *
     *  <p>If you define a custom filter function, you can examine the 
     *  <code>filterStyles</code> property from within your function.</p>
     */
    public var filterStyles:Array = [];

    //----------------------------------
    //  requiredSemantics
    //----------------------------------

    /**
     *  A collection of properties and associated values which must be associated
     *  with a target for the effect to be played.
     *
     *  <p>When working with data effects, you can use this property to filter effects. 
     *  If you want to play a data effect on all targets of a list control 
     *  that are not added by the effect, meaning targets that is removed, replaced, moved, 
     *  or affected in any other way, you can write the effect definition as shown below: </p>
     *
     *  <pre>
     *  &lt;mx:Blur&gt;
     *      &lt;mx:customFilter&gt;
     *          &lt;mx:EffectTargetFilter requiredSemantics="{{'added':false}}"/&gt;
     *      &lt;/mx:customFilter&gt;
     *  &lt;/mx:Blur&gt; </pre>
     *
     *  <p>To play a data effect on all targets that are not added or not removed by the effect, 
     *  you can write the effect definition as shown below:</p>
     *
     *  <pre>
     *  &lt;mx:Blur&gt;
     *      &lt;mx:customFilter&gt;
     *          &lt;mx:EffectTargetFilter requiredSemantics="{{'added':false}, {'removed':false}}"/&gt;
     *      &lt;/mx:customFilter&gt;
     *  &lt;/mx:Blur&gt;</pre>
     *
     *  <p>The allowed list of properties that you can specify includes <code>added</code>, 
     *  <code>removed</code>, <code>replaced</code>, and <code>replacement</code>. 
     *  The allowed values for the properties are <code>true</code> and <code>false</code>.</p>
     */
    public var requiredSemantics:Object = null;
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /** 
     *  Determines whether a target should be filtered, returning true if it should be
     *  included in an effect. 
     *
     *  The determination is made by calling filterFunction and semanticFilterFunction,
     *  returning true if and only if both functions return true. The default functions
     *  with the default values will always return true.
     *
     *  Typically, an EffectTargetFilter will use one type of filter or the other, but
     *  not both.
     *
     *  @param propChanges An Array of PropertyChanges objects. The target property of 
     *  each PropertyChanges object is equal to the effect's target. If a property of 
     *  a target is not modified by a transition, the corresponding PropertyChanges 
     *  object is not included in this array.
     *
     *  @param semanticsProvider The IEffectTargetHost used to evaluate the properties 
     *  specified in requiredSemantics for the target, normally the effectTargetHost of 
     *  the effect. For item change effects, when the targets of the effect are item 
     *  renderers, this will be the List or TileList containing the item renderers.
     *
     *  @param target The target of the EffectInstance that calls this function. If an 
     *  effect has multiple targets, this function is called once per target.
     *
     *  @return Returna <code>true</code>, if the target should be included in the effect; 
     *  otherwise returns <code>false</code>.
     */
    public function filterInstance(propChanges:Array, semanticsProvider:IEffectTargetHost, 
                                   target:Object):Boolean
    {
        // TODO For better performance, 
        if (filterFunction.length == 2)
            return filterFunction(propChanges, target);
        else // if (filterFunction.length == 3)
            return filterFunction(propChanges, semanticsProvider, target);
    }                                   

    /** 
     *  @private
     */
    protected function defaultFilterFunctionEx(propChanges:Array, semanticsProvider:IEffectTargetHost, 
                                   target:Object):Boolean
    {
        if (requiredSemantics)
        {
            for (var prop:String in requiredSemantics)
            {
                // seems dumb to check this every time
                // if necessary, we could do something with setters and getters
                if (!semanticsProvider)
                    return false;
                if (semanticsProvider.getRendererSemanticValue(target,prop) != requiredSemantics[prop])
                    return false;
            }

            // not clear this is the right thing to do here
            // the problem is that defaultFilterFunction returns false in 
            // some cases where we might expect it to return true
            return true;
        }
        // if semantic filtering has passed, do property change filtering
        return defaultFilterFunction(propChanges, target);
    }
                                       
    /**
     *  The default filter function for the EffectTargetFilter class. 
     *  If the <code>instanceTarget</code> has different start and end values
     *  for any of the values specified by the <code>filterProperties</code>
     *  or <code>filterStyles</code> properties, play the effect on the target.
     *
     *  @param propChanges An Array of PropertyChanges objects.
     *  The <code>target</code> property of each PropertyChanges object
     *  is equal to the effect's target. 
     *  If a property of a target is not modified by a transition, the 
     *  corresponding PropertyChanges 
     *  object is not included in this array.
     *  
     *  @param instanceTarget The target of the EffectInstance
     *  that calls this function.
     *  If an effect has multiple targets,
     *  this function is called once per target. 
     *
     *  @return Returns <code>true</code> to allow the effect instance to play. 
     *
     *  @see mx.effects.effectClasses.PropertyChanges 
     */
    protected function defaultFilterFunction(propChanges:Array,
                                             instanceTarget:Object):Boolean
    {
        var n:int = propChanges.length;
        for (var i:int = 0; i < n; i++)
        {
            var props:PropertyChanges = propChanges[i];
            if (props.target == instanceTarget)
            {
                var triggers:Array = filterProperties.concat(filterStyles);
                var m:int = triggers.length;
                for (var j:int = 0; j < m; j++)
                {
                    if (props.start[triggers[j]] !== undefined &&
                        props.end[triggers[j]] != props.start[triggers[j]])
                    {
                        return true;
                    }
                }
            }
        }
            
        return false;
    }
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  A UIComponentDescriptor instance encapsulates the information that you
 *  specified in an MXML tag for an instance of a visual component.
 *
 *  <p>Most of the tags in an MXML file describe a tree of UIComponent objects.
 *  For example, the <code>&lt;mx:Application&gt;</code> tag represents a
 *  UIComponent object, and its child containers and controls are all
 *  UIComponent objects.</p>
 *
 *  <p>The MXML compiler compiles each of these MXML tags into a
 *  UIComponentDescriptor instance.
 *  To be precise, the MXML compiler autogenerates an ActionScript
 *  data structure which is a tree of UIComponentDescriptor objects.</p>
 *
 *  <p>At runtime, the <code>createComponentsFromDescriptors()</code> method
 *  of the Container class uses the information in the UIComponentDescriptor
 *  objects in the container's <code>childDescriptors</code> array to create
 *  the actual UIComponent objects that are the container's children,
 *  plus deeper descendants as well.
 *  Depending on the value of the container's <code>creationPolicy</code>,
 *  property, the descendants might be created at application startup,
 *  when some part of the component is about to become visible,
 *  or when the application developer manually calls
 *  the <code>createComponentsFromDescriptors()</code> method.</p>
 *
 *  <p>You do not typically create UIComponentDescriptor instances yourself;
 *  you can access the ones that the MXML compiler autogenerates via the
 *  <code>childDescriptors</code> array of the Container class.</p>
 *
 *  @see mx.core.Container#childDescriptors
 *  @see mx.core.Container#creationPolicy
 *  @see mx.core.Container#createComponentsFromDescriptors()
 */ 
public class UIComponentDescriptor extends ComponentDescriptor
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param descriptorProperties An Object containing name/value pairs
	 *  for the  properties of the UIComponentDescriptor object, such as its
	 *  <code>type</code>, <code>id</code>, <code>propertiesFactory</code>,
	 *  <code>events</code>, <code>stylesFactory</code>,
	 *  and <code>effects</code>.
     */
    public function UIComponentDescriptor(descriptorProperties:Object)
    {
        super(descriptorProperties);
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  effects
    //----------------------------------

    /**
     *  An Array containing the effects for the component, as specified in MXML.
     *
     *  <p>For example, if you write the following code:</p>
	 *
     *  <pre>
	 *  &lt;mx:TextInput showEffect="Fade" hideEffect="Fade"/&gt;</pre>
	 *
     *  <p>The descriptor's <code>effects</code> property is the Array
     *  <code>[ "showEffect", "hideEffect" ]</code>.</p>
     *
     *  <p>The <code>effects</code>property is <code>null</code>
	 *  if no MXML effects were specified for the component.</p>
	 *
	 *  <p>Note that the values of the effect attributes are not specified
	 *  in this property.
	 *  Instead, effects are treated like styles and therefore are include
	 *  in the <code>stylesFactory</code> property.
	 *  The <code>effect</code> Array simply keeps track of which styles
	 *  in the <code>stylesFactory</code> are actually effects.</p>
	 *
     *  <p>This property is used by the Container method
	 *  <code>createComponentsFromDescriptors()</code>
	 *  to register the effects with the EffectManager.</p>
     */
    public var effects:Array;

	//----------------------------------
	//  instanceIndices
	//----------------------------------

	/**
	 *  @private
	 */
	mx_internal var instanceIndices:Array /* of int */;
	
	//----------------------------------
	//  repeaterIndices
	//----------------------------------

	/**
	 *  @private
	 */
	mx_internal var repeaterIndices:Array /* of int */;

	//----------------------------------
	//  repeaters
	//----------------------------------

	/**
	 *  @private
	 */
	mx_internal var repeaters:Array /* of Repeater */;
	
    //----------------------------------
    //  stylesFactory
    //----------------------------------

    /**
     *  A Function that constructs an Object containing name/value pairs
	 *  for the instance styles for the component, as specified in MXML.
	 *
	 *  <p>For example, if you write the following code:</p>
	 *
     *  <pre>
	 *  &lt;mx:TextInput borderColor="0x888888" color="0xDDDDDD"/&gt;</pre>
	 *
	 *  <p>Then the descriptors' <code>stylesFactory</code> property
	 *  is the Function:</p>
	 *
     *  <pre>
	 *  function():void { this.borderColor = 0x888888; this.color = 0xDDDDDD };</pre>
	 *
     *  <p>The <code>stylesFactory</code> property is <code>null</code>
	 *  if no MXML styles were specified for the component instance.</p>
     */
    public var stylesFactory:Function;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
     *  @private
	 *  Returns the string "UIComponentDescriptor_" plus the value of the 
	 *  UIComponentDescriptor object's <code>id</code> property.
	 *
	 *  @return The string "UIComponentDescriptor_" plus the value of the 
	 *  UIComponentDescriptor object's <code>id</code> property.
	 */
    override public function toString():String
    {
        return "UIComponentDescriptor_" + id;
    }
}

}

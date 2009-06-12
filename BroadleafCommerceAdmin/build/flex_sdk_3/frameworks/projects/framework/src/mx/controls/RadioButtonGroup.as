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

package mx.controls
{

import flash.events.Event;
import flash.events.EventDispatcher;
import mx.core.Application;
import mx.core.IFlexDisplayObject;
import mx.core.IMXMLObject;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;

use namespace mx_internal;

//--------------------------------------
//  Events
//-------------------------------------- 

/**
 *  Dispatched when the value of the selected RadioButton control in 
 *  this group changes.
 *
 *  @eventType flash.events.Event.CHANGE
 */
[Event(name="change", type="flash.events.Event")]

/**
 *  Dispatched when a user selects a RadioButton control in the group.
 *  You can also set a handler for individual RadioButton controls.
 *
 *  @eventType mx.events.ItemClickEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="mx.events.ItemClickEvent")]

//--------------------------------------
//  Other metadata
//-------------------------------------- 

[DefaultBindingProperty(source="selection", destination="selection")]

[DefaultTriggerEvent("change")]

[IconFile("RadioButtonGroup.png")]

/**
 *  The RadioButtonGroup control defines a group of RadioButton controls
 *  that act as a single mutually exclusive control; therefore, 
 *  a user can select only one RadioButton control at a time.
 *  The <code>id</code> property is required when you use the
 *  <code>&lt;mx:RadioButtonGroup&gt;</code> tag to define the name
 *  of the group.
 * 
 *  <p>Notice that the RadioButtonGroup control is a subclass of EventDispatcher, not UIComponent, 
 *  and implements the IMXMLObject interface. 
 *  All other Flex visual components are subclasses of UIComponent, which implements 
 *  the IUIComponent interface. 
 *  The RadioButtonGroup control has support built into the Flex compiler 
 *  that allows you to use the RadioButtonGroup control as a child of a Flex container, 
 *  event though it does not implement IUIComponent. 
 *  All other container children must implement the IUIComponent interface.</p>
 *
 *  <p>Therefore, if you try to define a visual component as a subclass of 
 *  EventDispatcher that implements the IMXMLObject interface, 
 *  you will not be able to use it as the child of a container.</p>
 *  
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:RadioButtonGroup&gt;</code> tag inherits all of the
 *  tag attributes of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:RadioButtonGroup
 *    <strong>Properties</strong>
 *    enabled="true|false"      
 *    id="<i>No default</i>"
 *    labelPlacement="right|left|top|bottom"
 *  
 *    <strong>Events</strong>
 *    change="<i>No default</i>"
 *    itemClick="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/RadioButtonGroupExample.mxml
 *  
 *  @see mx.controls.RadioButton
 */
public class RadioButtonGroup extends EventDispatcher implements IMXMLObject
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
     *  @param document In simple cases where a class extends EventDispatcher, 
     *  the <code>document</code> parameter should not be used.
     *
     *  @see flash.events.EventDispatcher
     */
    public function RadioButtonGroup(document:IFlexDisplayObject = null)
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The document containing a reference to this RadioButtonGroup.
     */
    private var document:IFlexDisplayObject;

    /**
     *  @private
     *  An Array of the RadioButtons that belong to this group.
     */
    private var radioButtons:Array /* of RadioButton */ = [];

    /**
     *  @private
     *  Index for the next RadioButton added to this group.
     */
    private var indexNumber:int = 0;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  enabled
    //----------------------------------

    [Bindable("enabledChanged")]
    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Determines whether selection is allowed.
     *  
     *  @default true
     */
    public function get enabled():Boolean
    {
        var s:Number = 0;
        
        var n:int = numRadioButtons;
        for (var i:int = 0; i < n; i++)
        {
            s = s + getRadioButtonAt(i).enabled;
        }
        
        if (s == 0)
            return false;

        if (s == n)
            return true;

        return false;
    }

    /**
     *  @private
     */
    public function set enabled(value:Boolean):void
    {
        var n:int = numRadioButtons;
        for (var i:int = 0; i < n; i++)
        {
            getRadioButtonAt(i).enabled = value;
        }

        dispatchEvent(new Event("enabledChanged"));
    }

    //----------------------------------
    //  labelPlacement
    //----------------------------------

    /**
     *  @private
     *  Storage for the labelPlacement property.
     */
    private var _labelPlacement:String = "right";

    [Bindable("labelPlacementChanged")]
    [Inspectable(category="General", enumeration="left,right,top,bottom", defaultValue="right")]

    /**
     *  Position of the RadioButton label relative to the RadioButton icon
     *  for each control in the group.
     *  You can override this setting for the individual controls.
     *
     *  <p>Valid values in MXML are <code>"right"</code>, <code>"left"</code>, 
     *  <code>"bottom"</code>, and <code>"top"</code>. </p>
     *
     *  <p>In ActionScript, you use the following constants to set this property:
     *  <code>ButtonLabelPlacement.RIGHT</code>, <code>ButtonLabelPlacement.LEFT</code>,
     *  <code>ButtonLabelPlacement.BOTTOM</code>, and <code>ButtonLabelPlacement.TOP</code>.</p>
     *
     *  @default "right" 
     */
    public function get labelPlacement():String
    {
        return _labelPlacement;
    }

    /**
     *  @private
     */
    public function set labelPlacement(value:String):void
    {
        _labelPlacement = value;

        var n:int = numRadioButtons;
        for (var i:int = 0; i < n; i++)
        {
            getRadioButtonAt(i).labelPlacement = value;
        }
    }

    //----------------------------------
    //  numRadioButtons
    //----------------------------------
    
    [Bindable("numRadioButtonsChanged")]

    /**
     *  The number of RadioButtons that belong to this RadioButtonGroup.
     * 
     *  @default "undefined"
     */
    public function get numRadioButtons():int
    {
        return radioButtons.length;
    }

    //----------------------------------
    //  selectedValue
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedValue property.
     */
    private var _selectedValue:Object;
    
    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General")]

    /**
     *  The value of the <code>value</code> property of the selected
     *  RadioButton control in the group, if this has been set
     *  to be something other than <code>null</code> (the default value).
     *  Otherwise, <code>selectedValue</code> is the value of the
     *  <code>label</code> property of the selected RadioButton.
     *  If no RadioButton is selected, this property is <code>null</code>.
     *
     *  <p>If you set <code>selectedValue</code>, Flex selects the
     *  RadioButton control whose <code>value</code> or
     *  <code>label</code> property matches this value.</p>
     *
     *  @default null
     */
    public function get selectedValue():Object
    {
        if (selection)
        {
            return selection.value != null ?
                   selection.value :
                   selection.label;
        }

        return null;
    }

    /**
     *  @private.
     */
    public function set selectedValue(value:Object):void
    {
        _selectedValue = value;

        var n:int = numRadioButtons;
        for (var i:int = 0; i < n; i++)
        {
            var radioButton:RadioButton = getRadioButtonAt(i);
            if (radioButton.value == value ||
                radioButton.label == value)
            {
                changeSelection(i, false);
                break;
            }
        }

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  selection
    //----------------------------------

    /**
     *  @private
     *  Reference to the selected radio button.
     */
    private var _selection:RadioButton;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General")]

    /**
     *  Contains a reference to the currently selected
     *  RadioButton control in the group. 
     *  You can access the property in ActionScript only;
     *  it is not settable in MXML. 
     *  Setting this property to <code>null</code> deselects the currently selected RadioButton control. 
     *
     *  @default null 
     */
    public function get selection():RadioButton
    {
        return _selection;
    }

    /**
     *  @private
     */
    public function set selection(value:RadioButton):void
    {
        // Going through the selection setter should never fire a change event.
        setSelection(value, false); 
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Implementation of the <code>IMXMLObject.initialized()</code> method 
     *  to support deferred instantiation.
     *
     *  @param document The MXML document that created this object.
     *
     *  @param id The identifier used by document to refer to this object. 
     *  If the object is a deep property on document, <code>id</code> is null.
     * 
     *  @see mx.core.IMXMLObject
     */
    public function initialized(document:Object, id:String):void
    {
        this.document = document ?
                        IFlexDisplayObject(document) :
                        IFlexDisplayObject(Application.application);
    }
    
    /**
     *  Returns the RadioButton control at the specified index.
     *
     *  @param index The index of the RadioButton control in the 
     *  RadioButtonGroup control, where the index of the first control is 0.
     *
     *  @return The specified RadioButton control.
     */
    public function getRadioButtonAt(index:int):RadioButton
    {
        return RadioButton(radioButtons[index]);
    }

    /**
     *  @private
     *  Add a radio button to the group.
     */
    mx_internal function addInstance(instance:RadioButton):void
    {
        instance.indexNumber = indexNumber++;
        instance.addEventListener(Event.REMOVED, radioButton_removedHandler);    
        radioButtons.push(instance);

        if (_selectedValue != null)
            selectedValue = _selectedValue;
           
		dispatchEvent(new Event("numRadioButtonsChanged"));
    }

    /**
     *  @private
     *  Remove a radio button from the group.
     */
    mx_internal function removeInstance(instance:RadioButton):void
    {
        if (instance)
        {
        
            var foundInstance:Boolean = false;
            for (var i:int = 0; i < numRadioButtons; i++)
            {
                var rb:RadioButton = getRadioButtonAt(i);
                
                if (foundInstance)
                {
                    // Decrement the indexNumber for each button after the removed button.
                    rb.indexNumber--;
                }
                else if (rb == instance)
                {
                	rb.group = null;
                	
                    if (instance == _selection)
                    {
                        _selection = null;
                    }
                    // Remove the radio button from the internal array
                    radioButtons.splice(i,1); 
                    foundInstance = true;
                    indexNumber--;
                    // redo the same index because we removed the previous item at this index
                    i--; 
                }
            }  
            
            if (foundInstance)
				dispatchEvent(new Event("numRadioButtonsChanged"));
        }
    }
    
    /**
     *  @private
     *  Return the value or the label value
     *  of the selected radio button.
     */
    private function getValue():String
    {
        if (selection)
        {
            return selection.value && 
                   selection.value is String && 
                   String(selection.value).length != 0 ?
                   String(selection.value) :
                   selection.label;
        }
        else
        {
            return null;
        }
    }

    /**
     *  @private
     */
    mx_internal function setSelection(value:RadioButton, fireChange:Boolean = true):void
    {
        if (value == null && _selection != null)
        {
            _selection.selected = false;
            _selection = null;
            if (fireChange)
                dispatchEvent(new Event(Event.CHANGE));
        }
        else
        {       
            var n:int = numRadioButtons;
            for (var i:int = 0; i < n; i++)
            {
                if (value == getRadioButtonAt(i))
                {
                    changeSelection(i, fireChange);
                    break;
                }
            }
        }

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }
        
    /**
     *  @private
     */
    private function changeSelection(index:int, fireChange:Boolean = true):void
    {
        if (getRadioButtonAt(index))
        {
            // Unselect the currently selected radio
            if (selection)
                selection.selected = false;

            // Change the focus to the new radio.
            // Set the state of the new radio to true.
            // Fire a click event for the new radio.
            // Fire a click event for the radio group.
            _selection = getRadioButtonAt(index);
            _selection.selected = true;
            if (fireChange)
                dispatchEvent(new Event(Event.CHANGE));
        }
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event Handlers
    //
    //--------------------------------------------------------------------------
     /**
     *  @private
     */
    private function radioButton_removedHandler(event:Event):void
    {
        var rb:RadioButton = event.target as RadioButton;
        if (rb)
        {
        	rb.removeEventListener(Event.REMOVED, radioButton_removedHandler);
            removeInstance(RadioButton(event.target));
        }
    }
}

}

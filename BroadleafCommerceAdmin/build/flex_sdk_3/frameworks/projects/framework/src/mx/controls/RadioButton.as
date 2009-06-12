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
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.core.FlexVersion;
import mx.events.ItemClickEvent;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerGroup;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/IconColorStyles.as"

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="emphasized", kind="property")]
[Exclude(name="toggle", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.RadioButtonAccImpl")]

[DefaultBindingProperty(source="selected", destination="selected")]

[DefaultTriggerEvent("click")]

[IconFile("RadioButton.png")]

/**
 *  The RadioButton control lets the user make a single choice
 *  within a set of mutually exclusive choices.
 *  A RadioButton group is composed of two or more RadioButton controls
 *  with the same <code>groupName</code> property.
 *  The RadioButton group can refer to a group created by the
 *  <code>&lt;mx:RadioButtonGroup&gt;</code> tag.
 *  The user selects only one member of the group at a time.
 *  Selecting an unselected group member deselects the currently selected
 *  RadioButton control within that group.
 *
 *  <p>The RadioButton control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Wide enough to display the text label of the control</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>Undefined</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:RadioButton&gt;</code> tag inherits all of the tag
 *  attributes of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:RadioButton
 *    <strong>Properties</strong>
 *    groupName=""
 *    labelPlacement="right|left|top|bottom"
 *  
 *    <strong>Styles</strong>
 *    disabledIconColor="0x999999"
 *    iconColor="0x2B333C"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/RadioButtonExample.mxml
 *
 *  @see mx.controls.RadioButtonGroup
 */
public class RadioButton extends Button implements IFocusManagerGroup
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by RadioButtonAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function RadioButton()
    {
        super();

        // Button variables.
        _labelPlacement = "";
        _toggle = true;
        
        groupName = "radioGroup";
        
        addEventListener(FlexEvent.ADD, addHandler);
        
        // Old padding logic variables
        centerContent = false;
        extraSpacing = 8;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Default inital index value
     */
    mx_internal var indexNumber:int = 0;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  emphasized
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  A RadioButton doesn't have an emphasized state, so _emphasized
     *  is set false in the constructor and can't be changed via this setter.
     */
    override public function get emphasized():Boolean
    {
        return false;
    }

    //----------------------------------
    //  labelPlacement
    //----------------------------------

    [Bindable("labelPlacementChanged")]
    [Inspectable(category="General", enumeration="left,right,top,bottom", defaultValue="right")]

    /**
     *  Position of the label relative to the RadioButton icon.
     *  Valid values in MXML are <code>"right"</code>, <code>"left"</code>,
     *  <code>"bottom"</code>, and <code>"top"</code>.
     *
     *  <p>In ActionScript, you use the following constants
     *  to set this property:
     *  <code>ButtonLabelPlacement.RIGHT</code>,
     *  <code>ButtonLabelPlacement.LEFT</code>,
     *  <code>ButtonLabelPlacement.BOTTOM</code>, and
     *  <code>ButtonLabelPlacement.TOP</code>.</p>
     *
     *  @default ButtonLabelPlacement.RIGHT
     */
    override public function get labelPlacement():String
    {
        var value:String = ButtonLabelPlacement.RIGHT;

        if (_labelPlacement != "")
            value = _labelPlacement;
        else if (_group && _group.labelPlacement != "")
            value = _group.labelPlacement;

        return value;
    }

    //----------------------------------
    //  toggle
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  A RadioButton is always toggleable by definition, so toggle is set
     *  true in the constructor and can't be changed for a RadioButton.
     */

    override public function get toggle():Boolean
    {
        return super.toggle;
    }

    /**
     *  @private
     */
    override public function set toggle(value:Boolean):void
    {
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  group
    //----------------------------------

    /**
     *  @private
     *  Storage for the group property.
     */
    private var _group:RadioButtonGroup;

    /**
     *  The RadioButtonGroup object to which this RadioButton belongs.
     *
     *  @default "undefined"
     */
    public function get group():RadioButtonGroup
    {
        // Debugger asks too soon.
        if (!document)
            return _group;

        if (!_group)
        {
            if (groupName && groupName != "")
            {
                var g:RadioButtonGroup;
                try
                {
                    g = RadioButtonGroup(document[groupName]);
                }
                catch(e:Error)
                {
                    // UIComponent has a special automaticRadioButtonGroups slot.
                    if (document.automaticRadioButtonGroups &&
                        document.automaticRadioButtonGroups[groupName])
                    {
                        g = RadioButtonGroup(
                                document.automaticRadioButtonGroups[groupName]);
                    }
                }

                if (!g)
                {
                    g = new RadioButtonGroup(IFlexDisplayObject(document));
                    
                    if (!document.automaticRadioButtonGroups)
                        document.automaticRadioButtonGroups = {};
                    document.automaticRadioButtonGroups[groupName] = g;
                        
                }
                else if (!(g is RadioButtonGroup))
                {
                    return null;
                }

                _group = g;
            }
        }

        return _group;
    }

    /**
     *  @private
     */
    public function set group(value:RadioButtonGroup):void
    {
        _group = value;
    }

    //----------------------------------
    //  groupName
    //----------------------------------

    /**
     *  @private
     *  Storage for groupName property.
     */
    mx_internal var _groupName:String;

    /**
     *  @private
     */
    private var groupChanged:Boolean = false;

    [Bindable("groupNameChanged")]
    [Inspectable(category="General", defaultValue="radioGroup")]

    /**
     *  Specifies the name of the group to which this RadioButton control belongs, or 
     *  specifies the value of the <code>id</code> property of a RadioButtonGroup control
     *  if this RadioButton is part of a group defined by a RadioButtonGroup control.
     *
     *  @default "undefined"
     */
    public function get groupName():String
    {
        return _groupName;
    }

    /**
     *  @private
     */
    public function set groupName(value:String):void
    {
        // A groupName must be non-empty string.
        if (!value || value == "")
            return;

        deleteGroup(); // Delete the old group

        _groupName = value;

        groupChanged = true;

        invalidateProperties();
        invalidateDisplayList();

        dispatchEvent(new Event("groupNameChanged"));
    }

    //----------------------------------
    //  value
    //----------------------------------

    /**
     *  @private
     *  Storage for value property.
     */
    private var _value:Object;

    [Bindable("valueChanged")]
    [Inspectable(category="General", defaultValue="")]

    /**
     *  Optional user-defined value
     *  that is associated with a RadioButton control.
     * 
     *  @default null
     */
    public function get value():Object
    {
        return _value;
    }

    /**
     *  @private
     */
    public function set value(value:Object):void
    {
        _value = value;

        dispatchEvent(new Event("valueChanged"));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (RadioButton.createAccessibilityImplementation != null)
            RadioButton.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     *  Update properties before measurement/layout.
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (groupChanged)
        {
            addToGroup();

            groupChanged = false;
        }
    }

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
        {
	        var textHeight:Number = measureText(label).height;
	        var iconHeight:Number = currentIcon ? currentIcon.height : 0;
	
	        var h:Number;
	
	        if (labelPlacement == ButtonLabelPlacement.LEFT ||
	            labelPlacement == ButtonLabelPlacement.RIGHT)
	        {
	            h = Math.max(textHeight, iconHeight);
	        }
	        else
	        {
	            h = textHeight + iconHeight;
	
	            var verticalGap:Number = getStyle("verticalGap");
	            if (iconHeight != 0 && !isNaN(verticalGap))
	                h += verticalGap;
	        }
	
	        measuredMinHeight = measuredHeight = Math.max(h, 18);
	    }
    }

    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        if (groupChanged)
        {
            addToGroup();

            groupChanged = false;
        }
        if (_group && _selected && _group.selection != this)
            group.setSelection(this, false);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Create radio button group if it does not exist
     *  and add the instance to the group.
     */
    private function addToGroup():Object
    {
        var g:RadioButtonGroup = group; // Trigger getting the group
        if (g)
            g.addInstance(this);
        return g;
    }

    /**
     *  @private
     */
    mx_internal function deleteGroup():void
    {
        try
        {
            if (document[groupName])
                delete document[groupName];
        }
        catch(e:Error)
        {
            try
            {
                if (document.automaticRadioButtonGroups[groupName])
                    delete document.automaticRadioButtonGroups[groupName];
            }
            catch(e1:Error)
            {
            }
        }
    }

    /**
     *  @private
     *  Set next radio button in the group.
     */
    private function setPrev(moveSelection:Boolean = true):void
    {
        var g:RadioButtonGroup = group;

        var fm:IFocusManager = focusManager;
        if (fm)
            fm.showFocusIndicator = true;

        for (var i:int = 1; i <= indexNumber; i++)
        {
            var radioButton:RadioButton = g.getRadioButtonAt(indexNumber - i);
            if (radioButton && radioButton.enabled)
            {
                if (moveSelection)
                    g.setSelection(radioButton);
                radioButton.setFocus();
                return;
            }
        }

        if (moveSelection && g.getRadioButtonAt(indexNumber) != g.selection)
            g.setSelection(this);
        
        this.drawFocus(true);   
    }

    /**
     *  @private
     *  Set the previous radio button in the group.
     */
    private function setNext(moveSelection:Boolean = true):void
    {
        var g:RadioButtonGroup = group;

        var fm:IFocusManager = focusManager;
        if (fm)
            fm.showFocusIndicator = true;

        for (var i:int = indexNumber + 1; i < g.numRadioButtons; i++)
        {
            var radioButton:RadioButton = g.getRadioButtonAt(i);
            if (radioButton && radioButton.enabled)
            {
                if (moveSelection)
                    g.setSelection(radioButton);
                radioButton.setFocus();
                return;
            }
        }

        if (moveSelection && g.getRadioButtonAt(indexNumber) != g.selection)
            g.setSelection(this);
        this.drawFocus(true);   
    }

    /**
     *  @private
     */
    private function setThis():void
    {
        if (!_group)
            addToGroup();

        var g:RadioButtonGroup = group;
        if (g.selection != this)
            g.setSelection(this);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Support the use of keyboard within the group.
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        switch (event.keyCode)
        {
            case Keyboard.DOWN:
            {
                setNext(!event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.UP:
            {
                setPrev(!event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.LEFT:
            {
                setPrev(!event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.RIGHT:
            {
                setNext(!event.ctrlKey);
                event.stopPropagation();
                break;
            }

            case Keyboard.SPACE:
            {
                setThis();
                //disable toggling behavior for the RadioButton when
                //dealing with the spacebar since selection is maintained
                //by the group instead
                _toggle = false;
                //fall through, no break
            }

            default:
            {
                super.keyDownHandler(event);
                break;
            }
        }
    }

    /**
     *  @private
     *  Support the use of keyboard within the group.
     */
    override protected function keyUpHandler(event:KeyboardEvent):void
    {
        super.keyUpHandler(event);

        if (event.keyCode == Keyboard.SPACE && !_toggle)
        {
            //we disabled _toggle for SPACE because we don't want to allow
            //de-selection, but now it needs to be re-enabled
            _toggle = true;
        }
    }

    /**
     *  @private
     *  When we are added, make sure we are part of our group.
     */
    private function addHandler(event:FlexEvent):void
    {
        if (!_group && initialized)
            addToGroup();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: Button
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Set radio button to selected and dispatch that there has been a change.
     */
    override protected function clickHandler(event:MouseEvent):void
    {
        if (!enabled || selected)
            return; // prevent a selected button from dispatching "click"

        if (!_group)
            addToGroup();

        // Must call super.clickHandler() before setting
        // the group's selection.
        super.clickHandler(event);

        group.setSelection(this);

        // Dispatch an itemClick event from the RadioButtonGroup.
        var itemClickEvent:ItemClickEvent =
            new ItemClickEvent(ItemClickEvent.ITEM_CLICK);
        itemClickEvent.label = label;
        itemClickEvent.index = indexNumber;
        itemClickEvent.relatedObject = this;
        itemClickEvent.item = value;
        group.dispatchEvent(itemClickEvent);
    }
}

}
